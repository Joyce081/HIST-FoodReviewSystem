package com.ren.admin.controller.system;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ren.common.controller.BaseController;
import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.schemas.AjaxResultSchemas;
import com.ren.common.core.enums.BusinessType;
import com.ren.common.core.interfaces.OperLogAnn;
import com.ren.common.core.interfaces.Pageable;
import com.ren.common.core.page.TableDataInfo;
import com.ren.common.core.response.AjaxResult;
import com.ren.system.core.domain.entity.DictType;
import com.ren.system.service.DictTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/dictType")
@Tag(name = "字典类型相关", description = "字典类型相关")
public class DictTypeController extends BaseController {

    @Autowired
    DictTypeService dictTypeService;

    /**
     * 字典类型字典类型列表
     * @param searchLike
     * @return com.ren.common.domain.page.TableDataInfo
     * @author ren
     * @date 2025/05/18 15:28
     */
    @GetMapping("/list/page")
    @Pageable  //注意，如果要开启分页，请添加该注解
    @Operation(summary = "字典类型分页列表", description = "字典类型分页列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public TableDataInfo listDictTypeByPage(@RequestParam(required = false) @Schema(description = "模糊查询") String searchLike) {
        IPage<DictType> dictTypeList = dictTypeService.listDictTypeByPage(searchLike);
        return getDataTable(dictTypeList);
    }

    /**
     * 字典类型列表
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/23 13:51
     */
    @GetMapping("/list")
    @Operation(summary = "字典类型列表", description = "字典类型列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult listDictType() {
        List<DictType> dictTypeList = dictTypeService.listDictTypeByParam(null);
        return success(dictTypeList);
    }

    /**
     * 添加字典类型
     * @param loginUser
     * @param addDictType
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @PostMapping("/add")
    @OperLogAnn(title = "字典模块", businessType = BusinessType.INSERT)
    @Operation(summary = "添加字典类型", description = "添加字典类型")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult addDictType(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) DictType addDictType) {
        dictTypeService.addDictType(addDictType,loginUser.getUsername());
        return success();
    }

    /**
     * 编辑字典类型
     * @param loginUser
     * @param modifyDictType
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @PostMapping("/modify")
    @OperLogAnn(title = "字典模块", businessType = BusinessType.UPDATE)
    @Operation(summary = "编辑字典类型", description = "编辑字典类型")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult modifyDictType(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) DictType modifyDictType) {
        dictTypeService.modifyDictType(modifyDictType,loginUser.getUsername());
        return success();
    }

    /**
     * 删除字典类型
     * @param loginUser
     * @param dictTypeId
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @DeleteMapping("/delete")
    @OperLogAnn(title = "字典模块", businessType = BusinessType.DELETE)
    @Operation(summary = "删除字典类型", description = "删除字典类型")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult dictTypeDelete(@AuthenticationPrincipal LoginUser loginUser,@Schema(description = "字典类型标识",type = "long") long dictTypeId) {
        dictTypeService.removeDictType(dictTypeId);
        return success();
    }

}