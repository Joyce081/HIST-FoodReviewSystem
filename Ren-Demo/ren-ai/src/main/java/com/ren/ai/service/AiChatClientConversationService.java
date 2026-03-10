package com.ren.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ren.ai.core.domain.entity.AiChatClientConversation;
import com.ren.ai.core.domain.entity.AiChatClientConversationMessage;

public interface AiChatClientConversationService extends IService<AiChatClientConversation> {
    
    /*============================================AiChatClientConversation============================================*/

    /**
     * 添加AI对话
     * @param createBy
     * @return long
     * @author ren
     * @date 2025/12/19 17:12
     */
    void addAiChatClientConversation(AiChatClientConversation aiChatClientConversation,String createBy);

    /**
     * 编辑AI对话是否删除
     * @param conversationId
     * @param isDel
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    void modifyAiChatClientConversationIsDelById(String conversationId,int isDel,String updateBy);

    /**
     * 编辑AI对话
     * @param aiChatClientConversation
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    void modifyAiChatClientConversationById(AiChatClientConversation aiChatClientConversation,String updateBy);

    /**
     * 获取AI对话详情
     * @param conversationId
     * @return com.ren.common.core.entity.AiChatClientConversation
     * @author ren
     * @date 2025/12/19 17:14
     */
    AiChatClientConversation getAiChatClientConversationById(String conversationId);

    /**
     * 根据参数获取AI对话列表
     * @return java.util.List<com.ren.common.core.entity.AiChatClientConversation>
     * @author ren
     * @date 2025/12/19 17:15
     */
    List<AiChatClientConversation> listAiChatClientConversationByParam(Long userId,String title,String model);

    /*========================================AiChatClientConversationMessage=========================================*/

    /**
     * 添加AI对话消息
     * @param createBy
     * @return long
     * @author ren
     * @date 2025/12/19 17:12
     */
    long addAiChatClientConversationMessage(AiChatClientConversationMessage aiChatClientConversation, String createBy);

    /**
     * 添加AI对话消息列表
     * @param createBy
     * @return
     * @author ren
     * @date 2025/12/19 17:15
     */
    void addAiChatClientConversationMessageList(List<AiChatClientConversationMessage> aiChatClientConversationMessages,
            String createBy);

    /**
     * 根据消息ID删除消息
     * @param messageId
     * @param isDel
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    void modifyAiChatClientConversationMessageIsDelByMessageId(long messageId,int isDel,String updateBy);

    /**
     * 根据对话ID删除消息
     * @param conversationId
     * @param isDel
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    void modifyAiChatClientConversationMessageIsDelByConversationId(String conversationId,int isDel,String updateBy);

    /**
     * 根据参数获取AI对话消息列表
     * @return java.util.List<com.ren.common.core.entity.AiChatClientConversationMessage>
     * @author ren
     * @date 2025/12/19 17:15
     */
    List<AiChatClientConversationMessage> listAiChatClientConversationMessageByParam(String conversationId,String[] roleList);

    /**
     * @description: 根据对话ID获取对话消息列表并排除掉指定消息
     * @return {List<AiChatClientConversationMessage>}
     * @Author: ren
     * @Date: 2025-12-30 16:30:34
     */    
    List<AiChatClientConversationMessage> listAiChatClientConversationMessageByExcludeMessageId(String conversationId,Long[] excludeMessageIdList);

    /**
     * @description: 根据对话ID获取n轮对话消息列表（一问一答为一轮）
     * @return {List<AiChatClientConversationMessage>}
     * @Author: ren
     * @Date: 2025-12-28 19:12:55
     */    
    List<AiChatClientConversationMessage> listAiChatClientConversationMessageForRound(String conversationId,Long[] excludeMessageIdList,Integer roundNum);

    /**
     * @description: 根据消息ID获取消息
     * @param {long} messageId
     * @return {AiChatClientConversationMessage}
     * @Author: ren
     * @Date: 2025-12-30 16:39:40
     */    
    AiChatClientConversationMessage getAiChatClientConversationMessageById(long messageId);

    /**
     * @description: 根据对话ID获取最后一条消息
     * @param {String} conversationId
     * @return {AiChatClientConversationMessage}
     * @Author: ren
     * @Date: 2025-12-27 20:27:47
     */    
    AiChatClientConversationMessage getLastMessage(String conversationId);

    /**
     * @description: 根据消息ID编辑消息
     * @param {AiChatClientConversationMessage} aiChatClientConversationMessage
     * @return
     * @Author: ren
     * @Date: 2025-12-30 17:01:53
     */    
    void modifyAiChatClientConversationMessageById(AiChatClientConversationMessage aiChatClientConversationMessage,
            String username);

}