package com.ren.system.core.domain.entity;

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
@TableName("sys_notice")
public class Notice extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    @TableId(value = "notice_id", type = IdType.AUTO)
    @Schema(description = "公告ID")
    private Long noticeId;
    /** 公告标题 */
    @TableField(value = "notice_title")
    @Schema(description = "公告标题")
    private String noticeTitle;
    /** 公告类型（1：通知 2：公告） */
    @TableField(value = "notice_type")
    @Schema(description = "公告类型",type = "integer",allowableValues = {"1", "2"})
    private Integer noticeType;
    /** 公告内容 */
    @TableField(value = "notice_content")
    @Schema(description = "公告内容")
    private String noticeContent;
    /** 是否关闭（1：是 0：否） */
    @TableField(value = "is_close")
    @Schema(description = "是否关闭",type = "integer",allowableValues = {"0", "1"})
    private Integer isClose;

}