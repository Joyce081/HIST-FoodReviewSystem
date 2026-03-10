package com.ren.ai.util;

import org.springframework.ai.chat.client.ChatClient;

import com.ren.common.utils.StringUtils;

/*
 * @Description: 
 * @FilePath: /Ren-Demo/ren-ai/src/main/java/com/ren/ai/util/ChatUtil.java
 * @Author: ren
 * @Date: 2025-12-30 15:32:24
 * @LastEditTime: 2026-01-01 17:41:58
 * @LastEditors: ren
 */
public class ChatUtil{

	/**
     * @Author: ren
     * @Date: 2025-12-26 14:55:59
     * @description: 根据消息获取标题
     * @param {String} message
     * @return {String}
     */    
    public static String getTitle(String message,ChatClient chatClient){
        return chatClient.prompt()
                .user(StringUtils.format("请根据以下内容帮我生成一个对话标题：{}", message))
                .call()
                .content();
    }

}