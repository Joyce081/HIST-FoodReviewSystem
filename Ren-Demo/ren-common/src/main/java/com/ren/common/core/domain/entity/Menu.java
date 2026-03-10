package com.ren.common.core.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_menu")
public class Menu extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    @TableId(value = "menu_id", type = IdType.AUTO)
    @Schema(description = "菜单ID")
    private Long menuId;

    /** 菜单名称 */
    @TableField(value = "menu_name")
    @Schema(description = "菜单名称")
    private String menuName;

    /** 父菜单ID */
    @TableField(value = "parent_id")
    @Schema(description = "父菜单ID")
    private Long parentId;

    /** 显示顺序 */
    @TableField(value = "order_num")
    @Schema(description = "显示顺序")
    private Integer orderNum;

    /** 路由名称，默认和路由地址相同的驼峰格式（注意：因为vue3版本的router会删除名称相同路由，为避免名字的冲突，特殊情况可以自定义） */
    @TableField(value = "route_name")
    @Schema(description = "路由名称")
    private String routeName;

    /** 路由地址 */
    @Schema(description = "路由地址")
    private String path;

    /** 组件路径 */
    @Schema(description = "组件路径")
    private String component = "";

    /** 路由参数 */
    @Schema(description = "路由参数")
    private String query;

    /** 是否为外链（0是 1否） */
    @TableField(value = "is_frame")
    @Schema(description = "是否为外链",type = "integer",allowableValues = {"0", "1"})
    private Integer isFrame;

    /** 是否缓存（0缓存 1不缓存） */
    @TableField(value = "is_cache")
    @Schema(description = "是否缓存",type = "integer",allowableValues = {"0", "1"})
    private Integer isCache;

    /** 类型（M目录 C菜单 F按钮） */
    @TableField(value = "menu_type")
    @Schema(description = "类型",type = "string",allowableValues = {"M", "C", "F"})
    private String menuType;

    /** 是否隐藏（0：否 1：是） */
    @TableField(value = "is_visible")
    @Schema(description = "是否隐藏",type = "integer",allowableValues = {"0", "1"})
    private Integer isVisible;

    /** 是否停用（0：否 1：是） */
    @TableField(value = "is_stop")
    @Schema(description = "是否停用",type = "integer",allowableValues = {"0", "1"})
    private Integer isStop;

    /** 权限字符串 */
    @Schema(description = "权限字符串")
    private String perms;

    /** 菜单图标 */
    @Schema(description = "菜单图标")
    private String icon;

    /** 是否删除（0：否 1：是） */
    @TableField(value = "is_del")
    @Schema(description = "是否删除",type = "integer",allowableValues = {"0", "1"})
    private Integer isDel;

    /*==================================================以下为冗余字段===================================================*/

    /** 子菜单 */
    @TableField(exist = false)
    @Schema(hidden = true)
    private List<Menu> children = new ArrayList<Menu>();

    /** 父菜单名称 */
    @TableField(exist = false)
    @Schema(hidden = true)
    private String parentName;
}
