package com.ren.admin.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ren.common.controller.BaseController;
import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.schemas.AjaxResultSchemas;
import com.ren.common.core.domain.schemas.UserInfoSchemas;
import com.ren.common.core.response.AjaxResult;
import com.ren.common.core.enums.BusinessType;
import com.ren.common.core.page.TableDataInfo;
import com.ren.common.core.interfaces.OperLogAnn;
import com.ren.common.core.interfaces.Pageable;
import com.ren.system.core.domain.entity.DictData;
import com.ren.system.service.DictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dictData")
@Tag(name = "字典相关", description = "字典相关")
public class DictDataController extends BaseController {

    @Autowired
    DictDataService dictDataService;

    /**
     * 字典数据字典数据列表
     * @param searchLike
     * @return com.ren.common.domain.page.TableDataInfo
     * @author ren
     * @date 2025/05/18 15:28
     */
    @GetMapping("/list/page")
    @Pageable  //注意，如果要开启分页，请添加该注解
    @Operation(summary = "字典数据分页列表", description = "字典数据分页列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public TableDataInfo listDictDataByPage(@RequestParam(required = false) @Schema(description = "模糊查询") String searchLike) {
        IPage<DictData> dictDataList = dictDataService.listDictDataByPage(searchLike);
        return getDataTable(dictDataList);
    }

    /**
     * 字典数据列表
     * @param dictType
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/08/15 14:57
     */
    @GetMapping("/list")
    @Operation(summary = "字典数据列表", description = "字典数据列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult listDictData(@RequestParam(required = false) @Schema(description = "字典类型") String dictType) {
        List<DictData> dictDataList = dictDataService.listDictDataByDictType(dictType);
        return success(dictDataList);
    }

    /**
     * 添加字典数据
     * @param loginUser
     * @param addDictData
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @PostMapping("/add")
    @OperLogAnn(title = "字典模块", businessType = BusinessType.INSERT)
    @Operation(summary = "添加字典数据", description = "添加字典数据")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult addDictData(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) DictData addDictData) {
        dictDataService.addDictData(addDictData,loginUser.getUsername());
        return success();
    }

    /**
     * 编辑字典数据
     * @param loginUser
     * @param modifyDictData
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @PostMapping("/modify")
    @OperLogAnn(title = "字典模块", businessType = BusinessType.UPDATE)
    @Operation(summary = "编辑字典数据", description = "编辑字典数据")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult modifyDictData(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) DictData modifyDictData) {
        dictDataService.modifyDictData(modifyDictData,loginUser.getUsername());
        return success();
    }

    /**
     * 删除字典数据
     * @param loginUser
     * @param dictDataId
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @DeleteMapping("/delete")
    @OperLogAnn(title = "字典模块", businessType = BusinessType.DELETE)
    @Operation(summary = "删除字典数据", description = "删除字典数据")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult dictDataDelete(@AuthenticationPrincipal LoginUser loginUser, @Schema(description = "字典ID",type = "long") long dictDataId) {
        dictDataService.removeDictData(dictDataId);
        return success();
    }

}