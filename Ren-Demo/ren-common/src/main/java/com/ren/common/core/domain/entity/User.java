package com.ren.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
@Schema(description = "用户模型")
public class User extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /** 对应数据库中的名称为userId，并且是主键自增 */
    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(description = "用户标识")
    private Long userId;
    /** 用户账号 */
    @Schema(description = "用户账号")
    private String username;
    /** 用户昵称 */
    @Schema(description = "用户昵称")
    private String nickname;
    /** 登陆密码 */
    @Schema(description = "登陆密码")
    private String password;
    /** 是否删除 */
    @TableField(value = "is_del")
    @Schema(description = "是否删除")
    private Integer isDel;
    /** 部门ID */
    @TableField(value = "dept_id")
    @Schema(description = "部门ID")
    private Long deptId;
    /** 用户类型（取值在字典表） */
    @TableField(value = "user_type")
    @Schema(description = "用户类型")
    private String userType;
    /** 用户邮箱 */
    @TableField(value = "email")
    @Schema(description = "用户邮箱")
    private String email;
    /** 手机号码 */
    @TableField(value = "phonenumber")
    @Schema(description = "手机号码")
    private String phonenumber;
    /** 性别 */
    @TableField(value = "sex")
    @Schema(description = "性别",type = "integer",allowableValues = {"0", "1"})
    private Integer sex;
    /** 头像地址 */
    @TableField(value = "avatar")
    @Schema(description = "手机号码")
    private String avatar;
    /** 最后登录IP */
    @TableField(value = "login_ip")
    @Schema(description = "手机号码")
    private String loginIp;
    /** 最后登录时间 (秒时间戳) */
    @TableField(value = "login_date")
    @Schema(hidden = true)
    private Long loginDate;

    /*==================================================以下为冗余字段===================================================*/
    /** 部门 */
    @TableField(exist = false)
    @Schema(hidden = true)
    private Dept dept;
    /** 角色列表 */
    @TableField(exist = false)
    @Schema(hidden = true)
    private List<Role> roleList = new ArrayList<>();
    /** 角色Id */
    @TableField(exist = false)
    @Schema(hidden = true)
    private Long[] roleIdArr;
    /** 岗位Id */
    @TableField(exist = false)
    @Schema(hidden = true)
    private Long[] postIdArr;
    /** 最后登录时间Str */
    @TableField(exist = false)
    @Schema(hidden = true)
    private String loginDateStr;

}