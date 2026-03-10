package com.ren.admin.controller.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ren.common.controller.BaseController;
import com.ren.common.core.constant.AppConstants;
import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.entity.Dept;
import com.ren.common.core.domain.entity.Menu;
import com.ren.common.core.domain.entity.Role;
import com.ren.common.core.domain.entity.User;
import com.ren.common.core.domain.schemas.AjaxResultSchemas;
import com.ren.common.core.domain.schemas.UserInfoSchemas;
import com.ren.common.core.domain.schemas.UserInfoV2Schemas;
import com.ren.common.core.domain.vo.DynamicRouteVO;
import com.ren.common.core.domain.vo.MenuVO;
import com.ren.common.core.enums.BusinessType;
import com.ren.common.core.interfaces.OperLogAnn;
import com.ren.common.core.interfaces.Pageable;
import com.ren.common.core.page.TableDataInfo;
import com.ren.common.core.response.AjaxResult;
import com.ren.common.manager.SecurityManager;
import com.ren.common.utils.StringUtils;
import com.ren.common.utils.TreeUtils;
import com.ren.framework.security.utils.JwtUtils;
import com.ren.system.core.domain.dto.ResetPasswordDTO;
import com.ren.system.core.domain.entity.Post;
import com.ren.system.core.domain.entity.UserPost;
import com.ren.system.core.domain.entity.UserRole;
import com.ren.system.service.DeptService;
import com.ren.system.service.MenuService;
import com.ren.system.service.PostService;
import com.ren.system.service.RoleService;
import com.ren.system.service.UserPostService;
import com.ren.system.service.UserRoleService;
import com.ren.system.service.UserService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关操作接口")
public class UserController extends BaseController {

    @Autowired
    UserService userService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserPostService userPostService;
    @Autowired
    RoleService roleService;
    @Autowired
    PostService postService;
    @Autowired
    MenuService menuService;
    @Autowired
    DeptService deptService;
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户信息（登录使用）
     * @AuthenticationPrincipal LoginUser loginUser可以直接从SpringSecurity中获取到当前的用户信息
     * @param loginUser
     * @return org.springframework.http.ResponseEntity<?>
     * @author ren
     * @date 2025/04/17 19:42
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录人用户信息")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoSchemas.class)))
    public AjaxResult getUserInfo(@AuthenticationPrincipal LoginUser loginUser) {
        //获取用户信息，并返回
        AjaxResult ajax = success();
        loginUser.getUser().setPassword(null);
        ajax.put("loginUser", loginUser);
        List<Role> roleList = roleService.listRoleByUserId(loginUser.getUserId());
        //是否Admin
        boolean isAdmin = roleList.stream().anyMatch(role -> role.getRoleKey().equals(AppConstants.ROLE_SUPER_KEY));
        ajax.put("isAdmin", isAdmin);
        //获取用户所拥有角色名称，并返回
        ajax.put("roleNames", roleList.stream().map(Role::getRoleName).toArray(String[]::new));
        //获取用户所拥有角色Id，并返回
        Long[] roleIds = roleList.stream().map(Role::getRoleId).toArray(Long[]::new);
        ajax.put("roleIds", roleIds);
        List<Post> postList = postService.listPostByUserId(loginUser.getUserId());
        ajax.put("postNames", postList.stream().map(Post::getPostName).toArray(String[]::new));
        ajax.put("postIds", postList.stream().map(Post::getPostId).toArray(Long[]::new));
        //获取用所拥有权限，并返回（前端用于判断按钮权限）
        List<Menu> menuList = new ArrayList<>();
        if(isAdmin){
            //超级管理员获取所有菜单
            menuList = menuService.listMenuByParam(null,null,null,null);
        }else if(roleIds.length > 0){
            //不是超级管理员获取相应菜单
            menuList = menuService.listMenuByRoleIds(roleIds);
        }
        ajax.put("permissions", menuList.stream().filter(menu -> !menu.getMenuType().equals(AppConstants.MENU_TYPE_DIR)).map(Menu::getPerms).toArray(String[]::new));
        List<Menu> routerMenuList = menuList.stream().distinct().filter(menu -> menu.getMenuType().equals(AppConstants.MENU_TYPE_DIR) || menu.getMenuType().equals(AppConstants.MENU_TYPE_MENU)).toList();
        List<Menu> routerMenuTree = TreeUtils.formatTree(routerMenuList, menu -> Convert.toInt(BeanUtil.getProperty(menu, "parentId")) == 0,"menuId",null,null,"orderNum");
        //获取用户所能看到的菜单（包含目录），格式化后返回（前端用于侧边栏目录显示）
        List<MenuVO> menuVOList = routerMenuTree.stream().map(menu -> new MenuVO(menu, "path", "menuName", "icon", "children")).toList();
        //递归修改每一级菜单路径
        menuVOList = modifyMenuVOIndex("",menuVOList);
        ajax.put("menus", menuVOList);
        //获取用户所能看到的菜单（不包含目录），格式化后返回（前端用于动态路由配置）
        List<DynamicRouteVO> dynamicRouteVOList = routerMenuTree.stream().map(menu -> new DynamicRouteVO(menu, "menuName","path", "routeName", "component", new DynamicRouteVO().new Meta(true, new String[]{},""),"children")).toList();
        dynamicRouteVOList = modifyDynamicRouteVOMenuShow("",dynamicRouteVOList);
        ajax.put("dynamicRoutes", dynamicRouteVOList);
        return ajax;
    }

    /**
     * 递归修改每级菜单Index值
     * @param parentIndex
     * @param menuVOList
     * @return java.util.List<com.ren.common.domain.vo.MenuVO>
     * @author ren
     * @date 2025/05/19 17:36
     */
    protected List<MenuVO> modifyMenuVOIndex(String parentIndex,List<MenuVO> menuVOList) {
        for(MenuVO menuVO : menuVOList){
            menuVO.setIndex(parentIndex + "/" + menuVO.getIndex());
            if(menuVO.getChildren() != null && !menuVO.getChildren().isEmpty()){
                menuVO.setChildren(modifyMenuVOIndex(menuVO.getIndex(),menuVO.getChildren()));
            }
        }
        return menuVOList;
    }

    /**
     * 递归修改每级菜单Show值
     * @param parentMenuShow
     * @param dynamicRouteVOList
     * @return java.util.List<com.ren.common.domain.vo.DynamicRouteVO>
     * @author ren
     * @date 2025/05/19 17:36
     */
    protected List<DynamicRouteVO> modifyDynamicRouteVOMenuShow(String parentMenuShow,List<DynamicRouteVO> dynamicRouteVOList) {
        for(DynamicRouteVO dynamicRouteVO : dynamicRouteVOList){
            dynamicRouteVO.getMeta().setMenuShow(parentMenuShow + "/" + dynamicRouteVO.getPath());
            if(dynamicRouteVO.getChildren() != null && !dynamicRouteVO.getChildren().isEmpty()){
                dynamicRouteVO.setChildren(modifyDynamicRouteVOMenuShow(dynamicRouteVO.getMeta().getMenuShow(),dynamicRouteVO.getChildren()));
            }
        }
        return dynamicRouteVOList;
    }

    /**
     * 用户信息（其他模块获取用户信息使用）
     * @param userId
     * @return com.ren.common.core.dto.AjaxResult
     * @author ren
     * @date 2025/05/12 21:02
     */
    @GetMapping("/info/v2")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoV2Schemas.class)))
    public AjaxResult getUserInfoV2(@Parameter(description = "用户标识", required = true) @Schema(description = "用户标识",type = "long") Long userId) {
        User user = userService.getUserById(userId);
        List<UserRole> userRoleList = userRoleService.listUserRoleByUserId(userId);
        Long[] roleIdArr = userRoleList.stream().map(UserRole::getRoleId).toArray(Long[]::new);
        List<UserPost> userPostList = userPostService.listUserPostByUserId(userId);
        Long[] postIdArr = userPostList.stream().map(UserPost::getPostId).toArray(Long[]::new);
        return success().put("userInfo",user).put("roleIdArr",roleIdArr).put("postIdArr",postIdArr);
    }

    /**
     * 用户列表
     * @return com.ren.admin.common.dto.AjaxResult
     * @author ren
     * @date 2025/04/26 15:55
     */
    @GetMapping("/list/page")
    @Operation(summary = "用户分页列表", description = "分页获取用户列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @Pageable  //注意，如果要开启分页，请添加该注解
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public TableDataInfo listUserByPage(
            @RequestParam(required = false) @Schema(description = "模糊查询") String searchLike,
            @RequestParam(required = false) @Schema(description = "部门标识",type = "long") Long deptId,
            @RequestParam(required = false) @Schema(description = "用户类型",type = "integer") String userType,
            @RequestParam(required = false) @Schema(description = "性别",type = "integer") Integer sex
    ) {
        IPage<User> userList = userService.listUserByPage(deptId,searchLike,userType,sex);
        return getDataTable(userList);
    }

    /**
     * 添加用户
     * @param loginUser
     * @param addUser
     * @return com.ren.admin.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/04 16:26
     */
    @PostMapping("/add")
    @OperLogAnn(title = "用户模块", businessType = BusinessType.INSERT)
    @Operation(summary = "添加用户", description = "添加用户")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult addUser(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) User addUser) {
        userService.addUser(addUser,loginUser.getUsername());
        return success();
    }

    /**
     * 编辑用户
     * @param loginUser
     * @param modifyUser
     * @return com.ren.admin.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/04 16:08
     */
    @PostMapping("/modify")
    @OperLogAnn(title = "用户模块", businessType = BusinessType.UPDATE)
    @Operation(summary = "编辑用户", description = "编辑用户")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult modifyUser(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) User modifyUser) {
        userService.modifyUser(modifyUser,loginUser.getUsername());
        return success();
    }

    /**
     * 删除用户
     * @param loginUser
     * @param userId
     * @return com.ren.admin.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/04 15:27
     */
    @DeleteMapping("/delete")
    @OperLogAnn(title = "用户模块", businessType = BusinessType.DELETE)
    @Operation(summary = "删除用户", description = "删除用户")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult deleteUser(@AuthenticationPrincipal LoginUser loginUser,
								 @Parameter(description = "用户标识", required = true) @Schema(description = "用户标识",type = "long") long userId) {
        userService.modifyUserIsDelById(userId, AppConstants.COMMON_INT_YES,loginUser.getUsername());
        return success();
    }

    /**
     * 重置密码
     * @param loginUser
     * @param resetPasswordDTO
     * @return com.ren.admin.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/04 14:33
     */
    @PostMapping("/resetPassword")
    @OperLogAnn(title = "用户模块", businessType = BusinessType.UPDATE)
    @Operation(summary = "重置密码", description = "重置用户密码")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult resetPassword(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false)ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(resetPasswordDTO.getUserId(),resetPasswordDTO.getPassword(),loginUser.getUsername());
        return success();
    }

    /**
     * 用户信息
     * @param loginUser
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/09/20 14:22
     */
    @GetMapping("/profile")
    public AjaxResult profile(@AuthenticationPrincipal LoginUser loginUser)
    {
        Dept dept = deptService.getDeptById(loginUser.getUser().getDeptId());
        if(ObjUtil.isNotEmpty(dept)) loginUser.getUser().setDept(dept);
        List<Role> roleList = roleService.listRoleByUserId(loginUser.getUserId());
        List<Post> postList = postService.listPostByUserId(loginUser.getUserId());

        return success(loginUser.getUser())
                .put("allRoleName", CollUtil.emptyIfNull(roleList).stream().map(Role::getRoleName).collect(Collectors.joining(",")))
                .put("allPostName", CollUtil.emptyIfNull(postList).stream().map(Post::getPostName).collect(Collectors.joining(",")));
    }

    /**
     * 修改用户信息
     * @param loginUser
     * @param user
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/09/20 14:22
     */
    @PutMapping("/profile/update")
    public AjaxResult updateProfile(HttpServletRequest request,@AuthenticationPrincipal LoginUser loginUser, @RequestBody User user)
    {
        //修改用户
        User currentUser = loginUser.getUser();
        currentUser.setNickname(user.getNickname());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotBlank(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotBlank(user.getEmail()) && !userService.checkEmailUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
        }
        if (userService.modifyUserProfile(currentUser) > 0)
        {
            // 更新缓存用户信息
            loginUser.getUser().setNickname(user.getNickname());
            loginUser.getUser().setEmail(user.getEmail());
            loginUser.getUser().setPhonenumber(user.getPhonenumber());
            loginUser.getUser().setSex(user.getSex());
            Map<String,String> map = jwtUtils.saveNewAuthenticationAndReturnAccessToken(loginUser,request);
            return success().put("accessToken",map.get("accessToken")).put("refreshToken",map.get("refreshToken")).put("loginUser",loginUser);
        }
        return error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     * @param params
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/09/20 16:40
     */
    @PutMapping("/profile/updatePwd")
    public AjaxResult updatePwd(HttpServletRequest request,@AuthenticationPrincipal LoginUser loginUser,@RequestBody Map<String, String> params)
    {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        Long userId = loginUser.getUserId();
        String password = loginUser.getPassword();
        if (!SecurityManager.matchesPassword(oldPassword, password))
        {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityManager.matchesPassword(newPassword, password))
        {
            return error("新密码不能与旧密码相同");
        }
        newPassword = SecurityManager.encryptPassword(newPassword);

        if (userService.resetPassword(userId, newPassword,loginUser.getUsername()) > 0)
        {
            loginUser.getUser().setPassword(newPassword);
            // 更新缓存用户信息
            Map<String,String> map = jwtUtils.saveNewAuthenticationAndReturnAccessToken(loginUser,request);
            return success().put("accessToken",map.get("accessToken")).put("refreshToken",map.get("refreshToken"));
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 修改用户头像
     * @param request
     * @param loginUser
     * @param params
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/09/20 22:24
     */
    @PutMapping("/profile/updateAvatar")
    public AjaxResult updateAvatar(HttpServletRequest request,@AuthenticationPrincipal LoginUser loginUser, @RequestBody Map<String, String> params)
    {
        if (userService.modifyUserAvatar(loginUser.getUserId(), params.get("avatar")) > 0)
        {
            // 更新缓存用户信息
            loginUser.getUser().setAvatar(params.get("avatar"));
            Map<String,String> map = jwtUtils.saveNewAuthenticationAndReturnAccessToken(loginUser,request);
            return success().put("accessToken",map.get("accessToken")).put("refreshToken",map.get("refreshToken")).put("loginUser",loginUser);
        }
        return error("修改用户头像异常，请联系管理员");
    }

}