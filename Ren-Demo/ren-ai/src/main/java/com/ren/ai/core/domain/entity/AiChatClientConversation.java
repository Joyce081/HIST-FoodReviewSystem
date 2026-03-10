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

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ai_chat_client_conversation")
public class AiChatClientConversation extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 对话Id */
    @TableId(value = "conversation_id", type = IdType.AUTO)
    @Schema(description = "对话Id")
    private String conversationId;
    /** 用户Id */
    @TableField(value = "user_id")
    @Schema(description = "用户Id")
    private Long userId;
    /** 对话标题 */
    @TableField(value = "title")
    @Schema(description = "对话标题")
    private String title;
    /** 所用大模型 */
    @TableField(value = "model")
    @Schema(description = "所用大模型")
    private Integer model;
    /** 是否删除：0-否 1-是 */
    @TableField(value = "is_del")
    @Schema(description = "是否删除：0-否 1-是")
    private Integer isDel;

}