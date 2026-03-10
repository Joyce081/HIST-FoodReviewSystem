package com.ren.ai.core.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ren.common.core.domain.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @Description: 
 * @FilePath: /Ren-Demo/ren-ai/src/main/java/com/ren/ai/core/domain/entity/AiChatClientConversationMessage.java
 * @Author: ren
 * @Date: 2025-12-19 10:14:08
 * @LastEditTime: 2025-12-31 20:20:54
 * @LastEditors: ren
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ai_chat_client_conversation_message")
public class AiChatClientConversationMessage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    @TableId(value = "message_id", type = IdType.AUTO)
    @Schema(description = "消息ID")
    private Long messageId;
    /** 对话ID */
    @TableField(value = "conversation_id")
    @Schema(description = "对话ID")
    private String conversationId;
    /** 消息序号 */
    @TableField(value = "message_index")
    @Schema(description = "消息序号")
    private Integer messageIndex;
    /** 角色：0-系统 1-用户 2-AI */
    @TableField(value = "role")
    @Schema(description = "角色：system-系统 user-用户 assistant-AI tool-工具")
    private String role;
    /** 内容 */
    @TableField(value = "content")
    @Schema(description = "内容")
    private String content;
    /** 是否删除：0-否 1-是 */
    @TableField(value = "is_del")
    @Schema(description = "是否删除：0-否 1-是")
    private Integer isDel;

    /*==================================================以下为冗余字段===================================================*/
    /** 会话标题 */
    @Schema(description = "会话标题")
    @TableField(exist = false)
    private String title;
}