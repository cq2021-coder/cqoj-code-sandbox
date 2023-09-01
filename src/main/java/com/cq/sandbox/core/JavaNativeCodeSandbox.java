package com.cq.sandbox.core;

import cn.hutool.core.io.FileUtil;
import com.cq.sandbox.model.ExecuteMessage;
import com.cq.sandbox.utils.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


/**
 * java本机代码沙箱
 *
 * @author 程崎
 * @since 2023/08/21
 */
@Slf4j
public class JavaNativeCodeSandbox extends CodeSandboxTemplate {
    private static final String PREFIX = File.separator + "java";

    private static final String GLOBAL_CODE_DIR_PATH = File.separator + "tempCode";

    private static final String GLOBAL_JAVA_CLASS_NAME = File.separator + "Main.java";
    /**
     * 超时时间，超过10秒则结束
     */
    public static final Long DEFAULT_TIME_OUT = 10000L;


    @Override
    public File saveCodeToFile(String code) {
        String globalCodePath = System.getProperty("user.dir") + GLOBAL_CODE_DIR_PATH;
        if (!FileUtil.exist(globalCodePath)) {
            FileUtil.mkdir(globalCodePath);
        }

        // 存放用户代码
        String userCodeParentPath = globalCodePath + PREFIX + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + GLOBAL_JAVA_CLASS_NAME;
        return FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
    }

    @Override
    public ExecuteMessage compileCode(String userCodePath) throws IOException {
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodePath);
        Process compileProcess = Runtime.getRuntime().exec(compileCmd);
        return ProcessUtil.handleProcessMessage(compileProcess, "编译");
    }

    @Override
    public List<ExecuteMessage> runCode(List<String> inputList, String userCodeParentPath) throws RuntimeException {
        String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main", userCodeParentPath);
        List<ExecuteMessage> executeMessageList = new LinkedList<>();
        for (String input : inputList) {
            Process runProcess;
            try {
                runProcess = Runtime.getRuntime().exec(runCmd);
                new Thread(() -> {
                    try {
                        Thread.sleep(DEFAULT_TIME_OUT);
                        log.info("超时了，中断");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            ExecuteMessage executeMessage = ProcessUtil.handleProcessInteraction(runProcess, input, "运行");
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
            executeMessageList.add(executeMessage);
        }
        return executeMessageList;
    }
}
