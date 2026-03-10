
package com.ren.quartz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ren.quartz.core.domain.entity.TimedTaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TimedTaskLogMapper extends BaseMapper<TimedTaskLog> {

    /**
     * 添加定时任务日志
     * @param timedTaskLog
     * @author ren
     * @date 2025/05/07 17:38
     */
    void insertTimedTaskLog(TimedTaskLog timedTaskLog);

    /**
     * 根据参数获取定时任务日志列表
     * @param status
     * @return java.util.List<com.ren.common.core.entity.TimedTaskLog>
     * @author ren
     * @date 2025/05/07 17:31
     */
    List<TimedTaskLog> listTimedTaskLogByParam(Integer status);

    /**
     * 分页获取定时任务日志列表
     *
     * @param searchLike
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ren.quartz.core.domain.entity.TimedTaskLog>
     * @author ren
     * @date 2025/07/19 13:28
     */
    IPage<TimedTaskLog> listTimedTaskLogByPage(Page<TimedTaskLog> page, @Param("searchLike") String searchLike);

    /**
     * 删除定时任务日志
     * @param taskLogId
     * @author ren
     * @date 2025/07/19 13:59
     */
    void deleteTimedTaskLogById(long taskLogId);
}