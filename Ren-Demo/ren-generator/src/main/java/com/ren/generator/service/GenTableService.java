package com.ren.generator.service;

import java.io.IOException;
import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ren.generator.core.domain.dto.CreateTableDTO;
import com.ren.generator.core.domain.entity.GenTable;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GenTableService 表接口
 *
 * @author ren
 * @version 2025/07/28 17:54
 **/
public interface GenTableService {

    /**
     * 添加表信息
     * 
     * @param tableList
     * @return int
     * @author ren
     * @date 2025/07/28 18:00
     */
    void addGenTable(List<GenTable> tableList, String createBy);

    /**
     * 删除业务信息
     * 
     * @param tableIds
     * @author ren
     * @date 2025/08/02 17:42
     */
    void deleteGenTableByIds(Long[] tableIds);

    /**
     * 编辑表信息
     * 
     * @param genTable
     * @param updateBy
     * @author ren
     * @date 2025/07/28 18:01
     */
    void modifyGenTableById(GenTable genTable, String updateBy);

    /**
     * 创建表主方法
     *
     * @param createTableDTO
     * @param createBy
     * @author ren
     * @date 2025/08/10 18:16
     */
    void createTableMain(CreateTableDTO createTableDTO, String createBy);

    /**
     * 创建表
     * 
     * @param sqlStr
     * @return boolean
     * @author ren
     * @date 2025/08/10 18:01
     */
    boolean createTable(String sqlStr);

    /**
     * 同步表结构
     * 
     * @param tableId
     * @param username
     * @author ren
     * @date 2025/08/10 19:03
     */
    void refreshTableMain(Long tableId, String username);

    /**
     * 获取表信息详情
     * 
     * @param tableId
     * @return com.ren.generator.domain.entity.GenTable
     * @author ren
     * @date 2025/07/28 18:01
     */
    GenTable getGenTableById(long tableId);

    /**
     * 获取表信息列表
     * 
     * @param searchLike
     * @return java.util.List<com.ren.generator.domain.entity.GenTable>
     * @author ren
     * @date 2025/07/28 18:01
     */
    IPage<GenTable> listGenTableByPage(String searchLike);

    /**
     * 直接从数据库中获取所有表信息
     *
     * @return java.util.List<com.ren.generator.domain.entity.GenTable>
     * @author ren
     * @date 2025/07/28 21:50
     */
    IPage<GenTable> listGenTableForDatabase(String searchLike);

    /**
     * 根据参数获取表信息
     * 
     * @return java.util.List<com.ren.generator.core.domain.entity.GenTable>
     * @author ren
     * @date 2025/08/07 13:16
     */
    List<GenTable> listGenTableByParam(Long[] notInTableIds);

    /**
     * 根据表名查询数据库表信息
     * 
     * @param tableNames
     * @return java.util.List<com.ren.generator.core.domain.entity.GenTable>
     * @author ren
     * @date 2025/08/10 18:07
     */
    List<GenTable> selectDbTableListByNames(String[] tableNames);

    /**
     * 生成代码到指定路径
     *
     * @param tableIds
     * @author ren
     * @date 2025/08/13 15:17
     */
    void genCode(long[] tableIds);

    /**
     * 生成代码并下载（压缩包形式）
     *
     * @param tableIds
     * @author ren
     * @date 2025/08/13 15:17
     */
    byte[] genCodeZip(HttpServletResponse response, long[] tableIds) throws IOException;
}
