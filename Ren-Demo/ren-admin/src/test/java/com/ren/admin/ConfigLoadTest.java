package com.ren.admin;

import com.ren.RenApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest(classes = RenApplication.class) // 关键配置
public class ConfigLoadTest {

    @Autowired
    private Environment environment;

    @Test
    public void testConfigLoaded() {
        // 验证 Spring AI 配置是否被加载
        String apiKey = environment.getProperty("spring.ai.openai.api-key");
        String model = environment.getProperty("spring.ai.openai.chat.options.model");

        System.out.println("API Key 配置: " + (apiKey != null ? "已加载" : "未加载"));
        System.out.println("模型配置: " + (model != null ? "已加载" : "未加载"));
    }
}
