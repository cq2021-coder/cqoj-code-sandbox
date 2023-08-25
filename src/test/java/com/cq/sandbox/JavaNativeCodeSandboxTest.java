package com.cq.sandbox;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.cq.sandbox.model.ExecuteCodeRequest;
import com.cq.sandbox.model.ExecuteCodeResponse;
import com.cq.sandbox.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JavaNativeCodeSandboxTest {

    @Test
    void executeCode() {
        CodeSandbox codeSandbox = new JavaNativeCodeSandbox();
        String code = ResourceUtil.readStr("testcode/Main.java", StandardCharsets.UTF_8);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest
                .builder()
                .inputList(List.of("1 2", "3 4"))
                .language(QuestionSubmitLanguageEnum.JAVA)
                .code(code)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(JSONUtil.toJsonStr(executeCodeResponse));
    }
}
