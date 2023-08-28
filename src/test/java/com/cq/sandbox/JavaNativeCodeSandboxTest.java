package com.cq.sandbox;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.cq.sandbox.core.CodeSandbox;
import com.cq.sandbox.core.JavaNativeCodeSandbox;
import com.cq.sandbox.model.ExecuteCodeRequest;
import com.cq.sandbox.model.ExecuteCodeResponse;
import com.cq.sandbox.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

class JavaNativeCodeSandboxTest {

    @Test
    void executeCode() {
        CodeSandbox codeSandbox = new JavaNativeCodeSandbox();
        String code = ResourceUtil.readStr("testcode/Main.java", StandardCharsets.UTF_8);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest
                .builder()
                .inputList(Arrays.asList("1 2", "3 4"))
                .language(QuestionSubmitLanguageEnum.JAVA)
                .code(code)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(JSONUtil.toJsonStr(executeCodeResponse));
    }
}
