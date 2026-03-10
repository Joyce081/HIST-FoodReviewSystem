package com.ren.ai.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ren.ai.core.domain.entity.AiChatClientConversationMessage;
import com.ren.ai.service.AiChatClientConversationService;
import com.ren.common.core.constant.AppConstants;

/*
 * @Author: ren
 * @Date: 2025-12-18 17:09:40
 * @LastEditTime: 2025-12-26 14:45:49
 * @LastEditors: ren
 * @Description: 聊天模型记忆实现
 * @FilePath: /Ren-Demo/ren-ai/src/main/java/com/ren/ai/manager/ChatMemory.java
 */
@Component
public class ChatManager{

    @Autowired
    private AiChatClientConversationService aiChatClientConversationService;

    /**
     * @Author: ren
     * @Date: 2025-12-26 14:56:40
     * @description: 获取消息列表
     * @return {List<Message>}
     */    
    public List<Message> listMessage(String conversationId,Long[] excludeMessageIdList) {
        List<AiChatClientConversationMessage> aiChatClientConversationMessageList = aiChatClientConversationService.listAiChatClientConversationMessageForRound(conversationId, excludeMessageIdList, null);
        List<Message> messageList = aiChatClientConversationMessageList.stream().map(this::convertToAiMessage).collect(Collectors.toList());
        return messageList;
    }

    /*=========================================================以下是工具方法===========================================================*/

    /**
     * @Author: ren
     * @Date: 2025-12-26 14:57:28
     * @description: 组装SpringAI需要的消息
     * @param {AiChatClientConversationMessage} aiMessage
     * @return {Message}
     */    
    private Message convertToAiMessage(AiChatClientConversationMessage aiMessage) {
        if (aiMessage == null || aiMessage.getContent() == null) {
            return null;
        }
        String role = aiMessage.getRole();
        String content = aiMessage.getContent();
        // 根据角色创建不同类型的Message
        return switch (role) {
            case AppConstants.AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_SYSTEM -> 
                createSystemMessage(content);
            case AppConstants.AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_USER -> 
                createUserMessage(content);
            case AppConstants.AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_ASSISTANT -> 
                createAssistantMessage(content);
            default -> null;
        };
    }

    /**
     * @Author: ren
     * @Date: 2025-12-26 14:57:50
     * @description: 创建系统消息（为了后续功能扩展，单独拿出来一个方法）
     * @param {String} content
     * @return {SystemMessage}
     */    
    private SystemMessage createSystemMessage(String content) {
        SystemMessage systemMessage = new SystemMessage(content);
        return systemMessage;
    }

    /**
     * @Author: ren
     * @Date: 2025-12-26 14:58:02
     * @description: 创建用户消息（为了后续功能扩展，单独拿出来一个方法）
     * @param {String} content
     * @return {UserMessage}
     */    
    private UserMessage createUserMessage(String content) {
        UserMessage userMessage = new UserMessage(content);
        return userMessage;
    }

    /**
     * @Author: ren
     * @Date: 2025-12-26 14:58:12
     * @description: 创建助理消息（为了后续功能扩展，单独拿出来一个方法）
     * @param {String} content
     * @return {AssistantMessage}
     */    
    private AssistantMessage createAssistantMessage(String content) {
        AssistantMessage assistantMessage = new AssistantMessage(content);
        return assistantMessage;
    }

}
