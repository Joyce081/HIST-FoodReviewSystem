package com.ren.system.core.domain.schemas;

import java.util.List;

import com.ren.common.core.domain.entity.Menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MenuSchemas 菜单返回类--OpenApi使用
 *
 * @author ren
 * @version 2025/07/19 16:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "菜单返回类", name = "AjaxResultForRoleMenu")
public class RoleMenuSchemas {

    @Schema(description = "状态码", example = "200")
    private Integer code;
    @Schema(description = "返回信息", example = "操作成功")
    private String msg;
    @Schema(description = "返回数据")
    private Object data;
    @Schema(description = "自定义返回数据--菜单列表")
    private List<Menu> menuList;
    @Schema(description = "自定义返回数据--菜单ID列表")
    private Long[] menuIdArr;

}
