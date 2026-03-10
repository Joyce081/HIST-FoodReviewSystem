package com.ren.generator.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ren.generator.core.domain.entity.GenTable;

/**
 * GenTableMapper 表Mapper
 *
 * @author ren
 * @version 2025/07/28 20:49
 **/
@Mapper
public interface GenTableMapper extends BaseMapper<GenTable> {

    /**
     * 添加表信息
     *
     * @param genTable
     * @return int
     * @author ren
     * @date 2025/07/28 18:00
     */
    void insertGenTable(GenTable genTable);

    /**
     * 批量删除业务
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteGenTableByIds(Long[] ids);

    /**
     * 编辑表信息
     *
     * @param genTable
     * @author ren
     * @date 2025/07/28 18:01
     */
    void updateGenTableById(GenTable genTable);

    /**
     * 创建表
     * 
     * @param sqlStr
     * @return int
     * @author ren
     * @date 2025/08/10 18:02
     */
    int createTable(String sqlStr);

    /**
     * 获取表信息列表
     *
     * @param searchLike
     * @return java.util.List<com.ren.generator.domain.entity.GenTable>
     * @author ren
     * @date 2025/07/28 18:01
     */
    IPage<GenTable> listGenTableByPage(Page<GenTable> page, String searchLike);

    /**
     * 直接从数据库中获取所有表信息
     *
     * @return java.util.List<com.ren.generator.domain.entity.GenTable>
     * @author ren
     * @date 2025/07/28 21:50
     */
    IPage<GenTable> listGenTableForDatabase(Page<GenTable> page, String searchLike);

    /**
     * 根据参数获取表信息
     * 
     * @return java.util.List<com.ren.generator.core.domain.entity.GenTable>
     * @author ren
     * @date 2025/08/07 13:18
     */
    List<GenTable> listGenTableByParam(@Param("notInTableIds") Long[] notInTableIds);

    /**
     * 获取表信息
     * 
     * @param tableId
     * @return com.ren.generator.core.domain.entity.GenTable
     * @author ren
     * @date 2025/08/07 13:54
     */
    GenTable selectGenTableById(long tableId);

    /**
     * 获取表信息
     * 
     * @param subTableName
     * @return com.ren.generator.core.domain.entity.GenTable
     * @author ren
     * @date 2025/08/13 15:31
     */
    GenTable selectGenTableByTableName(String subTableName);

    /**
     * 根据表名查询数据库表信息
     * 
     * @param tableNames
     * @return java.util.List<com.ren.generator.core.domain.entity.GenTable>
     * @author ren
     * @date 2025/08/10 18:07
     */
    public List<GenTable> selectDbTableListByNames(String[] tableNames);

}
