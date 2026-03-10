package com.ren.admin.controller.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ren.common.controller.BaseController;
import com.ren.common.core.constant.AppConstants;
import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.entity.Menu;
import com.ren.common.core.domain.schemas.AjaxResultSchemas;
import com.ren.common.core.domain.vo.TreeSelectVO;
import com.ren.common.core.enums.BusinessType;
import com.ren.common.core.interfaces.OperLogAnn;
import com.ren.common.core.response.AjaxResult;
import com.ren.common.utils.TreeUtils;
import com.ren.system.core.domain.entity.RoleMenu;
import com.ren.system.core.domain.schemas.RoleMenuSchemas;
import com.ren.system.service.MenuService;
import com.ren.system.service.RoleMenuService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/menu")
@Tag(name = "菜单相关", description = "菜单相关")
public class MenuController extends BaseController {

    @Autowired
    MenuService menuService;
    @Autowired
    RoleMenuService roleMenuService;

    /**
     * 菜单树形列表（页面显示用）
     * @param searchLike
     * @return com.ren.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/08 17:14
     */
    @GetMapping("/tree")
    @Operation(summary = "菜单树形列表", description = "菜单树形列表（页面显示用）")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult getMenuTree(@RequestParam(required = false) @Schema(description = "模糊查询") String searchLike)
    {
        List<Menu> menuList = menuService.listMenuByParam(null,null,searchLike,null);
        //将列表转为树形结构
        menuList = TreeUtils.formatTree(menuList, menu -> Convert.toInt(BeanUtil.getProperty(menu, "parentId")) == 0,"menuId","parentId","children","orderNum");
        return success(menuList);
    }

    /**
     * 获取菜单树列表（下拉列表使用）
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/08/02 15:29
     */
    @GetMapping("/tree/select")
    @Operation(summary = "获取菜单树列表（下拉列表使用）", description = "获取菜单树列表（下拉列表使用）")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult getMenuTreeSelect()
    {
        List<Menu> menuList = menuService.listMenuByParam(null,null,null,null);
        //将列表转为树形结构
        menuList = TreeUtils.formatTree(menuList,menu -> Convert.toInt(BeanUtil.getProperty(menu, "parentId")) == 0,"menuId","parentId","children","orderNum");
        //将菜单列表转换为下拉框树形结构后传输到前台
        List<TreeSelectVO<Long>> mianTree = convertTreeSelect(menuList);
        return success(mianTree);
    }

    /**
     * 获取排除本菜单的菜单树列表（下拉列表使用）
     * @param menuId
     * @return com.ren.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/08 17:14
     */
    @GetMapping("/tree/select/doesNotContainSelfMenu/{menuId}")
    @Operation(summary = "获取排除本菜单的菜单树列表（下拉列表使用）", description = "获取排除本菜单的菜单树列表（下拉列表使用）")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult getDoesNotContainSelfMenuTreeSelect(@PathVariable(value = "menuId", required = false) @Schema(description = "菜单标识",type = "long") Long menuId)
    {
        List<Menu> menuList = menuService.listMenuByParam(null,null,null,null);
        menuList.removeIf(d -> d.getMenuId().intValue() == menuId);
        //将列表转为树形结构
        menuList = TreeUtils.formatTree(menuList,menu -> Convert.toInt(BeanUtil.getProperty(menu, "parentId")) == 0,"menuId","parentId","children","orderNum");
        //将菜单列表转换为下拉框树形结构后传输到前台
        List<TreeSelectVO<Long>> mianTree = convertTreeSelect(menuList);
        return success(mianTree);
    }

    /**
     * 将树形列表转换为下拉框形式（角色模块选择角色关联菜单时使用，可多选，无需超级节点）
     * @param menuList
     * @return java.util.List<com.ren.common.core.domain.vo.TreeSelectVO<java.lang.Long>>
     * @author ren
     * @date 2025/08/26 13:29
     */
    private List<TreeSelectVO<Long>> convertTreeSelect(List<Menu> menuList){
        List<TreeSelectVO<Long>> treeSelectVOList =  TreeUtils.convertTreeSelectForAll(menuList, "menuId", "menuName", "isStop", "children");
        TreeSelectVO<Long> mianSelect = new TreeSelectVO<>(0L,"主目录",false,treeSelectVOList);
        List<TreeSelectVO<Long>> mianTree = new ArrayList<>();
        mianTree.add(mianSelect);
        return mianTree;
    }

    /**
     * 获取角色菜单树列表（下拉列表使用）
     * @return com.ren.common.core.dto.AjaxResult
     * @author ren
     * @date 2025/05/12 14:53
     */
    @GetMapping("/tree/select/role/{roleId}")
    @Operation(summary = "获取角色菜单树列表（下拉列表使用）", description = "获取角色菜单树列表（下拉列表使用）")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleMenuSchemas.class)))
    public AjaxResult getRoleMenuTreeSelect(@PathVariable(value = "roleId", required = false) @Schema(description = "角色标识",type = "integer") Integer roleId)
    {
        List<Menu> menuList = menuService.listMenuByParam(null,null,null,null);
        //将列表转为树形结构
        menuList = TreeUtils.formatTree(menuList,menu -> Convert.toInt(BeanUtil.getProperty(menu, "parentId")) == 0,"menuId","parentId","children","orderNum");
        //将菜单列表转换为下拉框树形结构后传输到前台
        List<TreeSelectVO<Long>> treeSelectVOList = TreeUtils.convertTreeSelectForAll(menuList, "menuId", "menuName", "isStop", "children");

        Long[] menuIdArr = null;
        if(roleId != null){
            //获取角色菜单列表
            List<RoleMenu> roleMenuList = roleMenuService.listRoleMenuByRoleId(roleId);
            if(roleMenuList != null && !roleMenuList.isEmpty()){
                menuIdArr = roleMenuList.stream().map(roleMenu -> roleMenu.getMenuId()).toArray(Long[] :: new);
            }
        }

        return success().put("menuList",treeSelectVOList).put("menuIdArr",menuIdArr);
    }

    /**
     * 添加菜单
     * @param loginUser
     * @param addMenu
     * @return com.ren.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/09 17:01
     */
    @PostMapping("/add")
    @OperLogAnn(title = "菜单模块", businessType = BusinessType.INSERT)
    @Operation(summary = "添加菜单", description = "添加菜单")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult addMenu(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) Menu addMenu) {
        List<Menu> menuList = menuService.listMenuByParam(null,null,null,addMenu.getRouteName());
        if(CollUtil.isNotEmpty(menuList)){
            return error("路由名称已存在");
        }
        menuService.addMenu(addMenu,loginUser.getUsername());
        return success();
    }

    /**
     * 编辑菜单
     * @param loginUser
     * @param modifyMenu
     * @return com.ren.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/09 17:01
     */
    @PostMapping("/modify")
    @OperLogAnn(title = "菜单模块", businessType = BusinessType.UPDATE)
    @Operation(summary = "编辑菜单", description = "编辑菜单")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult modifyMenu(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) Menu modifyMenu) {
        List<Menu> menuList = menuService.listMenuByParam(null,null,null,modifyMenu.getRouteName());
        if(CollUtil.isNotEmpty(menuList) && menuList.stream().anyMatch(menu -> !Objects.equals(menu.getMenuId(), modifyMenu.getMenuId()))){
            return error("路由名称已存在");
        }
        menuService.modifyMenuById(modifyMenu,loginUser.getUsername());
        return success();
    }

    /**
     * 删除菜单
     * @param loginUser
     * @param menuId
     * @return com.ren.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/09 17:01
     */
    @DeleteMapping("/delete")
    @OperLogAnn(title = "菜单模块", businessType = BusinessType.DELETE)
    @Operation(summary = "删除菜单", description = "删除菜单")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult menuDelete(@AuthenticationPrincipal LoginUser loginUser,@Schema(description = "菜单标识",type = "long") long menuId) {
        //查询是否有子级菜单
        List<Menu> menuList = menuService.listMenuByParam(null,menuId,null,null);
        if(menuList != null && !menuList.isEmpty()){
            return warn("请先删除子级菜单");
        }
        //删除菜单
        menuService.modifyMenuIsDelById(menuId, AppConstants.COMMON_INT_YES,loginUser.getUsername());
        return success();
    }

}