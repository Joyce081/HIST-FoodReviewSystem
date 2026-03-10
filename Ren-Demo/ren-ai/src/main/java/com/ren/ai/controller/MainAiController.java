package com.ren.ai.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ren.common.utils.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import com.ren.ai.core.domain.entity.AiChatClientConversation;
import com.ren.ai.core.domain.entity.AiChatClientConversationMessage;
import com.ren.ai.manager.ChatManager;
import com.ren.ai.service.AiChatClientConversationService;
import com.ren.ai.service.StreamAuthService;
import com.ren.ai.util.ChatUtil;
import com.ren.common.controller.BaseController;
import com.ren.common.core.constant.AppConstants;
import com.ren.common.core.domain.schemas.AjaxResultSchemas;
import com.ren.common.core.enums.BusinessType;
import com.ren.common.core.interfaces.OperLogAnn;
import com.ren.common.core.page.TableDataInfo;
import com.ren.common.core.response.AjaxResult;
import com.ren.common.manager.SecurityManager;
import com.ren.common.core.domain.bo.LoginUser;

import cn.hutool.core.util.ObjUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
 * @Author: ren
 * @Date: 2025-12-16 14:42:56
 * @LastEditTime: 2025-12-26 14:55:02
 * @LastEditors: ren
 * @Description: 主要AI控制器
 * @FilePath: /Ren-Demo/ren-ai/src/main/java/com/ren/ai/controller/MainAiController.java
 */
@RestController
@RequestMapping("/main/ai")
@Tag(name = "主要AI控制器", description = "主要AI控制器")
@Slf4j
public class MainAiController extends BaseController {

    @Autowired
    @Qualifier("deepSeekChatClient")
    private ChatClient deepSeekChatClient;
    @Autowired
    private ChatManager chatManager;
    @Autowired
    private AiChatClientConversationService aiChatClientConversationService;
    @Autowired
    private StreamAuthService streamAuthService;

    /**
     * @description: 主要AI聊天客户端会话列表
     * @return {AjaxResult}
     * @Author: ren
     * @Date: 2025-12-30 13:11:46
     */    
    @GetMapping("/list")
    @Operation(summary = "主要AI聊天客户端会话列表", description = "主要AI聊天客户端会话列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public AjaxResult listAiChatClientConversation() {
        List<AiChatClientConversation> aiChatClientConversationList = aiChatClientConversationService.listAiChatClientConversationByParam(SecurityManager.getUserId(), null, null);
        return success(aiChatClientConversationList);
    }

    /**
     * @description: 主要AI聊天客户端会话消息列表
     * @return {AjaxResult}
     * @Author: ren
     * @Date: 2025-12-30 13:11:46
     */    
    @GetMapping("/listMessages")
    @Operation(summary = "主要AI聊天客户端会话消息列表", description = "主要AI聊天客户端会话消息列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public AjaxResult listAiChatClientConversationMessages(String conversationId) {
        List<AiChatClientConversationMessage> aiChatClientConversationMessageList = aiChatClientConversationService.listAiChatClientConversationMessageByParam(conversationId, new String[]{AppConstants.AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_USER, AppConstants.AICHATCLIENTCONVERSATIOMESSAGEN_ROLE_ASSISTANT});
        return success(aiChatClientConversationMessageList);
    }

    /**
     * @description: 主要AI聊天客户端会话重命名
     * @return {AjaxResult}
     * @Author: ren
     * @Date: 2025-12-30 14:27:14
     */    
    @GetMapping("/renameConversation")
    @Operation(summary = "主要AI聊天客户端会话重命名", description = "主要AI聊天客户端会话重命名")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public AjaxResult renameConversation(String conversationId, String title) {
        AiChatClientConversation aiChatClientConversation = aiChatClientConversationService.getAiChatClientConversationById(conversationId);
        if (ObjUtil.isNotEmpty(aiChatClientConversation)) {
            aiChatClientConversation.setTitle(title);
            aiChatClientConversationService.modifyAiChatClientConversationById(aiChatClientConversation, SecurityManager.getUsername());
        }else{
            AiChatClientConversation aiChatClientConversationNew = new AiChatClientConversation();
            aiChatClientConversationNew.setTitle(title);
            aiChatClientConversationNew.setConversationId(conversationId);
            aiChatClientConversationNew.setUserId(SecurityManager.getUserId());
            aiChatClientConversationNew.setModel(AppConstants.AICHATCLIENTCONVERSATION_MODEL_DEEPSEEK);
            aiChatClientConversationService.addAiChatClientConversation(aiChatClientConversationNew, SecurityManager.getUsername());
        }
        return success();
    }

    /**
     * @description: 主要AI聊天客户端会话
     * @return {AjaxResult}
     * @Author: ren
     * @Date: 2025-12-30 14:30:13
     */
    @DeleteMapping("/delete")
    @OperLogAnn(title = "主要AI聊天客户端会话", businessType = BusinessType.DELETE)
    @Operation(summary = "主要AI聊天客户端会话删除", description = "主要AI聊天客户端会话删除")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult deleteAiChatClientConversation(@Schema(description = "会话标识",type = "String") String conversationId) {
        AiChatClientConversation aiChatClientConversation = aiChatClientConversationService.getAiChatClientConversationById(conversationId);
        if (ObjUtil.isNotEmpty(aiChatClientConversation)) {
            aiChatClientConversationService.modifyAiChatClientConversationIsDelById(conversationId, AppConstants.COMMON_INT_YES, SecurityManager.getUsername());
        }
        return success();
    }

    /**
     * @description: 主要AI聊天客户端会话消息添加
     * @return {AjaxResult}
     * @Author: ren
     * @Date: 2025-12-30 15:21:10
     */    
    @PostMapping("/addConversationMessage")
    @OperLogAnn(title = "主要AI聊天客户端会话消息添加", businessType = BusinessType.INSERT)
    @Operation(summary = "主要AI聊天客户端会话消息添加", description = "主要AI聊天客户端会话消息添加")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult addConversationMessage(@RequestBody(required = false) AiChatClientConversationMessage aiChatClientConversationMessage) {
        HashMap<String, Object> result = new HashMap<>();
        // 先查询会话是否存在，如果不存在则先创建会话
        AiChatClientConversation aiChatClientConversation = aiChatClientConversationService.getAiChatClientConversationById(aiChatClientConversationMessage.getConversationId());
        if (ObjUtil.isEmpty(aiChatClientConversation)) {
            AiChatClientConversation aiChatClientConversationNew = new AiChatClientConversation();
            aiChatClientConversationNew.setTitle(ChatUtil.getTitle(aiChatClientConversationMessage.getContent(), deepSeekChatClient));
            aiChatClientConversationNew.setConversationId(aiChatClientConversationMessage.getConversationId());
            aiChatClientConversationNew.setUserId(SecurityManager.getUserId());
            aiChatClientConversationNew.setModel(AppConstants.AICHATCLIENTCONVERSATION_MODEL_DEEPSEEK);
            aiChatClientConversationService.addAiChatClientConversation(aiChatClientConversationNew, SecurityManager.getUsername());
            result.put("conversation", aiChatClientConversationNew);
        }else{
            result.put("conversation", aiChatClientConversation);
        }
        // 添加会话消息
        long messageId = aiChatClientConversationService.addAiChatClientConversationMessage(aiChatClientConversationMessage, SecurityManager.getUsername());
        aiChatClientConversationMessage.setMessageId(messageId);
        result.put("message", aiChatClientConversationMessage);
        return success(result);
    }

    /**
     * @Author: ren
     * @Date: 2025-12-26 14:54:57
     * @description: 主要问答式AI
     * @return {Flux<ServerSentEvent<String>>}：经过包装的SSE格式的数据流
     * 备注：SSE有两层含义：1. Server-Sent Events，SSE 通信协议（长连接机制，基于 HTTP 的长连接，服务器可以主动推送数据）；2. SSE 数据格式（消息格式，有 comment、data、event、id、retry字段）
     */    
     @GetMapping("/mainChat")
     @Operation(summary = "主要问答式AI", description = "主要问答式AI")
     Flux<ServerSentEvent<String>> generation(long questionMessageId, long aiMessageId, String conversationId,HttpServletRequest request) {
         // 使用专门的流式认证服务
         StreamAuthService.AuthResult authResult = streamAuthService.authenticate(request);
         if (!authResult.isSuccess()) {
             return Flux.error(new RuntimeException(authResult.getErrorMessage()));
         }
         LoginUser loginUser = authResult.getLoginUser();
         log.info("用户 {} 开始AI对话，会话ID: {}, 问题消息ID: {}, AI消息ID: {}",loginUser.getUsername(), conversationId, questionMessageId, aiMessageId);
         // 业务权限验证：验证会话是否属于当前用户
         AiChatClientConversation conversation = aiChatClientConversationService.getAiChatClientConversationById(conversationId);
         if (conversation != null && !conversation.getUserId().equals(loginUser.getUserId())) {
             return Flux.error(new RuntimeException("无权访问此会话"));
         }
         // 验证消息是否属于当前用户的会话
         AiChatClientConversationMessage questionMessage = aiChatClientConversationService.getAiChatClientConversationMessageById(questionMessageId);
         AiChatClientConversationMessage aiMessage = aiChatClientConversationService.getAiChatClientConversationMessageById(aiMessageId);
         if (questionMessage == null || aiMessage == null) {
             return Flux.error(new RuntimeException("消息不存在"));
         }
         if (!questionMessage.getConversationId().equals(conversationId) || !aiMessage.getConversationId().equals(conversationId)) {
             return Flux.error(new RuntimeException("消息与会话不匹配"));
         }
         // 获取历史对话
         List<Message> history = chatManager.listMessage(conversationId, new Long[]{questionMessageId});
         // 构建消息列表（包含历史消息 + 当前用户问题）
         List<Message> messages = new ArrayList<>();
         // 添加历史消息
         if (history != null) {
             messages.addAll(history);
         }
         // 添加当前用户问题
         Message currentUserMessage = new UserMessage(questionMessage.getContent());
         messages.add(currentUserMessage);
         // 使用 StringBuilder 收集响应，用于数据库保存（线程安全，因为是单线程处理单个请求）
         StringBuilder responseBuilder = new StringBuilder();
         // 调用AI客户端进行生成并返回
         return deepSeekChatClient.prompt()
             .messages(messages)
             .stream()
             .content()
             .map(chunk -> {
                 // 收集响应片段
                 responseBuilder.append(chunk);
                 // 返回 SSE 格式
                 return ServerSentEvent.builder(chunk).build();
             })
             .concatWith(Mono.defer(() -> {
                 // 流结束后，异步保存对话
                 String aiResponse = responseBuilder.toString();
                 // AI响应不为空，先异步保存到数据库，然后返回完成信号给前台
                 if (StringUtils.isNotBlank(aiResponse)) {
                     // 异步保存，不阻塞响应
                     return Mono.fromRunnable(() -> {
                         AiChatClientConversationMessage aiChatClientConversationMessage = aiChatClientConversationService.getAiChatClientConversationMessageById(aiMessageId);
                         aiChatClientConversationMessage.setContent(aiResponse);
                         aiChatClientConversationService.modifyAiChatClientConversationMessageById(aiChatClientConversationMessage, loginUser.getUsername());
                     }).then(Mono.just(ServerSentEvent.builder("[DONE]").build()));
                 }
                 // AI响应为空，直接给前台发送完成信号
                 return Mono.just(ServerSentEvent.builder("[DONE]").build());
                 // 到这里，流结束，后台自动关闭SSE通道，前台在上面收到了[DONE]信号，知道已经完成，于是可以进行移除管理的SSE通道
             }))
             .doOnError(error -> {
                 log.error("AI调用失败, conversationId: {}, question: {}", conversationId, questionMessage.getContent(), error);
             })
             .timeout(Duration.ofSeconds(30))
             .onErrorResume(e -> {
                 log.error("流式响应异常", e);
                 return Flux.just(
                     ServerSentEvent.builder("抱歉，AI服务暂时不可用，请稍后重试。").build(),
                     ServerSentEvent.builder("[DONE]").build()
                 );
             });
     }

}