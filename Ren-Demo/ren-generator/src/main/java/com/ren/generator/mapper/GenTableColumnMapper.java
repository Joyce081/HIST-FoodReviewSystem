package com.ren.generator.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ren.generator.core.domain.entity.GenTableColumn;

/**
 * GenTableColumnMapper 列Mapper
 *
 * @author ren
 * @version 2025/07/28 20:49
 **/
@Mapper
public interface GenTableColumnMapper extends BaseMapper<GenTableColumn> {

    /**
     * 添加列信息
     *
     * @param genTableColumn
     * @return int
     * @author ren
     * @date 2025/07/28 17:59
     */
    void insertGenTableColumn(GenTableColumn genTableColumn);

    /**
     * 删除业务字段信息
     * 
     * @param tableIds
     * @return int
     * @author ren
     * @date 2025/08/02 17:44
     */
    int deleteGenTableColumnByTableIds(Long[] tableIds);

    /**
     * 删除业务字段信息
     *
     * @param columnIds
     * @return int
     * @author ren
     * @date 2025/08/02 17:43
     */
    int deleteGenTableColumnByIds(Long[] columnIds);

    /**
     * 编辑列信息
     *
     * @param genTableColumn
     * @author ren
     * @date 2025/07/28 18:00
     */
    void updateGenTableColumnById(GenTableColumn genTableColumn);

    /**
     * 获取列信息列表
     * 
     * @param tableName
     * @return java.util.List<com.ren.generator.domain.entity.GenTableColumn>
     * @author ren
     * @date 2025/07/29 10:02
     */
    List<GenTableColumn> listGenTableColumnByTableName(String tableName);

    /**
     * 根据表id获取列信息
     * 
     * @param tableId
     * @return java.util.List<com.ren.generator.core.domain.entity.GenTableColumn>
     * @author ren
     * @date 2025/08/07 13:13
     */
    List<GenTableColumn> listGenTableColumnByTableId(long tableId);

}
