/*
 * @Description: 
 * @FilePath: /Ren-Demo/ren-ai/src/main/java/com/ren/ai/mapper/AiChatClientConversationMapper.java
 * @Author: ren
 * @Date: 2025-12-19 10:32:17
 * @LastEditTime: 2026-01-01 20:12:07
 * @LastEditors: ren
 */
package com.ren.ai.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ren.ai.core.domain.entity.AiChatClientConversation;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface AiChatClientConversationMapper extends BaseMapper<AiChatClientConversation> {

    /**
     * 添加AI对话
     *
     * @param aiChatClientConversation
     * @return long
     * @author ren
     * @date 2025/12/19 17:12
     */
    void insertAiChatClientConversation(AiChatClientConversation aiChatClientConversation);

    /**
     * 编辑AI对话是否删除
     *
     * @param conversationId
     * @param isDel
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    void updateAiChatClientConversationIsDelById(@Param("conversationId") String conversationId, @Param("isDel") int isDel, @Param("updateBy") String updateBy, @Param("updateTime") long updateTime);

    /**
     * 编辑AI对话
     *
     * @param aiChatClientConversation
     * @author ren
     * @date 2025/12/19 17:13
     */
    void updateAiChatClientConversationById(AiChatClientConversation aiChatClientConversation);

    /**
     * 根据会话标识获取AI对话
     * @param {String} conversationId
     * @return {AiChatClientConversation}
     * @Author: ren
     * @Date: 2026-01-01 20:00:42
     */    
    AiChatClientConversation selectAiChatClientConversationById(String conversationId);

    /**
     * 根据参数获取AI对话列表
     *
     * @param userId
     * @param title
     * @param model
     * @return java.util.List<com.ren.common.core.entity.AiChatClientConversation>
     * @author ren
     * @date 2025/12/19 17:15
     */
    List<AiChatClientConversation> listAiChatClientConversationByParam(@Param("userId") Long userId, @Param("title") String title, @Param("model") String model);

}