package com.ren.admin.controller.system;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ren.common.controller.BaseController;
import com.ren.common.core.constant.AppConstants;
import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.entity.Dept;
import com.ren.common.core.domain.entity.User;
import com.ren.common.core.domain.schemas.AjaxResultSchemas;
import com.ren.common.core.domain.vo.TreeSelectVO;
import com.ren.common.core.enums.BusinessType;
import com.ren.common.core.interfaces.OperLogAnn;
import com.ren.common.core.response.AjaxResult;
import com.ren.common.utils.StringUtils;
import com.ren.common.utils.TreeUtils;
import com.ren.system.core.domain.entity.RoleDept;
import com.ren.system.core.domain.schemas.DeptSchemas;
import com.ren.system.service.DeptService;
import com.ren.system.service.RoleDeptService;
import com.ren.system.service.UserService;

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
@RequestMapping("/dept")
@Tag(name = "部门相关", description = "部门相关")
public class DeptController extends BaseController {

    @Autowired
    DeptService deptService;
    @Autowired
    RoleDeptService roleDeptService;
    @Autowired
    UserService userService;

    /**
     * 部门树形列表（页面显示用）
     * @return com.ren.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/08 17:14
     */
    @GetMapping("/tree")
    @Operation(summary = "部门树形列表", description = "部门树形列表（页面显示用）")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult getDeptTree(
            @RequestParam(required = false) @Schema(description = "模糊查询") String searchLike
    )
    {
        List<Dept> deptList = deptService.listDeptByParam(searchLike,null,null);
        //将列表转为树形结构
        deptList = TreeUtils.formatTree(deptList, dept -> Convert.toInt(BeanUtil.getProperty(dept, "parentId")) == 0,"deptId","parentId","children","orderNum");
        return success(deptList);
    }

    /**
     * 部门树形列表（其他模块下拉列表用）
     * @return com.ren.common.core.dto.AjaxResult
     * @author ren
     * @date 2025/05/10 18:06
     */
    @GetMapping("/tree/select")
    @Operation(summary = "部门树形列表", description = "部门树形列表（其他模块下拉列表用）")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult getDeptTreeSelect()
    {
        List<Dept> deptList = deptService.listDeptByParam(null,null,null);
        //将列表转为树形结构
        deptList = TreeUtils.formatTree(deptList, dept -> Convert.toInt(BeanUtil.getProperty(dept, "parentId")) == 0,"deptId","parentId","children","orderNum");
        List<TreeSelectVO<Long>> mianTree = convertTreeSelect(deptList);
        return success(mianTree);
    }

    /**
     * 排除本部门Id的部门列表（部门本身修改时下拉列表使用）
     * @param deptId
     * @return com.ren.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/08 17:14
     */
    @GetMapping("/tree/select/doesNotContainSelfDept/{deptId}")
    @Operation(summary = "排除本部门Id的部门列表", description = "排除本部门Id的部门列表（部门本身修改时下拉列表使用）")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult getDoesNotContainSelfDeptTreeSelect(@PathVariable(value = "deptId", required = false) @Schema(description = "部门标识",type = "long") Long deptId)
    {
        List<Dept> deptList = deptService.listDeptByParam(null,null,null);
        deptList.removeIf(d -> d.getDeptId().intValue() == deptId || CollUtil.contains(
                StringUtils.split(d.getAncestors(), ","),
                String.valueOf(deptId)
        ));
        //将列表转为树形结构
        deptList = TreeUtils.formatTree(deptList,dept -> Convert.toInt(BeanUtil.getProperty(dept, "parentId")) == 0,"deptId","parentId","children","orderNum");
        List<TreeSelectVO<Long>> mianTree = convertTreeSelect(deptList);
        return success(mianTree);
    }

    /**
     * 将树形列表转换为下拉框形式
     * @param deptList
     * @return java.util.List<com.ren.common.core.domain.vo.TreeSelectVO<java.lang.Long>>
     * @author ren
     * @date 2025/08/26 13:25
     */
    private List<TreeSelectVO<Long>> convertTreeSelect(List<Dept> deptList) {
        List<TreeSelectVO<Long>> treeSelectVOList = TreeUtils.convertTreeSelectForAll(deptList, "deptId", "deptName", "isStop", "children");
        TreeSelectVO<Long> mianSelect = new TreeSelectVO<>(0L,"总公司",false,treeSelectVOList);
        List<TreeSelectVO<Long>> mianTree = new ArrayList<>();
        mianTree.add(mianSelect);
        return mianTree;
    }

    /**
     * 角色部门权限列表（角色模块选择角色关联部门时使用，可多选，无需超级节点）
     * @param roleId
     * @return com.ren.common.core.dto.AjaxResult
     * @author ren
     * @date 2025/05/12 16:18
     */
    @GetMapping("/tree/select/role/{roleId}")
    @Operation(summary = "角色部门权限列表", description = "角色部门权限列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeptSchemas.class)))
    public AjaxResult getRoleDeptTreeSelect(@PathVariable(value = "roleId", required = false) @Schema(description = "角色标识",type = "integer") Integer roleId)
    {
        List<Dept> deptList = deptService.listDeptByParam(null,null,null);
        //将列表转为树形结构
        deptList = TreeUtils.formatTree(deptList, dept -> Convert.toInt(BeanUtil.getProperty(dept, "parentId")) == 0,"deptId","parentId","children","orderNum");

        Long[] deptIdArr = null;
        if(roleId != null){
            //获取角色菜单列表
            List<RoleDept> roleDeptList = roleDeptService.listRoleDeptByRoleId(roleId);
            if(roleDeptList != null && !roleDeptList.isEmpty()){
                deptIdArr = roleDeptList.stream().map(roleDept -> roleDept.getDeptId()).toArray(Long[] :: new);
            }
        }
        List<TreeSelectVO<Long>> treeSelectVOList = TreeUtils.convertTreeSelectForAll(deptList, "deptId", "deptName", "isStop", "children");
        return success().put("deptList",treeSelectVOList).put("deptIdArr",deptIdArr);
    }

    /**
     * 添加部门
     * @param loginUser
     * @param addDept
     * @return com.ren.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/09 17:01
     */
    @PostMapping("/add")
    @OperLogAnn(title = "部门模块", businessType = BusinessType.INSERT)
    @Operation(summary = "添加部门", description = "添加部门")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult addDept(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) Dept addDept) {
        deptService.addDept(addDept,loginUser.getUsername());
        return success();
    }

    /**
     * 编辑部门
     * @param loginUser
     * @param modifyDept
     * @return com.ren.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/09 17:01
     */
    @PostMapping("/modify")
    @OperLogAnn(title = "部门模块", businessType = BusinessType.UPDATE)
    @Operation(summary = "编辑部门", description = "编辑部门")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult modifyDept(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) Dept modifyDept) {
        deptService.modifyDeptById(modifyDept,loginUser.getUsername());
        return success();
    }

    /**
     * 删除部门
     * @param loginUser
     * @param deptId
     * @return com.ren.common.dto.AjaxResult
     * @author ren
     * @date 2025/05/09 17:01
     */
    @DeleteMapping("/delete")
    @OperLogAnn(title = "部门模块", businessType = BusinessType.DELETE)
    @Operation(summary = "删除部门", description = "删除部门")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult deptDelete(@AuthenticationPrincipal LoginUser loginUser,@Schema(description = "部门标识",type = "long") long deptId) {
        //查询是否有子级部门
        List<Dept> deptList = deptService.listDeptByParam(null,null,deptId);
        if(deptList != null && !deptList.isEmpty()){
            return warn("请先删除子级部门");
        }
        //查询部门下是否有用户
        List<User> userList = userService.listUserByDeptId(deptId);
        if(userList != null && !userList.isEmpty()){
            return warn("该部门下还有正在使用的用户，请先删除");
        }
        deptService.modifyDeptIsDelById(deptId, AppConstants.COMMON_INT_YES,loginUser.getUsername());
        return success();
    }

}