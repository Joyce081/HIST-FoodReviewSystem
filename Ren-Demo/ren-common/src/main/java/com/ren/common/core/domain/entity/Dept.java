package com.ren.common.core.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_dept")
public class Dept extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 部门ID */
    @TableId(value = "dept_id", type = IdType.AUTO)
    @Schema(description = "部门ID")
    private Long deptId;

    /** 父部门ID */
    @TableField(value = "parent_id")
    @Schema(description = "父部门ID")
    private Long parentId;

    /** 祖级列表 */
    @Schema(description = "祖级列表")
    private String ancestors;

    /** 部门名称 */
    @TableField(value = "dept_name")
    @Schema(description = "部门名称")
    private String deptName;

    /** 显示顺序 */
    @TableField(value = "order_num")
    @Schema(description = "显示顺序")
    private Integer orderNum;

    /** 负责人 */
    @Schema(description = "负责人")
    private String leader;

    /** 联系电话 */
    @Schema(description = "联系电话")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 是否停用（0：否 1：是） */
    @TableField(value = "is_stop")
    @Schema(description = "是否停用",type = "integer",allowableValues = {"0", "1"})
    private Integer isStop;

    /** 是否删除（0：否 1：是） */
    @TableField(value = "is_del")
    @Schema(description = "是否删除",type = "integer",allowableValues = {"0", "1"})
    private Integer isDel;

    /*==================================================以下为冗余字段===================================================*/

    /** 父部门名称 */
    @TableField(exist = false)
    @Schema(hidden = true)
    private String parentName;

    /** 子部门 */
    @TableField(exist = false)
    @Schema(hidden = true)
    private List<Dept> children = new ArrayList<Dept>();

}
