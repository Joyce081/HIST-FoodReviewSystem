package com.ren.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.ren.common.factory.NestedYamlPropertySourceFactory;

/*
 * @Author: ren
 * @Date: 2025-12-15 16:50:24
 * @LastEditTime: 2025-12-27 20:30:59
 * @LastEditors: ren
 * @Description: AI配置类
 * @FilePath: /Ren-Demo/ren-ai/src/main/java/com/ren/ai/config/AiConfig.java
 */
@Configuration
@PropertySource(value = {"classpath:/config/ai/spring-ai.yml"}, factory = NestedYamlPropertySourceFactory.class)
public class AiConfig {

    /**
     * @Author: ren
     * @Date: 2025-12-26 14:53:01
     * @description: 创建OpenAI聊天客户端
     * @return {ChatClient}
     */    
    @Bean
    public ChatClient deepSeekChatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).defaultSystem("""
            你是一个智能助手，请用中文回答。
            你的回答应该专业、准确、友好。
            """).build();
    }

}
