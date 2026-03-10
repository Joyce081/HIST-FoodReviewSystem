package com.ren.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
public class BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 创建者 */
    @TableField(value = "create_by")
    @Schema(hidden = true)
    private String createBy;
    /** 创建时间 (秒时间戳) */
    @TableField(value = "create_time")
    @Schema(hidden = true)
    private Long createTime;
    /** 更新者 */
    @TableField(value = "update_by")
    @Schema(hidden = true)
    private String updateBy;
    /** 更新时间 (秒时间戳) */
    @TableField(value = "update_time")
    @Schema(hidden = true)
    private Long updateTime;
    /** 备注 */
    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;

    /*==================================================以下为冗余字段===================================================*/
    /** 请求参数 */
    @TableField(exist = false)
    //@JsonInclude(JsonInclude.Include.NON_EMPTY)表示在进行序列化时，如果对象属性值为NULL或空字符串或列表集合长度为0，则忽略该属性。
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;
    /** 创建时间Str */
    @TableField(exist = false)
    @Schema(hidden = true)
    private String createTimeStr;
    /** 更新时间Str */
    @TableField(exist = false)
    @Schema(hidden = true)
    private String updateTimeStr;
}