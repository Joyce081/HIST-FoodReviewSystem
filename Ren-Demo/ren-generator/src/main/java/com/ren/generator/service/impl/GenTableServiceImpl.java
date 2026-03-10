package com.ren.generator.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ren.common.core.constant.Constants;
import com.ren.common.core.exception.ServiceException;
import com.ren.common.utils.DateUtils;
import com.ren.common.utils.PageUtils;
import com.ren.common.utils.SqlUtils;
import com.ren.common.utils.StringUtils;
import com.ren.common.utils.json.FastJSON2Utils;
import com.ren.generator.core.constant.GenConstants;
import com.ren.generator.core.domain.dto.CreateTableDTO;
import com.ren.generator.core.domain.entity.GenTable;
import com.ren.generator.core.domain.entity.GenTableColumn;
import com.ren.generator.mapper.GenTableMapper;
import com.ren.generator.service.GenTableColumnService;
import com.ren.generator.service.GenTableService;
import com.ren.generator.utils.GenUtils;
import com.ren.generator.utils.VelocityUtils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import jakarta.servlet.http.HttpServletResponse;

/**
 * GenTableServiceImpl 表接口实现
 *
 * @author ren
 * @version 2025/07/28 20:47
 **/
@Service
public class GenTableServiceImpl extends ServiceImpl<GenTableMapper, GenTable> implements GenTableService {

    @Autowired
    private GenTableMapper genTableMapper;
    @Autowired
    private GenTableColumnService genTableColumnService;

    /**
     * 添加表信息
     * 
     * @param tableList
     * @return int
     * @author ren
     * @date 2025/07/28 18:00
     */
    @Override
    @Transactional
    public void addGenTable(List<GenTable> tableList, String createBy) {
        Optional.ofNullable(tableList).orElseGet(CollUtil::newArrayList).forEach(genTable -> {
            // 添加表信息（除表名和表说明之外的其他字段）
            GenUtils.assembleGenTable(genTable, createBy);
            genTableMapper.insertGenTable(genTable);
            // 添加表中列的信息
            List<GenTableColumn> genTableColumnList =
                genTableColumnService.listGenTableColumnByTableName(genTable.getTableName());
            genTableColumnService.addGenTableColumn(genTableColumnList, genTable.getTableId(), createBy);
        });
    }

    /**
     * 删除业务信息
     * 
     * @param tableIds
     * @author ren
     * @date 2025/08/02 17:42
     */
    @Override
    @Transactional
    public void deleteGenTableByIds(Long[] tableIds) {
        genTableMapper.deleteGenTableByIds(tableIds);
        genTableColumnService.deleteGenTableColumnByTableIds(tableIds);
    }

    /**
     * 编辑表信息
     * 
     * @param genTable
     * @param updateBy
     * @author ren
     * @date 2025/07/28 18:01
     */
    @Override
    @Transactional
    public void modifyGenTableById(GenTable genTable, String updateBy) {
        genTable.setOptions(FastJSON2Utils.toJson(genTable.getOptionsMaps()));
        // 修改表信息
        genTable.setUpdateBy(updateBy);
        genTable.setUpdateTime(DateUtils.currentSeconds());
        genTableMapper.updateGenTableById(genTable);
        // 修改表中列的信息
        Optional.ofNullable(genTable.getColumns()).orElseGet(CollUtil::newArrayList).forEach(column -> {
            genTableColumnService.modifyGenTableColumnById(column, updateBy);
        });
    }

    /**
     * 创建表主方法
     *
     * @param createTableDTO
     * @param createBy
     * @author ren
     * @date 2025/08/10 18:16
     */
    @Override
    @Transactional
    public void createTableMain(CreateTableDTO createTableDTO, String createBy) {
        // SQL安全过滤（防止SQL注入攻击）
        SqlUtils.filterKeyword(createTableDTO.getSqlStr());

        // 解析SQL语句（将多条Sql字符串转换为一个SQLStatement列表，数据库类型为Mysql）
        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(createTableDTO.getSqlStr(), DbType.mysql);

        // 处理建表语句
        List<String> tableNames = new ArrayList<>();
        for (SQLStatement sqlStatement : sqlStatements) {
            // 判断sqlStatement是否为Mysql类型
            if (sqlStatement instanceof MySqlCreateTableStatement) {
                // 强转为MySqlCreateTableStatement
                MySqlCreateTableStatement createTableStatement = (MySqlCreateTableStatement)sqlStatement;

                // 执行实际建表操作
                if (createTable(createTableStatement.toString())) {

                    // 收集创建成功的表名
                    String tableName = createTableStatement.getTableName().replaceAll("`", "");
                    tableNames.add(tableName);
                }
            }
        }

        // 获取创建成功的表结构信息
        List<GenTable> tableList = selectDbTableListByNames(tableNames.toArray(new String[0]));

        // 将表结构信息导入系统
        addGenTable(tableList, createBy);
    }

    /**
     * 创建表
     * 
     * @param sqlStr
     * @return boolean
     * @author ren
     * @date 2025/08/10 18:01
     */
    @Override
    public boolean createTable(String sqlStr) {
        return genTableMapper.createTable(sqlStr) == 0;
    }

    /**
     * 同步表结构
     *
     * @param tableId
     * @param createBy
     * @author ren
     * @date 2025/08/10 19:03
     */
    @Override
    @Transactional
    public void refreshTableMain(Long tableId, String createBy) {
        // 先获取代码生成模块表结构信息
        GenTable table = getGenTableById(tableId);
        List<GenTableColumn> tableColumns = table.getColumns();
        // 组装代码生成模块表结构信息为Map，key为字段名，value为字段信息本身
        Map<String, GenTableColumn> tableColumnMap =
            tableColumns.stream().collect(Collectors.toMap(GenTableColumn::getColumnName, Function.identity()));

        // 获取数据库实际表结构信息
        List<GenTableColumn> dbTableColumns = genTableColumnService.listGenTableColumnByTableName(table.getTableName());
        if (CollUtil.isEmpty(dbTableColumns)) {
            throw new ServiceException("同步数据失败，原表结构不存在");
        }
        // 组装数据库实际表结构信息为一个字段名列表
        List<String> dbTableColumnNames =
            dbTableColumns.stream().map(GenTableColumn::getColumnName).collect(Collectors.toList());

        List<GenTableColumn> addGenTableColumnList = new ArrayList<>();
        // 遍历数据库实际表结构信息列表
        dbTableColumns.forEach(column -> {
            // 判断字段是否已经在代码生成模块表结构信息列表中存在
            if (tableColumnMap.containsKey(column.getColumnName())) {
                // 组装新字段信息
                GenUtils.assembleGenTableColumn(column, tableId, createBy);
                // 获取原字段信息
                GenTableColumn prevColumn = tableColumnMap.get(column.getColumnName());
                // 新字段信息的columnId不变动
                column.setColumnId(prevColumn.getColumnId());
                // 判断当前字段是否在列表中展示
                if (column.isList()) {
                    // 如果是列表字段，则需要保留字典类型和查询方式
                    column.setDictType(prevColumn.getDictType());
                    column.setQueryType(prevColumn.getQueryType());
                }
                // 判断字段是否必填字段、是否主建字段、是否（可插入字段、可修改字段）、是否不可忽略字段、是否父类字段
                if (StringUtils.isNotBlank(prevColumn.getIsRequired()) && !column.isPk()
                    && (column.isInsert() || column.isEdit())
                    && ((!column.isSuperColumn()) || (column.isUsableColumn()))) {
                    // 保留字段是否必填以及html类型属性
                    column.setIsRequired(prevColumn.getIsRequired());
                    column.setHtmlType(prevColumn.getHtmlType());
                }
                // 字段信息已经存在，所以做修改操作
                genTableColumnService.modifyGenTableColumnById(column, createBy);
            } else {
                addGenTableColumnList.add(column);
            }
        });
        if (CollUtil.isNotEmpty(addGenTableColumnList)) {
            // 字段信息不存在，所以做添加操作
            genTableColumnService.addGenTableColumn(addGenTableColumnList, tableId, createBy);
        }

        // 对原来存在现在不存在的字段进行删除
        Long[] columnIds = tableColumns.stream().filter(column -> !dbTableColumnNames.contains(column.getColumnName()))
            .map(column -> column.getColumnId()).toArray(Long[]::new);
        if (ArrayUtil.isNotEmpty(columnIds)) {
            genTableColumnService.deleteGenTableColumnByIds(columnIds);
        }
    }

    /**
     * 获取表信息详情
     * 
     * @param tableId
     * @return com.ren.generator.domain.entity.GenTable
     * @author ren
     * @date 2025/07/28 18:01
     */
    @Override
    public GenTable getGenTableById(long tableId) {
        GenTable genTable = genTableMapper.selectGenTableById(tableId);
        // 设置冗余字段
        GenUtils.setTableFromOptions(genTable);
        return genTable;
    }

    /**
     * 获取表信息列表
     * 
     * @param searchLike
     * @return java.util.List<com.ren.generator.domain.entity.GenTable>
     * @author ren
     * @date 2025/07/28 18:01
     */
    @Override
    public IPage<GenTable> listGenTableByPage(String searchLike) {
        if (StringUtils.isNotBlank(searchLike)) {
            searchLike = StringUtils.format("%%{}%%", searchLike);
        }
        IPage<GenTable> genTableList =
            genTableMapper.listGenTableByPage(PageUtils.createPage(GenTable.class), searchLike);
        return genTableList;
    }

    /**
     * 直接从数据库中获取所有表信息
     *
     * @return java.util.List<com.ren.generator.domain.entity.GenTable>
     * @author ren
     * @date 2025/07/28 21:50
     */
    @Override
    public IPage<GenTable> listGenTableForDatabase(String searchLike) {
        if (StringUtils.isNotBlank(searchLike)) {
            searchLike = StringUtils.format("%%{}%%", searchLike);
        }
        IPage<GenTable> genTableList =
            genTableMapper.listGenTableForDatabase(PageUtils.createPage(GenTable.class), searchLike);
        return genTableList;
    }

    /**
     * 根据参数获取表信息
     * 
     * @return java.util.List<com.ren.generator.core.domain.entity.GenTable>
     * @author ren
     * @date 2025/08/07 13:16
     */
    @Override
    public List<GenTable> listGenTableByParam(Long[] notInTableIds) {
        List<GenTable> genTableList = genTableMapper.listGenTableByParam(notInTableIds);
        return genTableList;
    }

    /**
     * 根据表名查询数据库表信息
     * 
     * @param tableNames
     * @return java.util.List<com.ren.generator.core.domain.entity.GenTable>
     * @author ren
     * @date 2025/08/10 18:07
     */
    @Override
    public List<GenTable> selectDbTableListByNames(String[] tableNames) {
        return genTableMapper.selectDbTableListByNames(tableNames);
    }

    /**
     * 生成代码到指定路径
     *
     * @param tableIds
     * @author ren
     * @date 2025/08/13 15:17
     */
    @Override
    @Transactional
    public void genCode(long[] tableIds) {
        for (long tableId : tableIds) {
            // 表信息
            GenTable genTable = getGenTableById(tableId);
            if (ObjUtil.isEmpty(genTable))
                throw new ServiceException("表信息不存在");
            // 设置子表信息
            setSubTableInfo(genTable);
            // 设置主键信息
            setPkInfo(genTable);
            //初始化模版引擎
            VelocityUtils.initVelocity();
            // 准备模板上下文数据
            VelocityContext context = VelocityUtils.prepareContext(genTable);
            // 获取模板列表
            List<String> templates = VelocityUtils.getTemplateList(genTable.getTplCategory());
            for (String template : templates)
            {
                // 创建内存字符输出流
                StringWriter sw = new StringWriter();
                // 使用Velocity加载指定路径的模板文件
                Template tpl = Velocity.getTemplate(template, Constants.UTF8);
                // 将数据（context）注入模板（tpl），并将渲染后的效果放入sw中
                tpl.merge(context, sw);
				//获取最终文件输出路径
				String path = "";
				//先获取用户自定义的文件输出前置路径
				String genPath = genTable.getGenPath();
				//如果用户未定义，则使用项目根目录作为前置路径进行生成
				if (StringUtils.equals(genPath, "/"))
				{
					path = System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, genTable);
				}else{
					path = genPath + File.separator + VelocityUtils.getFileName(template, genTable);
				}
                // 写出内容到指定路径的文件中，如果路径不存在则会创建
				FileUtil.writeString(sw.toString(), path, StandardCharsets.UTF_8);
			}
        }
    }

    /**
     * 生成代码并下载（压缩包形式）
     *
     * @param tableIds
     * @author ren
     * @date 2025/08/13 15:18
     */
    @Override
    @Transactional
    public byte[] genCodeZip(HttpServletResponse response, long[] tableIds) throws IOException {
        // 创建内存字节流容器，将需要进行输出的内容全部存入当前容器，最后一起输出
        // 节点流（存储流），用于实际存储数据，给装饰器流使用
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 创建一个zip文件输出流，包装内存字节流容器
        // 装饰器流，仅提供一些操作功能，不负责存储数据，数据交由装饰器流包裹的存储流存储
        try (ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            // 循环遍历表主键信息，生成代码并全部写入zip文件输出流中
            for (long tableId : tableIds) {
                generatorCodeZip(tableId, zip);
            }
        } // 使用try-with-resources语法自动关闭zip装饰流（内部会自动关闭装饰器流所包装的节点流，无需手动关闭节点流，但是这里装饰流装饰的是一个内存流，实际上本身也不需要关闭），释放资源，强制刷新缓冲区并写入ZIP结束标记
        return outputStream.toByteArray();
    }

    /*==============================================extension_method==================================================*/

    /**
     * 生成代码写入ZIP文件输出流中
     * 
     * @param tableId
     * @param zip
     * @author ren
     * @date 2025/08/13 15:24
     */
    private void generatorCodeZip(long tableId, ZipOutputStream zip) {
        // 表信息
        GenTable genTable = getGenTableById(tableId);
        if (ObjUtil.isEmpty(genTable))
            throw new ServiceException("表信息不存在");
        // 设置子表信息
        setSubTableInfo(genTable);
        // 设置主键信息
        setPkInfo(genTable);
        if(StringUtils.isNotBlank(genTable.getSubTableFkName()) && ObjUtil.isNotEmpty(genTable.getSubTable()) && CollUtil.isNotEmpty(genTable.getSubTable().getColumns())){
            // 设置外键信息
            setFkInfo(genTable);
        }

        //初始化模版引擎
        VelocityUtils.initVelocity();
        // 准备模板上下文数据
        VelocityContext context = VelocityUtils.prepareContext(genTable);
        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(genTable.getTplCategory());
        for (String template : templates) {
            // 创建内存字符流，用于存储模板渲染后的结果
            StringWriter sw = new StringWriter();
            // 使用Velocity加载指定路径的模板文件
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            // 将数据（context）注入模板（tpl），并将渲染后的效果放入sw中
            tpl.merge(context, sw);
            try {
                // 根据模板和数据获取最终文件的文件名和路径，创建一个ZIP条目
                zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, genTable)));
                // 将渲染结果写入当前ZIP条目(不关闭当前输入流)
                IoUtil.write(zip, StandardCharsets.UTF_8, false, sw.toString());
                // 安全关闭内存字符流（内存流不需要关闭，他的close方法是一个空操作，当然为了规范，关闭了也可以）
                IoUtil.close(sw);
                // 刷新ZIP流确保数据写入
                zip.flush();
                // 关闭当前ZIP条目
                zip.closeEntry();
            } catch (IOException e) {
                log.error("渲染模板失败，表名：" + genTable.getTableName(), e);
            }
        }
    }

    /**
     * 设置子表信息
     * 
     * @param table
     * @author ren
     * @date 2025/08/13 15:30
     */
    public void setSubTableInfo(GenTable table) {
        String subTableName = table.getSubTableName();
        if (StringUtils.isNotBlank(subTableName)) {
            table.setSubTable(genTableMapper.selectGenTableByTableName(subTableName));
        }
    }

    /**
     * 设置主键列信息
     * 
     * @param table
     * @author ren
     * @date 2025/08/13 15:32
     */
    public void setPkInfo(GenTable table) {
        // 循环遍历表列信息，找出主键列信息，冗余进表中
        for (GenTableColumn column : table.getColumns()) {
            if (column.isPk()) {
                table.setPkColumn(column);
                break;
            }
        }
        // 如果没有主键列信息，则将第一个列信息作为主键列信息
        if (ObjUtil.isEmpty(table.getPkColumn())) {
            table.setPkColumn(table.getColumns().get(0));
        }

        // 如果生成类型是主子表生成，还需要获取子表的主键列信息
        if (GenConstants.TPL_SUB.equals(table.getTplCategory())) {
            for (GenTableColumn column : table.getSubTable().getColumns()) {
                if (column.isPk()) {
                    table.getSubTable().setPkColumn(column);
                    break;
                }
            }
            if (ObjUtil.isEmpty(table.getSubTable().getPkColumn())) {
                table.getSubTable().setPkColumn(table.getSubTable().getColumns().get(0));
            }
        }
    }

    /**
     * 设置外键信息
     *
     * @param table
     * @author ren
     * @date 2025/08/18 14:23
     */
    public void setFkInfo(GenTable table) {
        // 循环遍历表列信息，找出外键列信息，冗余进表中
        for(GenTableColumn subColumn : table.getSubTable().getColumns()){
            if(StringUtils.equals(subColumn.getColumnName(), table.getSubTableFkName())){
                table.getSubTable().setFkColumn(subColumn);
                break;
            }
        }

        // 如果没有外键列信息，则选择原表主键作为子表外键列信息
        if (ObjUtil.isEmpty(table.getSubTable().getFkColumn())) {
            table.getSubTable().setFkColumn(table.getPkColumn());
        }
    }

}
