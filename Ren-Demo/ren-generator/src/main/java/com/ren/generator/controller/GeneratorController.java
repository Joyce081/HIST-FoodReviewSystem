package com.ren.generator.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import cn.hutool.core.io.IoUtil;
import com.ren.generator.properties.GenProperties;
import jakarta.servlet.http.HttpServletResponse;
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
import com.ren.generator.core.domain.dto.CreateTableDTO;
import com.ren.generator.core.domain.entity.GenTable;
import com.ren.generator.core.domain.entity.GenTableColumn;
import com.ren.generator.service.GenTableColumnService;
import com.ren.generator.service.GenTableService;
import com.ren.generator.utils.GenUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

/**
 * GeneratorController
 *
 * @author ren
 * @version 2025/07/28 16:12
 **/
@RestController
@RequestMapping("/gen")
public class GeneratorController extends BaseController {

    @Autowired
    private GenTableService genTableService;
    @Autowired
    private GenTableColumnService genTableColumnService;

    /**
     * 数据库表分页列表
     * 
     * @param searchLike
     * @return com.ren.common.core.page.TableDataInfo
     * @author ren
     * @date 2025/07/29 13:09
     */
    @GetMapping("/list/page")
    @Pageable // 注意，如果要开启分页，请添加该注解
    @Operation(summary = "数据库表分页列表", description = "数据库表分页列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200", description = "成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public TableDataInfo
        listGenTableByPage(@RequestParam(required = false) @Schema(description = "模糊查询") String searchLike) {
        IPage<GenTable> genTableList = genTableService.listGenTableByPage(searchLike);
        return getDataTable(genTableList);
    }

    /**
     * 数据库表列表
     * 
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/07/29 13:10
     */
    @GetMapping("/list/dataBase/page")
    @Pageable // 注意，如果要开启分页，请添加该注解
    @Operation(summary = "数据库表列表", description = "数据库表列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200", description = "成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public TableDataInfo listGenTable(String searchLike) {
        IPage<GenTable> genTableList = genTableService.listGenTableForDatabase(searchLike);
        return getDataTable(genTableList);
    }

    /**
     * 添加数据库表
     * 
     * @param loginUser
     * @param tableList
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/07/29 13:12
     */
    @PostMapping("/add")
    @OperLogAnn(title = "添加数据库表", businessType = BusinessType.INSERT)
    @Operation(summary = "添加数据库表", description = "添加数据库表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200", description = "成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult addGenTable(@AuthenticationPrincipal LoginUser loginUser,
        @RequestBody(required = false) List<GenTable> tableList) {
        genTableService.addGenTable(tableList, loginUser.getUsername());
        return success();
    }

    /**
     * 数据库表详情
     * 
     * @param tableId
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/07/29 13:16
     */
    @GetMapping("/detail")
    @OperLogAnn(title = "数据库表详情", businessType = BusinessType.DELETE)
    @Operation(summary = "数据库表详情", description = "数据库表详情")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200", description = "成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult genTableDetail(@Schema(description = "数据库表标识", type = "long") long tableId) {
        // 表基础信息
        GenTable genTable = genTableService.getGenTableById(tableId);
        // 表列信息
        List<GenTableColumn> genTableColumnList = genTableColumnService.listGenTableColumnByTableId(tableId);
        // 子表信息
        List<GenTable> subTables = genTableService.listGenTableByParam(new Long[] {tableId});
        return success().put("genTable", genTable).put("genTableColumnList", genTableColumnList).put("subTables",
            subTables);
    }

    /**
     * 编辑数据库表
     * 
     * @param loginUser
     * @param genTable
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/07/29 13:19
     */
    @PostMapping("/modify")
    @OperLogAnn(title = "编辑数据库表", businessType = BusinessType.INSERT)
    @Operation(summary = "编辑数据库表", description = "编辑数据库表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200", description = "成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult modifyGenTable(@AuthenticationPrincipal LoginUser loginUser,
        @RequestBody(required = false) GenTable genTable) {
        // 验证表
        GenUtils.validateEdit(genTable);
        // 编辑表信息
        genTableService.modifyGenTableById(genTable, loginUser.getUsername());
        return success();
    }

    /**
     * 数据库表模块
     * 
     * @param loginUser
     * @param tableIds
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/07/29 13:14
     */
    @DeleteMapping("/delete")
    @OperLogAnn(title = "删除数据库表", businessType = BusinessType.DELETE)
    @Operation(summary = "删除数据库表", description = "删除数据库表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200", description = "成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult genTableDelete(@AuthenticationPrincipal LoginUser loginUser,
        @Schema(description = "数据库表标识", type = "long") Long[] tableIds) {
        genTableService.deleteGenTableByIds(tableIds);
        return success();
    }

    /**
     * 创建表
     * 
     * @param loginUser
     * @param createTableDTO
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/08/10 16:48
     */
    @PostMapping("/create")
    @OperLogAnn(title = "创建表", businessType = BusinessType.INSERT)
    @Operation(summary = "创建表", description = "创建表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200", description = "成功",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult createTable(@AuthenticationPrincipal LoginUser loginUser,
        @RequestBody(required = false) CreateTableDTO createTableDTO) {
        try {
            genTableService.createTableMain(createTableDTO, loginUser.getUsername());
            return AjaxResult.success();
        } catch (Exception e) {
            // 错误处理
            logger.error(e.getMessage(), e);
            return AjaxResult.error("创建表结构异常");
        }
    }

	/**
	 * 同步表结构
	 * @param loginUser
	 * @param tableId
	 * @return com.ren.common.core.response.AjaxResult
	 * @author ren
	 * @date 2025/08/10 19:01
	 */
	@PostMapping("/refresh")
	@OperLogAnn(title = "同步表结构", businessType = BusinessType.INSERT)
	@Operation(summary = "同步表结构", description = "同步表结构")
	@SecurityRequirements({@SecurityRequirement(name = "JWT")})
	@ApiResponse(responseCode = "200", description = "成功",
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
	public AjaxResult refreshTable(@AuthenticationPrincipal LoginUser loginUser,Long tableId) {
		try {
			genTableService.refreshTableMain(tableId, loginUser.getUsername());
			return AjaxResult.success();
		} catch (Exception e) {
			// 错误处理
			logger.error(e.getMessage(), e);
			return AjaxResult.error("同步表结构异常");
		}
	}

	/**
	 * 自定义路径代码生成
	 * @param tableIds
	 * @return com.ren.common.core.response.AjaxResult
	 * @author ren
	 * @date 2025/08/11 15:39
	 */
	@GetMapping("/genCode")
	@OperLogAnn(title = "代码生成", businessType = BusinessType.DELETE)
	@Operation(summary = "代码生成", description = "代码生成")
	@SecurityRequirements({@SecurityRequirement(name = "JWT")})
	public AjaxResult genCode(@Schema(description = "表标识", type = "long") long[] tableIds) {
		if (!GenProperties.getAllowOverwrite())
		{
			return error("【系统预设】不允许生成文件覆盖到本地");
		}
		genTableService.genCode(tableIds);
		return success();
	}

	/**
	 * 压缩包代码生成
	 * @param tableIds
	 * @return com.ren.common.core.response.AjaxResult
	 * @author ren
	 * @date 2025/08/11 15:39
	 */
	@GetMapping("/genCodeZip")
	@OperLogAnn(title = "代码生成（ZIP）", businessType = BusinessType.DELETE)
	@Operation(summary = "代码生成（ZIP）", description = "代码生成（ZIP）")
	@SecurityRequirements({@SecurityRequirement(name = "JWT")})
	public void genCodeZip(HttpServletResponse response, @Schema(description = "表标识", type = "long") long[] tableIds) throws IOException {
		byte[] data = genTableService.genCodeZip(response,tableIds);
		// 从内存输出流中读取数据（这里注意，一定要在装饰流关闭后再读取，因为装饰流的关闭，会同时将装饰流的结束标记写入内存输出流中，关闭后再读取可以保证数据完整性），并输出给浏览器
		returnWebBrowserIo(response,data);
	}

	/**
	 * 返回浏览器IO下载流
	 *
	 * @param response
	 * @param data
	 * @author ren
	 * @date 2025/08/13 17:30
	 */
	private void returnWebBrowserIo(HttpServletResponse response, byte[] data) throws IOException {
		response.reset();
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
		response.setHeader("Content-Disposition", "attachment; filename=\"ren.zip\"");
		response.addHeader("Content-Length", "" + data.length);
		response.setContentType("application/octet-stream; charset=UTF-8");
		IoUtil.write(response.getOutputStream(),true,data);
	}

}