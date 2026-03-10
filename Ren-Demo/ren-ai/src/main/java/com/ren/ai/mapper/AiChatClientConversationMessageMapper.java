/*
 * @Description: 
 * @FilePath: /Ren-Demo/ren-ai/src/main/java/com/ren/ai/mapper/AiChatClientConversationMessageMapper.java
 * @Author: ren
 * @Date: 2025-12-19 10:32:50
 * @LastEditTime: 2026-01-01 20:03:33
 * @LastEditors: ren
 */
package com.ren.ai.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ren.ai.core.domain.entity.AiChatClientConversationMessage;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface AiChatClientConversationMessageMapper extends BaseMapper<AiChatClientConversationMessage> {

    /**
     * 添加AI对话消息
     *
     * @param aiChatClientConversationMessage
     * @return long
     * @author ren
     * @date 2025/12/19 17:12
     */
    void insertAiChatClientConversationMessage(AiChatClientConversationMessage aiChatClientConversationMessage);

    /**
     * 添加AI对话消息列表
     *
     * @param aiChatClientConversationMessages
     * @author ren
     * @date 2025/12/19 17:12
     */
    void insertAiChatClientConversationMessageList(List<AiChatClientConversationMessage> aiChatClientConversationMessages);

    /**
     * 根据消息ID删除消息
     *
     * @param messageId
     * @param isDel
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    void updateAiChatClientConversationMessageIsDelByMessageId(@Param("messageId") long messageId, @Param("isDel") int isDel, @Param("updateBy") String updateBy, @Param("updateTime") long updateTime);

    /**
     * 根据对话ID删除消息
     *
     * @param conversationId
     * @param isDel
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    void updateAiChatClientConversationMessageIsDelByConversationId(@Param("conversationId") String conversationId, @Param("isDel") int isDel, @Param("updateBy") String updateBy, @Param("updateTime") long updateTime);

    /**
     * 根据参数获取AI对话消息列表
     *
     * @param conversationId
     * @param roleList
     * @return java.util.List<com.ren.common.core.entity.AiChatClientConversationMessage>
     * @author ren
     * @date 2025/12/19 17:15
     */
    List<AiChatClientConversationMessage> listAiChatClientConversationMessageByParam(@Param("conversationId") String conversationId, @Param("roleList") String[] roleList);

    /**
     * 根据对话ID获取对话消息列表并排除掉指定消息
     *
     * @param conversationId
     * @param excludeMessageIdList
     * @return java.util.List<com.ren.common.core.entity.AiChatClientConversationMessage>
     * @author ren
     * @date 2025/12/30 16:30
     */
    List<AiChatClientConversationMessage> listAiChatClientConversationMessageByExcludeMessageId(@Param("conversationId") String conversationId, @Param("excludeMessageIdList") Long[] excludeMessageIdList);

    /**
     * @description: 根据消息ID获取消息
     * @param {long} messageId
     * @return {AiChatClientConversationMessage}
     * @Author: ren
     * @Date: 2026-01-01 20:03:42
     */    
    AiChatClientConversationMessage selectAiChatClientConversationMessageById(long messageId);

    /**
     * @description: 根据对话ID获取最后一条消息
     * @param {String} conversationId
     * @return {AiChatClientConversationMessage}
     * @Author: ren
     * @Date: 2025-12-27 20:33:07
     */    
    AiChatClientConversationMessage selectLastMessage(String conversationId);

    /**
     * @description: 根据消息ID编辑消息
     * @param {AiChatClientConversationMessage} aiChatClientConversationMessage
     * @return
     * @Author: ren
     * @Date: 2025-12-30 17:01:53
     */
    void updateAiChatClientConversationMessageById(AiChatClientConversationMessage aiChatClientConversationMessage);

}