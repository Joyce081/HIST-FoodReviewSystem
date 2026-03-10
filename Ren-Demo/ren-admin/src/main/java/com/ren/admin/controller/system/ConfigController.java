package com.ren.admin.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ren.common.controller.BaseController;
import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.schemas.AjaxResultSchemas;
import com.ren.common.core.response.AjaxResult;
import com.ren.common.core.enums.BusinessType;
import com.ren.common.core.page.TableDataInfo;
import com.ren.common.core.interfaces.OperLogAnn;
import com.ren.common.core.interfaces.Pageable;
import com.ren.system.core.domain.entity.Config;
import com.ren.system.service.ConfigService;
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

@RestController
@RequestMapping("/config")
@Tag(name = "系统配置相关", description = "系统配置相关")
public class ConfigController extends BaseController {

    @Autowired
    ConfigService configService;

    /**
     * 配置分页列表
     * @return com.ren.common.domain.page.TableDataInfo
     * @author ren
     * @date 2025/05/18 15:28
     */
    @GetMapping("/list/page")
    @Pageable  //注意，如果要开启分页，请添加该注解
    @Operation(summary = "配置分页列表", description = "配置分页列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public TableDataInfo listConfigByPage(
            @RequestParam(required = false) @Schema(description = "模糊查询") String searchLike
    ) {
        IPage<Config> configList = configService.listConfigByPage(searchLike);
        return getDataTable(configList);
    }

    /**
     * 添加配置
     * @param loginUser
     * @param addConfig
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @PostMapping("/add")
    @OperLogAnn(title = "配置模块", businessType = BusinessType.INSERT)
    @Operation(summary = "添加配置", description = "添加配置")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult addConfig(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) Config addConfig) {
        configService.addConfig(addConfig,loginUser.getUsername());
        return success();
    }

    /**
     * 编辑配置
     * @param loginUser
     * @param modifyConfig
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @PostMapping("/modify")
    @OperLogAnn(title = "配置模块", businessType = BusinessType.UPDATE)
    @Operation(summary = "编辑配置", description = "编辑配置")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult modifyConfig(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) Config modifyConfig) {
        configService.modifyConfig(modifyConfig,loginUser.getUsername());
        return success();
    }

    /**
     * 删除配置
     * @param loginUser
     * @param configId
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @DeleteMapping("/delete")
    @OperLogAnn(title = "配置模块", businessType = BusinessType.DELETE)
    @Operation(summary = "删除配置", description = "删除配置")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult configDelete(@AuthenticationPrincipal LoginUser loginUser,@Schema(description = "配置ID",type = "long") long configId) {
        configService.removeConfig(configId);
        return success();
    }

}