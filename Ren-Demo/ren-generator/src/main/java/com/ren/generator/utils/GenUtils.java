package com.ren.generator.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RegExUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ren.common.core.exception.ServiceException;
import com.ren.common.utils.DateUtils;
import com.ren.common.utils.StringUtils;
import com.ren.generator.core.constant.GenConstants;
import com.ren.generator.core.domain.entity.GenTable;
import com.ren.generator.core.domain.entity.GenTableColumn;
import com.ren.generator.properties.GenProperties;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;

/**
 * GenUtils 代码生成工具类
 *
 * @author ren
 * @version 2025/07/28 16:23
 **/
public class GenUtils {

    /**
     * 组装表信息
     * @param genTable
     * @author ren
     * @date 2025/08/04 09:53
     */
    public static void assembleGenTable(GenTable genTable,String createBy) {
        genTable.setClassName(convertClassName(genTable.getTableName()));
        genTable.setPackageName(GenProperties.getPackageName());
        genTable.setModuleName(getModuleName(GenProperties.getPackageName()));
        genTable.setBusinessName(getBusinessName(genTable.getTableName()));
        genTable.setFunctionName(replaceText(genTable.getTableComment()));
        genTable.setGenType(GenConstants.GEN_GENTYPE_ZIP);
        genTable.setFunctionAuthor(GenProperties.getAuthor());
        genTable.setCreateBy(createBy);
        genTable.setCreateTime(DateUtils.currentSeconds());
    }

    /**
     * 表名转换成Java类名
     *
     * @param tableName 表名称
     * @return 类名
     */
    public static String convertClassName(String tableName)
    {
        //是否自动删除前缀
        boolean autoRemovePre = GenProperties.getAutoRemovePre();
        //需要删除的前缀
        String tablePrefix = GenProperties.getTablePrefix();
        if (autoRemovePre && StringUtils.isNotBlank(tablePrefix))
        {
            List<String> searchList = StringUtils.split(tablePrefix, ",");
            tableName = replaceFirst(tableName, searchList);
        }
        return StringUtils.convertToCamelCase(tableName);
    }

    /**
     * 获取模块名
     *
     * @param packageName 包名
     * @return 模块名
     */
    public static String getModuleName(String packageName)
    {
        int lastIndex = packageName.lastIndexOf(".");
        int nameLength = packageName.length();
        return StringUtils.sub(packageName, lastIndex + 1, nameLength);
    }

    /**
     * 获取业务名
     *
     * @param tableName 表名
     * @return 业务名
     */
    public static String getBusinessName(String tableName)
    {
        int lastIndex = tableName.lastIndexOf("_");
        int nameLength = tableName.length();
        return StringUtils.sub(tableName, lastIndex + 1, nameLength);
    }

    /**
     * 关键字替换
     *
     * @param text 需要被替换的名字
     * @return 替换后的名字
     */
    public static String replaceText(String text)
    {
        return RegExUtils.replaceAll(text, "(?:表|任)", "");
    }

    /**
     * 批量替换前缀
     *
     * @param replacementm 替换值
     * @param searchList 替换列表
     * @return
     */
    public static String replaceFirst(String replacementm, List<String> searchList)
    {
        String text = replacementm;
        for (String searchString : searchList)
        {
            if (replacementm.startsWith(searchString))
            {
                text = replacementm.replaceFirst(searchString, "");
                break;
            }
        }
        return text;
    }

    /**
     * 组装列
     * @param genTableColumn
     * @author ren
     * @date 2025/08/04 13:48
     */
    public static void assembleGenTableColumn(GenTableColumn genTableColumn, Long tableId, String createBy) {
        //设置表信息
        genTableColumn.setTableId(tableId);
        //设置基础信息
        genTableColumn.setCreateBy(createBy);
        genTableColumn.setCreateTime(DateUtils.currentSeconds());
        // 根据数据库字段名，设置java字段名
        genTableColumn.setJavaField(StringUtils.toCamelCase(genTableColumn.getColumnName()));
        //设置字段Java类型和原始页面组件类型
        setColumnJavaAndHtmlType(genTableColumn);
        //设置特殊字段的页面组件类型
        setSpecialColumnHtmlType(genTableColumn);
        //设置字段是否可以插入、编辑、列表展示、查询
        setColumnDefaultPageBehavior(genTableColumn);
        //设置字段默认查询组件类型
        setColumnDefaultQueryType(genTableColumn);
    }

    /**
     * 设置字段Java类型和原始页面控件类型
     * @param genTableColumn
     * @author ren
     * @date 2025/08/04 15:37
     */
    private static void setColumnJavaAndHtmlType(GenTableColumn genTableColumn) {
        String dataType = getDbType(genTableColumn.getColumnType());
        // 设置字段默认类型为String
        genTableColumn.setJavaType(GenConstants.TYPE_STRING);
        if (arraysContains(GenConstants.COLUMNTYPE_STR, dataType) || arraysContains(GenConstants.COLUMNTYPE_TEXT, dataType))
        {   // 如果当前类型是文本类型（"char", "varchar", "nvarchar", "varchar2", "tinytext", "text", "mediumtext", "longtext"）
            // 获取当前类型长度
            Integer columnLength = getColumnLength(genTableColumn.getColumnType());
            // 字符串长度超过500设置为文本域
            String htmlType = columnLength >= 500 || arraysContains(GenConstants.COLUMNTYPE_TEXT, dataType) ? GenConstants.HTML_TEXTAREA : GenConstants.HTML_INPUT;
            genTableColumn.setHtmlType(htmlType);
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_TIME, dataType))
        {   // 如果当前类型是时间类型（"datetime", "time", "date", "timestamp"），Java类型设置为Date，添加编辑页面设置为时间控件
            genTableColumn.setJavaType(GenConstants.TYPE_DATE);
            genTableColumn.setHtmlType(GenConstants.HTML_DATETIME);
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_INT, dataType))
        {   // 如果当前类型是整型类型（"bit", "tinyint", "smallint", "mediumint", "int", "integer"）
            genTableColumn.setJavaType(GenConstants.TYPE_INTEGER);
            genTableColumn.setHtmlType(GenConstants.HTML_INPUT);
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_BIGINT, dataType))
        {   // 如果当前类型是整型类型（"bigint"）
            genTableColumn.setJavaType(GenConstants.TYPE_LONG);
            genTableColumn.setHtmlType(GenConstants.HTML_INPUT);
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_FLOAT, dataType))
        {   // 如果当前类型是整型类型（"float"）
            genTableColumn.setJavaType(GenConstants.TYPE_FLOAT);
            genTableColumn.setHtmlType(GenConstants.HTML_INPUT);
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_DOUBLE, dataType))
        {   // 如果当前类型是整型类型（"double"）
            genTableColumn.setJavaType(GenConstants.TYPE_DOUBLE);
            genTableColumn.setHtmlType(GenConstants.HTML_INPUT);
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_DECIMAL, dataType))
        {   // 如果当前类型是整型类型（"decimal"）
            genTableColumn.setJavaType(GenConstants.TYPE_BIGDECIMAL);
            genTableColumn.setHtmlType(GenConstants.HTML_INPUT);
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_NUMBER, dataType))
        {
            genTableColumn.setHtmlType(GenConstants.HTML_INPUT);

            // 以下开始处理数字类型，根据数据库中数字类型的书写形式进行拆分，比如int类型写为int(0)，小数类型一般写为decimal(10,2)，逗号前表示总位数，逗号后表示小数位数
            List<String> strList = StringUtils.split(StringUtils.subBetween(genTableColumn.getColumnType(), "(", ")"), ",");
            // 如果是浮点型 统一用BigDecimal（括号中的内容使用逗号隔开，逗号拆分后拥有两个数值）
            if (CollUtil.isNotEmpty(strList) && strList.size() == 2 && Integer.parseInt(strList.get(1)) > 0)
            {
                genTableColumn.setJavaType(GenConstants.TYPE_BIGDECIMAL);
            }
            // 如果是整形（括号中内容没有逗号）
            else if (CollUtil.isNotEmpty(strList) && strList.size() == 1 && Integer.parseInt(strList.get(0)) <= 10)
            {
                genTableColumn.setJavaType(GenConstants.TYPE_INTEGER);
            }
            // 长整形（括号中内容没有逗号）
            else
            {
                genTableColumn.setJavaType(GenConstants.TYPE_LONG);
            }
        }
    }

    /**
     * 设置特殊字段的页面组件类型
     * @param genTableColumn
     * @author ren
     * @date 2025/08/04 15:40
     */
    private static void setSpecialColumnHtmlType(GenTableColumn genTableColumn) {
        String columnName = genTableColumn.getColumnName();
        // 状态字段设置单选框
        if (StringUtils.endWithIgnoreCase(columnName, "status"))
        {
            genTableColumn.setHtmlType(GenConstants.HTML_RADIO);
        }
        // 类型&性别字段设置下拉框
        else if (StringUtils.endWithIgnoreCase(columnName, "type")
                || StringUtils.endWithIgnoreCase(columnName, "sex"))
        {
            genTableColumn.setHtmlType(GenConstants.HTML_SELECT);
        }
        // 图片字段设置图片上传控件
        else if (StringUtils.endWithIgnoreCase(columnName, "image"))
        {
            genTableColumn.setHtmlType(GenConstants.HTML_IMAGE_UPLOAD);
        }
        // 文件字段设置文件上传控件
        else if (StringUtils.endWithIgnoreCase(columnName, "file"))
        {
            genTableColumn.setHtmlType(GenConstants.HTML_FILE_UPLOAD);
        }
        // 内容字段设置富文本控件
        else if (StringUtils.endWithIgnoreCase(columnName, "content"))
        {
            genTableColumn.setHtmlType(GenConstants.HTML_EDITOR);
        }
    }

    /**
     * 设置字段默认页面行为
     * @param genTableColumn
     * @author ren
     * @date 2025/08/04 15:42
     */
    private static void setColumnDefaultPageBehavior(GenTableColumn genTableColumn) {
        // 设置字段是否需要插入（字段不在不许插入的配置中，并且字段不是主键）
        if (!arraysContains(GenConstants.COLUMNNAME_NOT_ADD, genTableColumn.getColumnName()) && !genTableColumn.isPk())
        {
            genTableColumn.setIsInsert(GenConstants.REQUIRE);
        }else{
            genTableColumn.setIsInsert(GenConstants.NOT_REQUIRE);
        }
        // 设置字段是否需要编辑（字段不在不许编辑的配置中，并且字段不是主键）
        if (!arraysContains(GenConstants.COLUMNNAME_NOT_EDIT, genTableColumn.getColumnName()) && !genTableColumn.isPk())
        {
            genTableColumn.setIsEdit(GenConstants.REQUIRE);
        }else{
            genTableColumn.setIsEdit(GenConstants.NOT_REQUIRE);
        }
        // 设置字段是否需要列表展示（字段不在不许列表展示的配置中，并且字段不是主键）
        if (!arraysContains(GenConstants.COLUMNNAME_NOT_LIST, genTableColumn.getColumnName()) && !genTableColumn.isPk())
        {
            genTableColumn.setIsList(GenConstants.REQUIRE);
        }else{
            genTableColumn.setIsList(GenConstants.NOT_REQUIRE);
        }
        // 设置字段是否需要查询（字段不在不许查询的配置中，并且字段不是主键）
        if (!arraysContains(GenConstants.COLUMNNAME_NOT_QUERY, genTableColumn.getColumnName()) && !genTableColumn.isPk())
        {
            genTableColumn.setIsQuery(GenConstants.REQUIRE);
        }else{
            genTableColumn.setIsQuery(GenConstants.NOT_REQUIRE);
        }
    }

    /**
     * 设置字段默认查询组件类型
     * @param genTableColumn
     * @author ren
     * @date 2025/08/04 15:44
     */
    private static void setColumnDefaultQueryType(GenTableColumn genTableColumn) {
        genTableColumn.setQueryType(GenConstants.QUERY_EQ);
        // 查询字段类型
        if (StringUtils.endWithIgnoreCase(genTableColumn.getColumnName(), "name"))
        {
            genTableColumn.setQueryType(GenConstants.QUERY_LIKE);
        }
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType)
    {
        if (StringUtils.indexOf(columnType, '(') > 0)
        {
            return StringUtils.subBefore(columnType, "(",false);
        }
        else
        {
            return columnType;
        }
    }

    /**
     * 校验数组是否包含指定值
     *
     * @param arr 数组
     * @param targetValue 值
     * @return 是否包含
     */
    public static boolean arraysContains(String[] arr, String targetValue)
    {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 获取字段长度
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static Integer getColumnLength(String columnType)
    {
        if (StringUtils.indexOf(columnType, '(') > 0)
        {
            String length = StringUtils.subBetween(columnType, "(", ")");
            return Integer.valueOf(length);
        }
        else
        {
            return 0;
        }
    }

    /**
     * 当表类型为树形结构或者子表结构时，验证信息是否为空
     * @param genTable
     * @author ren
     * @date 2025/08/09 21:24
     */
    public static void validateEdit(GenTable genTable)
    {
        if (GenConstants.TPL_TREE.equals(genTable.getTplCategory()))
        {
            String options = JSON.toJSONString(genTable.getOptionsMaps());
            JSONObject paramsObj = JSON.parseObject(options);
            if (StringUtils.isBlank(paramsObj.getString(GenConstants.TREE_CODE)))
            {
                throw new ServiceException("树编码字段不能为空");
            }
            else if (StringUtils.isBlank(paramsObj.getString(GenConstants.TREE_PARENT_CODE)))
            {
                throw new ServiceException("树父编码字段不能为空");
            }
            else if (StringUtils.isBlank(paramsObj.getString(GenConstants.TREE_NAME)))
            {
                throw new ServiceException("树名称字段不能为空");
            }
        }
        else if (GenConstants.TPL_SUB.equals(genTable.getTplCategory()))
        {
            if (StringUtils.isBlank(genTable.getSubTableName()))
            {
                throw new ServiceException("关联子表的表名不能为空");
            }
            else if (StringUtils.isBlank(genTable.getSubTableFkName()))
            {
                throw new ServiceException("子表关联的外键名不能为空");
            }
        }
    }

    /**
     * 解构Options字段，冗余进表冗余字段
     *
     * @param genTable
     * @author ren
     * @date 2025/08/09 21:47
     */
    public static void setTableFromOptions(GenTable genTable)
    {
        JSONObject paramsObj = JSON.parseObject(genTable.getOptions());
        if (ObjUtil.isNotEmpty(paramsObj))
        {
            String treeCode = paramsObj.getString(GenConstants.TREE_CODE);
            String treeParentCode = paramsObj.getString(GenConstants.TREE_PARENT_CODE);
            String treeName = paramsObj.getString(GenConstants.TREE_NAME);
            Long parentMenuId = paramsObj.getLongValue(GenConstants.PARENT_MENU_ID);
            String parentMenuName = paramsObj.getString(GenConstants.PARENT_MENU_NAME);

            genTable.setTreeCode(treeCode);
            genTable.setTreeParentCode(treeParentCode);
            genTable.setTreeName(treeName);
            genTable.setParentMenuId(parentMenuId);
            genTable.setParentMenuName(parentMenuName);
        }
    }

}