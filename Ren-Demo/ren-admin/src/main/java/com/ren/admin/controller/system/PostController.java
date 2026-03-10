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
import com.ren.system.core.domain.entity.Post;
import com.ren.system.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/post")
@Tag(name = "岗位相关", description = "岗位相关")
public class PostController extends BaseController {

    @Autowired
    PostService postService;

    /**
     * 岗位分页列表
     * @param searchLike
     * @return com.ren.common.domain.page.TableDataInfo
     * @author ren
     * @date 2025/05/18 15:28
     */
    @GetMapping("/list/page")
    @Pageable  //注意，如果要开启分页，请添加该注解
    @Operation(summary = "岗位分页列表", description = "岗位分页列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public TableDataInfo listPostByPage(@RequestParam(required = false) @Schema(description = "模糊查询") String searchLike) {
        IPage<Post> postList = postService.listPostByPage(searchLike);
        return getDataTable(postList);
    }

    /**
     * 岗位列表
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/23 14:29
     */
    @GetMapping("/list")
    @Operation(summary = "岗位列表", description = "岗位列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult listPost() {
        List<Post> postList = postService.listPostByParam(null);
        return success(postList);
    }

    /**
     * 添加岗位
     * @param loginUser
     * @param addPost
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @PostMapping("/add")
    @OperLogAnn(title = "岗位模块", businessType = BusinessType.INSERT)
    @Operation(summary = "添加岗位", description = "添加岗位")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult addPost(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) Post addPost) {
        postService.addPost(addPost,loginUser.getUsername());
        return success();
    }

    /**
     * 编辑岗位
     * @param loginUser
     * @param modifyPost
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @PostMapping("/modify")
    @OperLogAnn(title = "岗位模块", businessType = BusinessType.UPDATE)
    @Operation(summary = "编辑岗位", description = "编辑岗位")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult modifyPost(@AuthenticationPrincipal LoginUser loginUser, @RequestBody(required = false) Post modifyPost) {
        postService.modifyPost(modifyPost,loginUser.getUsername());
        return success();
    }

    /**
     * 删除岗位
     * @param loginUser
     * @param postId
     * @return com.ren.common.domain.dto.AjaxResult
     * @author ren
     * @date 2025/05/18 15:28
     */
    @DeleteMapping("/delete")
    @OperLogAnn(title = "岗位模块", businessType = BusinessType.DELETE)
    @Operation(summary = "删除岗位", description = "删除岗位")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult postDelete(@AuthenticationPrincipal LoginUser loginUser, @Schema(description = "岗位标识",type = "long") long postId) {
        postService.removePost(postId);
        return success();
    }

}