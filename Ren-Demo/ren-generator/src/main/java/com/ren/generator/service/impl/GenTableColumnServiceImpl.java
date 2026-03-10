
package com.ren.generator.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ren.common.utils.DateUtils;
import com.ren.generator.core.domain.entity.GenTableColumn;
import com.ren.generator.mapper.GenTableColumnMapper;
import com.ren.generator.service.GenTableColumnService;
import com.ren.generator.utils.GenUtils;

import cn.hutool.core.collection.CollUtil;

/**
 * GenTableColumnServiceImpl 列接口实现
 *
 * @author ren
 * @version 2025/07/28 20:47
 **/
@Service
public class GenTableColumnServiceImpl extends ServiceImpl<GenTableColumnMapper, GenTableColumn>
    implements GenTableColumnService {

    @Autowired
    private GenTableColumnMapper genTableColumnMapper;

    /**
     * 添加列信息
     * 
     * @param genTableColumnList
     * @return int
     * @author ren
     * @date 2025/07/28 17:59
     */
    @Override
    public void addGenTableColumn(List<GenTableColumn> genTableColumnList, Long tableId, String createBy) {
        Optional.ofNullable(genTableColumnList).orElseGet(CollUtil::newArrayList).forEach(genTableColumn -> {
            GenUtils.assembleGenTableColumn(genTableColumn, tableId, createBy);
            genTableColumnMapper.insertGenTableColumn(genTableColumn);
        });
    }

    /**
     * 删除业务字段信息
     * 
     * @param tableIds
     * @return int
     * @author ren
     * @date 2025/08/02 17:43
     */
    @Override
    public int deleteGenTableColumnByTableIds(Long[] tableIds) {
        return genTableColumnMapper.deleteGenTableColumnByTableIds(tableIds);
    }

    /**
     * 删除业务字段信息
     *
     * @param columnIds
     * @return int
     * @author ren
     * @date 2025/08/02 17:43
     */
    @Override
    public int deleteGenTableColumnByIds(Long[] columnIds) {
        return genTableColumnMapper.deleteGenTableColumnByIds(columnIds);
    }

    /**
     * 编辑列信息
     * 
     * @param genTableColumn
     * @param updateBy
     * @author ren
     * @date 2025/07/28 18:00
     */
    @Override
    public void modifyGenTableColumnById(GenTableColumn genTableColumn, String updateBy) {
        genTableColumn.setUpdateBy(updateBy);
        genTableColumn.setUpdateTime(DateUtils.currentSeconds());
        genTableColumnMapper.updateGenTableColumnById(genTableColumn);
    }

    /**
     * 获取列信息
     * 
     * @param columnId
     * @return com.ren.generator.domain.entity.GenTableColumn
     * @author ren
     * @date 2025/07/28 18:00
     */
    @Override
    public GenTableColumn getGenTableColumnById(long columnId) {
        GenTableColumn genTableColumn = genTableColumnMapper.selectById(columnId);
        return genTableColumn;
    }

    /**
     * 直接从数据库中获取所有表信息
     *
     * @return java.util.List<com.ren.generator.domain.entity.GenTable>
     * @author ren
     * @date 2025/07/28 21:50
     */
    @Override
    public List<GenTableColumn> listGenTableColumnByTableName(String tableName) {
        List<GenTableColumn> genTableColumnList = genTableColumnMapper.listGenTableColumnByTableName(tableName);
        return genTableColumnList;
    }

    /**
     * 根据表id获取列信息
     * 
     * @param tableId
     * @return java.util.List<com.ren.generator.core.domain.entity.GenTableColumn>
     * @author ren
     * @date 2025/08/07 13:12
     */
    @Override
    public List<GenTableColumn> listGenTableColumnByTableId(long tableId) {
        List<GenTableColumn> genTableColumnList = genTableColumnMapper.listGenTableColumnByTableId(tableId);
        return genTableColumnList;
    }
}
