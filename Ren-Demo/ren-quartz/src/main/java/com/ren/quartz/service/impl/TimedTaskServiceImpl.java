package com.ren.quartz.service.impl;

import java.util.List;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ren.common.utils.DateUtils;
import com.ren.common.utils.PageUtils;
import com.ren.common.utils.StringUtils;
import com.ren.quartz.core.constant.QuartzContents;
import com.ren.quartz.core.domain.entity.TimedTask;
import com.ren.quartz.core.domain.entity.TimedTaskLog;
import com.ren.quartz.core.exception.QuartzException;
import com.ren.quartz.manager.QuartzManager;
import com.ren.quartz.mapper.TimedTaskLogMapper;
import com.ren.quartz.mapper.TimedTaskMapper;
import com.ren.quartz.service.TimedTaskService;

import cn.hutool.core.convert.Convert;

@Service
public class TimedTaskServiceImpl extends ServiceImpl<TimedTaskMapper, TimedTask> implements TimedTaskService {

    @Autowired
    private TimedTaskMapper timedTaskMapper;
    @Autowired
    private TimedTaskLogMapper timedTasklogMapper;
    // Quartz调度器
    @Autowired
    private Scheduler scheduler;

    /*===================================================TimedTask====================================================*/

    /**
     * 添加定时任务
     * 
     * @param createBy
     * @return long
     * @author ren
     * @date 2025/05/07 17:12
     */
    @Override
    @Transactional
    public long addTimedTask(TimedTask timedTask, String createBy) throws SchedulerException, QuartzException {
        // 数据库添加定时任务
        timedTask.setCreateBy(createBy);
        timedTask.setCreateTime(DateUtils.currentSeconds());
        int result = timedTaskMapper.insertTimedTask(timedTask);
        // 将定时任务映射到quartz
        if (result > 0) {
            // 创建Quartz任务
            QuartzManager.createScheduleJob(scheduler, timedTask);
        }
        return timedTask.getTaskId();
    }

    /**
     * 编辑定时任务是否删除
     * 
     * @param taskId
     * @param status
     * @param updateBy
     * @author ren
     * @date 2025/05/07 17:13
     */
    @Override
    @Transactional
    public void modifyTimedTaskStatusById(long taskId, int status, String updateBy) throws SchedulerException {
        timedTaskMapper.updateTimedTaskStatusById(taskId, status, updateBy, DateUtils.currentSeconds());
        // 根据TaskId重新获取任务
        TimedTask timedTask = getTimedTaskById(taskId);
        if (status == Convert.toInt(QuartzContents.Status.NORMAL.getValue())) {
            // 任务状态正常，启动任务
            scheduler.resumeJob(QuartzManager.getJobKey(timedTask.getTaskId(), timedTask.getTaskGroup()));
        } else {
            // 任务状态暂停，暂停任务
            scheduler.pauseJob(QuartzManager.getJobKey(timedTask.getTaskId(), timedTask.getTaskGroup()));
        }
    }

    /**
     * 编辑定时任务
     * 
     * @param timedTask
     * @param updateBy
     * @author ren
     * @date 2025/05/07 17:13
     */
    @Override
    @Transactional
    public void modifyTimedTaskById(TimedTask timedTask, String updateBy) throws SchedulerException, QuartzException {
        // 先不修改，先查询老的任务，用于后面删除任务
        TimedTask oldTimedTask = getTimedTaskById(timedTask.getTaskId());
        // 更新数据库任务
        timedTask.setUpdateBy(updateBy);
        timedTask.setUpdateTime(DateUtils.currentSeconds());
        int result = timedTaskMapper.updateTimedTaskById(timedTask);
        if (result > 0) {
            // 先删除任务，之后重新创建（虽然创建任务内部拥有删除功能，但是当taskGroup有变动时，内部删除将会失效）
            boolean isSuccess =
                QuartzManager.removeJob(scheduler, oldTimedTask.getTaskId(), oldTimedTask.getTaskGroup());
            if (!isSuccess) {
                throw new SchedulerException("删除原定时任务失败");
            }
            // 重新创建任务
            QuartzManager.createScheduleJob(scheduler, timedTask);
        }
    }

    /**
     * 获取定时任务详情
     * 
     * @param taskId
     * @return com.ren.common.core.entity.TimedTask
     * @author ren
     * @date 2025/05/07 17:14
     */
    @Override
    public TimedTask getTimedTaskById(long taskId) {
        TimedTask timedTask = timedTaskMapper.selectById(taskId);
        return timedTask;
    }

    /**
     * 根据参数获取定时任务列表
     * 
     * @param searchLike
     * @return java.util.List<com.ren.common.core.entity.TimedTask>
     * @author ren
     * @date 2025/05/07 17:15
     */
    @Override
    public IPage<TimedTask> listTimedTaskByPage(String searchLike) {
        if (StringUtils.isNotBlank(searchLike)) {
            searchLike = StringUtils.format("%%{}%%", searchLike);
        }
        IPage<TimedTask> timedTaskList =
            timedTaskMapper.listTimedTaskByPage(PageUtils.createPage(TimedTask.class), searchLike);
        return timedTaskList;
    }

    /**
     * 不分页获取定时任务列表
     * 
     * @param status
     * @return java.util.List<com.ren.quartz.domain.entity.TimedTask>
     * @author ren
     * @date 2025/06/23 15:15
     */
    @Override
    public List<TimedTask> listTimedTaskByParam(Integer status) {
        List<TimedTask> timedTaskList = timedTaskMapper.listTimedTaskByParam(status);
        return timedTaskList;
    }

    /**
     * 根据任务ID删除定时任务
     * 
     * @param taskId
     * @author ren
     * @date 2025/06/23 15:17
     */
    @Override
    @Transactional
    public void removeTimedTaskById(long taskId) throws SchedulerException {
        // 先获取原任务
        TimedTask oldTimedTask = getTimedTaskById(taskId);
        // 删除任务
        timedTaskMapper.deleteTimedTaskById(taskId);
        boolean isSuccess = oldTimedTask == null
            | QuartzManager.removeJob(scheduler, oldTimedTask.getTaskId(), oldTimedTask.getTaskGroup());
        if (!isSuccess) {
            throw new SchedulerException("删除定时任务失败");
        }
    }

    /*===================================================TimedTaskLog=================================================*/

    /**
     * 添加定时任务日志
     * 
     * @return long
     * @author ren
     * @date 2025/05/07 17:12
     */
    @Override
    public long addTimedTaskLog(TimedTaskLog timedTaskLog) {
        timedTaskLog.setCreateTime(DateUtils.currentSeconds());
        timedTasklogMapper.insertTimedTaskLog(timedTaskLog);
        return timedTaskLog.getTaskLogId();
    }

    /**
     * 分页获取定时任务日志列表
     * 
     * @param searchLike
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ren.quartz.core.domain.entity.TimedTaskLog>
     * @author ren
     * @date 2025/07/19 13:28
     */
    @Override
    public IPage<TimedTaskLog> listTimedTaskLogByPage(String searchLike) {
        if (StringUtils.isNotBlank(searchLike)) {
            searchLike = StringUtils.format("%%{}%%", searchLike);
        }
        IPage<TimedTaskLog> timedTaskList =
                timedTasklogMapper.listTimedTaskLogByPage(PageUtils.createPage(TimedTaskLog.class), searchLike);
        return timedTaskList;
    }

    /**
     * 根据参数获取定时任务日志列表
     * 
     * @param status
     * @return java.util.List<com.ren.common.core.entity.TimedTaskLog>
     * @author ren
     * @date 2025/05/07 17:15
     */
    @Override
    public List<TimedTaskLog> listTimedTaskLogByParam(Integer status) {
        List<TimedTaskLog> timedTaskLogList = timedTasklogMapper.listTimedTaskLogByParam(status);
        return timedTaskLogList;
    }

    /**
     * 删除定时任务日志
     * @param taskLogId
     * @author ren
     * @date 2025/07/19 13:58
     */
    @Override
    public void removeTimedTaskLogById(long taskLogId) throws SchedulerException {
        timedTasklogMapper.deleteTimedTaskLogById(taskLogId);
    }

}