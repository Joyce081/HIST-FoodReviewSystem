package com.ren.ai.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ren.ai.core.domain.entity.AiChatClientConversation;
import com.ren.ai.core.domain.entity.AiChatClientConversationMessage;
import com.ren.ai.mapper.AiChatClientConversationMapper;
import com.ren.ai.mapper.AiChatClientConversationMessageMapper;
import com.ren.ai.service.AiChatClientConversationService;
import com.ren.common.core.constant.AppConstants;
import com.ren.common.utils.DateUtils;
import com.ren.common.utils.StringUtils;

import cn.hutool.core.collection.CollUtil;

@Service
public class AiChatClientConversationServiceImpl extends ServiceImpl<AiChatClientConversationMapper, AiChatClientConversation> implements AiChatClientConversationService {

    @Autowired
    private AiChatClientConversationMapper aiChatClientConversationMapper;
    @Autowired
    private AiChatClientConversationMessageMapper aiChatClientConversationMessageMapper;

    /*============================================AiChatClientConversation============================================*/

    /**
     * 添加AI对话
     *
     * @param aiChatClientConversation
     * @param createBy
     * @return long
     * @author ren 
     * @date 2025/12/19 17:12
     */
    @Override
    public void addAiChatClientConversation(AiChatClientConversation aiChatClientConversation, String createBy) {
        aiChatClientConversation.setIsDel(AppConstants.COMMON_INT_NO);
        aiChatClientConversation.setCreateBy(createBy);
        aiChatClientConversation.setCreateTime(DateUtils.currentSeconds());
        aiChatClientConversationMapper.insertAiChatClientConversation(aiChatClientConversation);
    }

    /**
     * 编辑AI对话是否删除
     *
     * @param conversationId
     * @param isDel
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    @Override
    public void modifyAiChatClientConversationIsDelById(String conversationId, int isDel, String updateBy) {
        aiChatClientConversationMapper.updateAiChatClientConversationIsDelById(conversationId,isDel,updateBy,DateUtils.currentSeconds());
    }

    /**
     * 编辑AI对话
     *
     * @param aiChatClientConversation
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    @Override
    public void modifyAiChatClientConversationById(AiChatClientConversation aiChatClientConversation, String updateBy) {
        aiChatClientConversation.setUpdateBy(updateBy);
        aiChatClientConversation.setUpdateTime(DateUtils.currentSeconds());
        aiChatClientConversationMapper.updateAiChatClientConversationById(aiChatClientConversation);
    }

    /**
     * 获取AI对话详情
     *
     * @param conversationId
     * @return com.ren.common.core.entity.AiChatClientConversation
     * @author ren
     * @date 2025/12/19 17:14
     */
    @Override
    public AiChatClientConversation getAiChatClientConversationById(String conversationId) {
        AiChatClientConversation aiChatClientConversation = aiChatClientConversationMapper.selectAiChatClientConversationById(conversationId);
        return aiChatClientConversation;
    }

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
    @Override
    public List<AiChatClientConversation> listAiChatClientConversationByParam(Long userId, String title, String model) {
        if(StringUtils.isNotBlank(title)){
            title = StringUtils.format("%%{}%%",title);
        }
        List<AiChatClientConversation> aiChatClientConversationList = aiChatClientConversationMapper.listAiChatClientConversationByParam( userId, title, model);
        return aiChatClientConversationList;
    }

    /*========================================AiChatClientConversationMessage=========================================*/

    /**
     * 添加AI对话消息
     *
     * @param aiChatClientConversationMessage
     * @param createBy
     * @return long
     * @author ren
     * @date 2025/12/19 17:12
     */
    @Override
    public long addAiChatClientConversationMessage(AiChatClientConversationMessage aiChatClientConversationMessage, String createBy) {
        //获取最大messageIndex
        AiChatClientConversationMessage lastMessage = getLastMessage(aiChatClientConversationMessage.getConversationId());
        if (lastMessage != null) {
            aiChatClientConversationMessage.setMessageIndex(lastMessage.getMessageIndex() + 1);
        } else {
            aiChatClientConversationMessage.setMessageIndex(1);
        }
        aiChatClientConversationMessage.setIsDel(AppConstants.COMMON_INT_NO);
        aiChatClientConversationMessage.setCreateBy(createBy);
        aiChatClientConversationMessage.setCreateTime(DateUtils.currentSeconds());
        aiChatClientConversationMessageMapper.insertAiChatClientConversationMessage(aiChatClientConversationMessage);
        return aiChatClientConversationMessage.getMessageId();
    }

    /**
     * 添加AI对话消息列表
     * @param createBy
     * @return
     * @author ren
     * @date 2025/12/19 17:15
     */
    @Override
    public void addAiChatClientConversationMessageList(List<AiChatClientConversationMessage> aiChatClientConversationMessages, String createBy) {
        aiChatClientConversationMessages.forEach(aiChatClientConversationMessage -> {
            aiChatClientConversationMessage.setIsDel(AppConstants.COMMON_INT_NO);
            aiChatClientConversationMessage.setCreateBy(createBy);
            aiChatClientConversationMessage.setCreateTime(DateUtils.currentSeconds());
        });
        aiChatClientConversationMessageMapper.insertAiChatClientConversationMessageList(aiChatClientConversationMessages);
    }

    /**
     * 根据消息ID删除消息
     *
     * @param messageId
     * @param isDel
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    @Override
    public void modifyAiChatClientConversationMessageIsDelByMessageId(long messageId, int isDel, String updateBy) {
        aiChatClientConversationMessageMapper.updateAiChatClientConversationMessageIsDelByMessageId(messageId,isDel,updateBy,DateUtils.currentSeconds());
    }

    /**
     * 根据对话ID删除消息
     *
     * @param conversationId
     * @param isDel
     * @param updateBy
     * @author ren
     * @date 2025/12/19 17:13
     */
    @Override
    public void modifyAiChatClientConversationMessageIsDelByConversationId(String conversationId, int isDel, String updateBy) {
        aiChatClientConversationMessageMapper.updateAiChatClientConversationMessageIsDelByConversationId(conversationId,isDel,updateBy,DateUtils.currentSeconds());
    }

    /**
     * 根据参数获取AI对话消息列表
     *
     * @param conversationId
     * @param roleList
     * @return java.util.List<com.ren.common.core.entity.AiChatClientConversationMessage>
     * @author ren
     * @date 2025/12/19 17:15
     */
    @Override
    public List<AiChatClientConversationMessage> listAiChatClientConversationMessageByParam(String conversationId, String[] roleList) {
        List<AiChatClientConversationMessage> aiChatClientConversationMessageList = aiChatClientConversationMessageMapper.listAiChatClientConversationMessageByParam(conversationId, roleList);
        return aiChatClientConversationMessageList;
    }

    /**
     * @description: 根据对话ID获取对话消息列表并排除掉指定消息
     * @return {List<AiChatClientConversationMessage>}
     * @Author: ren
     * @Date: 2025-12-30 16:30:34
     */      
    @Override
    public List<AiChatClientConversationMessage> listAiChatClientConversationMessageByExcludeMessageId(String conversationId,Long[] excludeMessageIdList) {
        List<AiChatClientConversationMessage> aiChatClientConversationMessageList = aiChatClientConversationMessageMapper.listAiChatClientConversationMessageByExcludeMessageId(conversationId, excludeMessageIdList);
        return aiChatClientConversationMessageList;
    }

    /**
     * @description: 根据对话ID获取n轮对话消息列表（一问一答为一轮）
     * @return {List<AiChatClientConversationMessage>}
     * @Author: ren
     * @Date: 2025-12-28 19:12:55
     */ 
    @Override
    public List<AiChatClientConversationMessage> listAiChatClientConversationMessageForRound(String conversationId,Long[] excludeMessageIdList, Integer roundNum) {
        // 获取轮数，默认10轮
        if(roundNum == null) roundNum = 10;

        // 获取消息列表，按index正序（最旧的在最前）
        List<AiChatClientConversationMessage> allMessages = listAiChatClientConversationMessageByExcludeMessageId(conversationId, excludeMessageIdList);
        
        // 反转，让最新的消息在最前
        List<AiChatClientConversationMessage> reversed = CollUtil.reverse(allMessages);
        
        // 结果列表
        List<AiChatClientConversationMessage> result = new ArrayList<>();
        
        // 轮数计数器
        int roundCounter = 0;
        // 索引
        int i = 0;
        
        // 遍历反转后的列表
        while (i < reversed.size() && roundCounter < roundNum) {
            AiChatClientConversationMessage message = reversed.get(i);
            
            if (AppConstants.AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_USER.equals(message.getRole())) { // 当前消息是用户消息，向后找配对的assistant消息
                if (i + 1 < reversed.size()) {
                    AiChatClientConversationMessage nextMessage = reversed.get(i + 1);
                    if (AppConstants.AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_ASSISTANT.equals(nextMessage.getRole())) {
                        // 找到了完整的一轮：user -> assistant
                        // 先添加AI消息，再添加用户消息，就能保证用户消息在AI消息之前
                        result.add(0, nextMessage);
                        result.add(0, message);
                        roundCounter++;
                        i += 2;  // 跳过这两条，继续下一轮
                        continue;
                    }
                }
                // 没有配对的assistant消息，跳过这条孤立的用户消息
                i++;
            } else { // 当前消息是assistant消息，向前找配对的user消息
                if (i + 1 < reversed.size()) {
                    AiChatClientConversationMessage nextMessage = reversed.get(i + 1);
                    if (AppConstants.AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_USER.equals(nextMessage.getRole())) {
                        // 找到了完整的一轮：user -> assistant（但顺序是反的）
                        // 先添加AI消息，再添加用户消息，就能保证用户消息在AI消息之前
                        result.add(0, message);
                        result.add(0, nextMessage);
                        roundCounter++;
                        i += 2;  // 跳过这两条
                        continue;
                    }
                }
                // 孤立的assistant消息，跳过
                i++;
            }
        }
        return result;
    }

    /**
     * @description: 根据消息ID获取消息
     * @param {long} messageId
     * @return {AiChatClientConversationMessage}
     * @Author: ren
     * @Date: 2025-12-30 16:39:40
     */ 
    @Override
    public AiChatClientConversationMessage getAiChatClientConversationMessageById(long messageId) {
        AiChatClientConversationMessage aiChatClientConversationMessage = aiChatClientConversationMessageMapper.selectAiChatClientConversationMessageById(messageId);
        return aiChatClientConversationMessage;
    }

    /**
     * @description: 根据对话ID获取最后一条消息
     * @param {String} conversationId
     * @return {AiChatClientConversationMessage}
     * @Author: ren
     * @Date: 2025-12-27 20:27:47
     */ 
    @Override
    public AiChatClientConversationMessage getLastMessage(String conversationId) {
        AiChatClientConversationMessage aiChatClientConversationMessage = aiChatClientConversationMessageMapper.selectLastMessage(conversationId);
        return aiChatClientConversationMessage;
    }

    /**
     * @description: 根据消息ID编辑消息
     * @param {AiChatClientConversationMessage} aiChatClientConversationMessage
     * @return
     * @Author: ren
     * @Date: 2025-12-30 17:01:53
     */
    @Override
    public void modifyAiChatClientConversationMessageById(
            AiChatClientConversationMessage aiChatClientConversationMessage, String username) {
        aiChatClientConversationMessage.setUpdateBy(username);
        aiChatClientConversationMessage.setUpdateTime(DateUtils.currentSeconds());
        aiChatClientConversationMessageMapper.updateAiChatClientConversationMessageById(aiChatClientConversationMessage);
    }

}