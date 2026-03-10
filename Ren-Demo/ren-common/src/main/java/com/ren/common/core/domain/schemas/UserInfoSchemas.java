package com.ren.common.core.domain.schemas;

import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.vo.DynamicRouteVO;
import com.ren.common.core.domain.vo.MenuVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * UserInfoSchemas 用户信息返回模型--OpenApi使用
 *
 * @author ren
 * @version 2025/07/19 16:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户信息返回模型", name = "AjaxResultForUserInfo")
public class UserInfoSchemas {

    @Schema(description = "状态码", example = "200")
    private Integer code;
    @Schema(description = "返回信息", example = "操作成功")
    private String msg;
    @Schema(description = "返回数据")
    private Object data;
    @Schema(description = "用户数据")
    private LoginUser user;
    @Schema(description = "是否超级管理员",example = "1")
    private Integer isAdmin;
    @Schema(description = "角色数组")
    private String[] roles;
    @Schema(description = "角色ID数组")
    private Long[] roleIds;
    @Schema(description = "权限数组")
    private String[] permissions;
    @Schema(description = "菜单列表")
    private List<MenuVO> menus;
    @Schema(description = "路由列表")
    private List<DynamicRouteVO> dynamicRoutes;

}
