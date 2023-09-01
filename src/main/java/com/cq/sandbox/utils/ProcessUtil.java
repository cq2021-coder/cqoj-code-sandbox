package com.cq.sandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.cq.sandbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 进程处理工具类
 *
 * @author 程崎
 * @since 2023/08/25
 */
@Slf4j
public class ProcessUtil {

    /**
     * 运行进行
     *
     * @param runProcess    运行进程
     * @param operationName 操作名称
     * @return {@link ExecuteMessage}
     */
    public static ExecuteMessage handleProcessMessage(Process runProcess, String operationName) {
        int exitCode;
        StringBuilder output = new StringBuilder();
        StringBuilder errorOutput = new StringBuilder();
        try {
            exitCode = runProcess.waitFor();
            if (exitCode == 0) {
                log.info(operationName + "成功");
            } else {
                log.error(operationName + "失败，错误码为: {}", exitCode);
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                String errorRunOutputLine;
                while ((errorRunOutputLine = errorBufferedReader.readLine()) != null) {
                    errorOutput.append(errorRunOutputLine);
                }
                log.error("错误输出为：{}", errorOutput);
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            String runOutputLine;
            while ((runOutputLine = bufferedReader.readLine()) != null) {
                output.append(runOutputLine);
            }
            if (StrUtil.isNotBlank(output)) {
                log.info("正常输出：{}", output);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ExecuteMessage.builder()
                .exitCode(exitCode)
                .message(output.toString())
                .errorMessage(errorOutput.toString())
                .build();
    }


    public static ExecuteMessage handleProcessInteraction(Process runProcess, String input, String operationName) {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(runProcess.getOutputStream());
        try {
            outputStreamWriter.write(input);
            outputStreamWriter.write("\n");
            outputStreamWriter.flush();
            return handleProcessMessage(runProcess, operationName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                outputStreamWriter.close();
            } catch (IOException e) {
                log.error("关闭输入流失败");
            }
        }
    }

}
