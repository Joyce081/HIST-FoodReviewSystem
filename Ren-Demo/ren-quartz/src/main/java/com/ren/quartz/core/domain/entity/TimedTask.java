package com.ren.quartz.core.domain.entity;

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
@TableName("sys_timed_task")
@Schema(description = "定时任务")
public class TimedTask extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    @TableId(value = "task_id", type = IdType.AUTO)
    @Schema(description = "任务ID(添加时为空)")
    private Long taskId;
    /** 任务名称 */
    @TableField(value = "task_name")
    @Schema(description = "任务名称")
    private String taskName;
    /** 任务组名 */
    @TableField(value = "task_group")
    @Schema(description = "任务分组")
    private String taskGroup;
    /** 调用目标字符串 */
    @TableField(value = "invoke_target")
    @Schema(description = "调用目标字符串")
    private String invokeTarget;
    /** cron执行表达式 */
    @TableField(value = "cron_expression")
    @Schema(description = "cron执行表达式")
    private String cronExpression;
    /** 计划执行错误策略 参数取值见QuartzContents */
    @TableField(value = "misfire_policy")
    @Schema(description = "计划执行错误策略",type = "integer",allowableValues = {"0", "1", "2", "3"})
    private Integer misfirePolicy;
    /** 是否并发执行 参数取值见AppConstants */
    @TableField(value = "concurrent")
    @Schema(description = "是否并发执行",type = "integer",allowableValues = {"0", "1"})
    private Integer concurrent;
    /** 状态 参数取值见QuartzContents */
    @TableField(value = "status")
    @Schema(description = "状态",type = "integer",allowableValues = {"0", "1"})
    private Integer status;

}