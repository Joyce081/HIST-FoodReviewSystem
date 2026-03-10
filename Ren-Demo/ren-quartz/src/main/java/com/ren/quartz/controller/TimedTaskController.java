package com.ren.quartz.controller;

import java.util.List;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ren.common.controller.BaseController;
import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.schemas.AjaxResultSchemas;
import com.ren.common.core.enums.BusinessType;
import com.ren.common.core.interfaces.OperLogAnn;
import com.ren.common.core.interfaces.Pageable;
import com.ren.common.core.page.TableDataInfo;
import com.ren.common.core.response.AjaxResult;
import com.ren.common.utils.StringUtils;
import com.ren.quartz.core.domain.entity.TimedTask;
import com.ren.quartz.core.domain.entity.TimedTaskLog;
import com.ren.quartz.core.exception.QuartzException;
import com.ren.quartz.manager.QuartzManager;
import com.ren.quartz.service.TimedTaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/timedTask")
@Slf4j
@Tag(name = "定时任务相关", description = "定时任务相关")
public class TimedTaskController extends BaseController {

    @Autowired
    TimedTaskService timedTaskService;
    // Quartz调度器
    @Autowired
    private Scheduler scheduler;

    /*==================================================TimedTask=====================================================*/

    /**
     * 初始化任务
     *
     * @author ren
     * @date 2025/06/26 20:37
     */
    @PostConstruct
    public void init() throws SchedulerException, QuartzException {
        scheduler.clear();
        List<TimedTask> timedTaskList = timedTaskService.listTimedTaskByParam(null);
        for (TimedTask timedTask : timedTaskList) {
            QuartzManager.createScheduleJob(scheduler, timedTask);
        }
    }

    /**
     * 分页获取任务列表
     * 
     * @param searchLike
     * @return com.ren.common.domain.page.TableDataInfo
     * @author ren
     * @date 2025/06/23 15:12
     */
    @GetMapping("/list/page")
    @Pageable // 注意，如果要开启分页，请添加该注解
    @Operation(summary = "分页获取任务列表", description = "分页获取任务列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public TableDataInfo listTimedTaskByPage(@RequestParam(required = false) @Schema(description = "模糊查询") String searchLike) {
        IPage<TimedTask> timedTaskList = timedTaskService.listTimedTaskByPage(searchLike);
        return getDataTable(timedTaskList);
    }

    /**
     * 添加定时任务
     * 
     * @param loginUser
     * @param timedTask
     * @return com.ren.common.domain.model.dto.AjaxResult
     * @author ren
     * @date 2025/06/23 15:20
     */
    @PostMapping("/add")
    @OperLogAnn(title = "定时任务模块", businessType = BusinessType.INSERT)
    @Operation(summary = "添加定时任务", description = "添加定时任务")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult addTimedTask(@AuthenticationPrincipal LoginUser loginUser,
								   @RequestBody(required = false) TimedTask timedTask) {
        // 判断任务是否合规
        String result = QuartzManager.isTaskCompliant(timedTask);
        if (!StringUtils.equals("任务合规", result))
            return error(result);
        try {
            // 添加任务
            timedTaskService.addTimedTask(timedTask, loginUser.getUsername());
            return success();
        } catch (SchedulerException | QuartzException e) {
            return error("添加定时任务失败，请稍后重试");
        }
    }

    /**
     * 编辑定时任务
     * 
     * @param loginUser
     * @param timedTask
     * @return com.ren.common.domain.model.dto.AjaxResult
     * @author ren
     * @date 2025/06/25 14:09
     */
    @PostMapping("/modify")
    @OperLogAnn(title = "定时任务模块", businessType = BusinessType.INSERT)
    @Operation(summary = "编辑定时任务", description = "编辑定时任务")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult modifyTimedTask(@AuthenticationPrincipal LoginUser loginUser,
									  @RequestBody(required = false) TimedTask timedTask) {
        // 判断任务是否合规
        String result = QuartzManager.isTaskCompliant(timedTask);
        if (!StringUtils.equals("任务合规", result))
            return error(result);
        try {
            // 编辑任务
            timedTaskService.modifyTimedTaskById(timedTask, loginUser.getUsername());
            return success();
        } catch (SchedulerException | QuartzException e) {
            return error("编辑定时任务失败，请稍后重试");
        }
    }

    /**
     * 修改定时任务状态
     * 
     * @param loginUser
     * @return com.ren.common.domain.model.dto.AjaxResult
     * @author ren
     * @date 2025/06/25 14:17
     */
    @PostMapping("/modifyStatus")
    @OperLogAnn(title = "定时任务模块", businessType = BusinessType.INSERT)
    @Operation(summary = "修改定时任务状态", description = "修改定时任务状态")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult modifyTimedTaskStatus(@AuthenticationPrincipal LoginUser loginUser,
											@RequestBody(required = false) TimedTask timedTask) {
        try {
            timedTaskService.modifyTimedTaskStatusById(timedTask.getTaskId(),
                    timedTask.getStatus(), loginUser.getUsername());
            return success();
        } catch (SchedulerException e) {
            return error("操作失败，请稍后重试");
        }
    }

    /**
     * 删除定时任务
     * 
     * @param taskId
     * @return com.ren.common.domain.model.dto.AjaxResult
     * @author ren
     * @date 2025/06/25 14:18
     */
    @DeleteMapping("/delete")
    @OperLogAnn(title = "定时任务模块", businessType = BusinessType.DELETE)
    @Operation(summary = "删除定时任务", description = "删除定时任务")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult deleteTimedTask(@Schema(description = "任务标识",type="long") long taskId) {
        try {
            timedTaskService.removeTimedTaskById(taskId);
            return success();
        } catch (SchedulerException e) {
            return error("操作失败，请稍后重试");
        }
    }

    /*=================================================TimedTaskLog===================================================*/

    /**
     * 分页获取任务日志列表
     * @param searchLike
     * @return com.ren.common.core.page.TableDataInfo
     * @author ren
     * @date 2025/07/19 13:30
     */
    @GetMapping("/log/list/page")
    @Pageable // 注意，如果要开启分页，请添加该注解
    @Operation(summary = "分页获取任务日志列表", description = "分页获取任务日志列表")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableDataInfo.class)))
    public TableDataInfo listTimedTaskLogByPage(@RequestParam(required = false) @Schema(description = "模糊查询") String searchLike) {
        IPage<TimedTaskLog> timedTaskLogList = timedTaskService.listTimedTaskLogByPage(searchLike);
        return getDataTable(timedTaskLogList);
    }

    /**
     * 删除定时任务日志
     * @param taskLogId
     * @return com.ren.common.core.response.AjaxResult
     * @author ren
     * @date 2025/07/19 14:01
     */
    @DeleteMapping("/log/delete")
    @OperLogAnn(title = "定时任务日志模块", businessType = BusinessType.DELETE)
    @Operation(summary = "删除定时任务日志", description = "删除定时任务日志")
    @SecurityRequirements({@SecurityRequirement(name = "JWT")})
    @ApiResponse(responseCode = "200",description = "成功",content = @Content(mediaType = "application/json", schema = @Schema(implementation = AjaxResultSchemas.class)))
    public AjaxResult deleteTimedTaskLog(@Schema(description = "日志标识",type = "long") long taskLogId) {
        try {
            timedTaskService.removeTimedTaskLogById(taskLogId);
            return success();
        } catch (SchedulerException e) {
            return error("操作失败，请稍后重试");
        }
    }

}