package com.cq.sandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cq.sandbox.model.ExecuteCodeRequest;
import com.cq.sandbox.model.ExecuteCodeResponse;
import com.cq.sandbox.model.ExecuteMessage;
import com.cq.sandbox.model.JudgeInfo;
import com.cq.sandbox.model.enums.QuestionSubmitLanguageEnum;
import com.cq.sandbox.utils.ProcessUtil;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;


/**
 * java本机代码沙箱
 *
 * @author 程崎
 * @since 2023/08/21
 */
@Slf4j
public class JavaNativeCodeSandbox implements CodeSandbox {
    private static final String PREFIX = File.separator + "java";

    private static final String GLOBAL_CODE_DIR_PATH = File.separator + "tempCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = File.separator + "Main.java";


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        QuestionSubmitLanguageEnum language = executeCodeRequest.getLanguage();
        String userDir = System.getProperty("user.dir");
        String globalCodePath = userDir + GLOBAL_CODE_DIR_PATH;
        if (!FileUtil.exist(globalCodePath)) {
            FileUtil.mkdir(globalCodePath);
        }

        // 存放用户代码
        String userCodeParentPath = globalCodePath + PREFIX + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + GLOBAL_JAVA_CLASS_NAME;
        FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);


        // 编译代码
        try {
            String compileCmd = String.format("javac -encoding utf-8 %s", userCodePath);
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtil.handleProcessMessage(compileProcess, "编译");
        } catch (IOException e) {
            return errorResponse(e);
        }
        // 执行代码
        String compileCmd = String.format("java -Dfile.encoding=UTF-8 -cp %s Main", userCodeParentPath);
        List<ExecuteMessage> executeMessageList = new LinkedList<>();
        long maxTime = 0;
        for (String input : inputList) {
            Process compileProcess;
            try {
                compileProcess = Runtime.getRuntime().exec(compileCmd);
            } catch (IOException e) {
                return errorResponse(e);
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            executeMessageList.add(ProcessUtil.handleProcessInteraction(compileProcess, input, "运行"));
            stopWatch.stop();
            maxTime = Math.max(stopWatch.getLastTaskTimeMillis(), maxTime);
        }
        FileUtil.del(userCodeParentPath);

        // 返回处理结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setStatus(1);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        List<String> outputList = new LinkedList<>();

        for (ExecuteMessage executeMessage : executeMessageList) {
            if (ObjectUtil.equal(0, executeMessage.getExitCode())) {
                outputList.add(executeMessage.getMessage());
            }else {
                executeCodeResponse.setMessage(executeMessage.getErrorMessage());
                executeCodeResponse.setStatus(3);
                break;
            }
        }
        executeCodeResponse.setOutputList(outputList);
        return executeCodeResponse;
    }

    private ExecuteCodeResponse errorResponse(Throwable e) {
        return ExecuteCodeResponse
                .builder()
                .outputList(new ArrayList<>())
                .message(e.getMessage())
                .judgeInfo(new JudgeInfo())
                .status(2)
                .build();
    }
}
