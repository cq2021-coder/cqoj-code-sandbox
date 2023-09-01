package com.cq.sandbox.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cq.sandbox.model.ExecuteCodeRequest;
import com.cq.sandbox.model.ExecuteCodeResponse;
import com.cq.sandbox.model.ExecuteMessage;
import com.cq.sandbox.model.JudgeInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public abstract class CodeSandboxTemplate implements CodeSandbox {

    /**
     * 保存代码到文件中，注意这里需要实现，不同编程语言要放到不同文件夹中
     * 保存到文件中的格式应为: UUID/代码文件，后面删除代码文件需要将代码文件的父文件删除
     *
     * @param code 代码
     * @return {@link File}
     */
    public abstract File saveCodeToFile(String code);

    /**
     * 编译代码，注意编译代码要返回编译的信息
     *
     * @param userCodePath 代码目录
     * @return {@link ExecuteMessage}
     * @throws IOException IOException
     */
    public abstract ExecuteMessage compileCode(String userCodePath) throws IOException;

    /**
     * 运行代码
     *
     * @param inputList          输入用例
     * @param userCodeParentPath 代码父目录
     * @return {@link List}<{@link ExecuteMessage}>
     * @throws RuntimeException RuntimeException
     */
    public abstract List<ExecuteMessage> runCode(List<String> inputList, String userCodeParentPath) throws RuntimeException;


    @Override
    public final ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        // 保存代码
        File userCodeFile = saveCodeToFile(code);
        String userCodePath = userCodeFile.getAbsolutePath();
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();

        // 编译代码
        try {
            ExecuteMessage executeMessage = compileCode(userCodePath);
            if (executeMessage.getExitCode() != 0) {
                FileUtil.del(userCodeParentPath);
                return ExecuteCodeResponse
                        .builder()
                        .status(2)
                        .message("编译错误")
                        .build();
            }
        } catch (IOException e) {
            FileUtil.del(userCodeParentPath);
            return errorResponse(e);
        }

        // 执行代码
        try {
            List<ExecuteMessage> executeMessageList = runCode(inputList, userCodeParentPath);
            // 返回处理结果
            ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
            executeCodeResponse.setStatus(1);
            JudgeInfo judgeInfo = new JudgeInfo();
            executeCodeResponse.setJudgeInfo(judgeInfo);
            List<String> outputList = new LinkedList<>();
            long maxTime = 0;

            for (ExecuteMessage executeMessage : executeMessageList) {
                if (ObjectUtil.equal(0, executeMessage.getExitCode())) {
                    outputList.add(executeMessage.getMessage());
                } else {
                    executeCodeResponse.setMessage(executeMessage.getErrorMessage());
                    executeCodeResponse.setStatus(3);
                    break;
                }
                maxTime = Math.max(maxTime, executeMessage.getTime());
            }
            judgeInfo.setTime(maxTime);
            executeCodeResponse.setOutputList(outputList);
            FileUtil.del(userCodeParentPath);
            return executeCodeResponse;
        } catch (RuntimeException e) {
            FileUtil.del(userCodeParentPath);
            return errorResponse(e);
        }
    }

    final ExecuteCodeResponse errorResponse(Throwable e) {
        return ExecuteCodeResponse
                .builder()
                .outputList(new ArrayList<>())
                .message(e.getMessage())
                .judgeInfo(new JudgeInfo())
                .status(2)
                .build();
    }
}
