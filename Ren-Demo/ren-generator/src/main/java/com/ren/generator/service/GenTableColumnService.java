package com.ren.generator.service;

import java.util.List;

import com.ren.generator.core.domain.entity.GenTableColumn;

/**
 * GenTableColumnService 列接口
 * 
 * @author ren
 * @version 2025/07/28 17:53
 **/
public interface GenTableColumnService {

    /**
     * 添加列信息
     * 
     * @param genTableColumnList
     * @return int
     * @author ren
     * @date 2025/07/28 17:59
     */
    void addGenTableColumn(List<GenTableColumn> genTableColumnList, Long tableId, String createBy);

    /**
     * 删除业务字段信息
     * 
     * @param tableIds
     * @return int
     * @author ren
     * @date 2025/08/02 17:43
     */
    public int deleteGenTableColumnByTableIds(Long[] tableIds);

    /**
     * 删除业务字段信息
     * 
     * @param columnIds
     * @return int
     * @author ren
     * @date 2025/08/02 17:43
     */
    public int deleteGenTableColumnByIds(Long[] columnIds);

    /**
     * 编辑列信息
     * 
     * @param genTableColumn
     * @param updateBy
     * @author ren
     * @date 2025/07/28 18:00
     */
    void modifyGenTableColumnById(GenTableColumn genTableColumn, String updateBy);

    /**
     * 获取列信息
     * 
     * @param columnId
     * @return com.ren.generator.domain.entity.GenTableColumn
     * @author ren
     * @date 2025/07/28 18:00
     */
    GenTableColumn getGenTableColumnById(long columnId);

    /**
     * 根据表明获取数据库表字段信息
     * 
     * @param tableName
     * @return java.util.List<com.ren.generator.domain.entity.GenTableColumn>
     * @author ren
     * @date 2025/07/28 21:51
     */
    List<GenTableColumn> listGenTableColumnByTableName(String tableName);

    /**
     * 根据表id获取列信息
     * 
     * @param tableId
     * @return java.util.List<com.ren.generator.core.domain.entity.GenTableColumn>
     * @author ren
     * @date 2025/08/07 13:12
     */
    List<GenTableColumn> listGenTableColumnByTableId(long tableId);
}
