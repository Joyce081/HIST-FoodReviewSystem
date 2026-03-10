package com.ren.generator.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ren.common.core.constant.Constants;
import com.ren.common.utils.DateUtils;
import com.ren.common.utils.StringUtils;
import com.ren.generator.core.constant.GenConstants;
import com.ren.generator.core.domain.entity.GenTable;
import com.ren.generator.core.domain.entity.GenTableColumn;

import cn.hutool.core.util.ObjUtil;

/**
 * VelocityUtils 模版引擎工具类
 *
 * @author ren
 * @version 2025/08/13 15:51
 **/
public class VelocityUtils {

    /**
     * 初始化Velocity引擎
     *
     * @author ren
     * @date 2025/08/02 16:34
     */
	public static void initVelocity()
	{
		// 配置Velocity引擎属性
		Properties properties = new Properties();
		try {
			// 设置Velocity加载资源的基础路径时项目类路径（而非Http或文件系统）
			//properties.setProperty("resource.loader", "class");
			properties.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			// 定义字符集
			properties.setProperty(Velocity.INPUT_ENCODING, Constants.UTF8);
			// 初始化Velocity引擎，指定配置Properties
			Velocity.init(properties);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    // ========================= 上下文准备 =========================

    /**
     * 准备Velocity模板上下文
     *
     * @param genTable 代码生成表对象
     * @return 已配置的Velocity上下文
     * @author ren
     * @date 2025/08/13 16:51
     */
    public static VelocityContext prepareContext(GenTable genTable) {
        // 获取表基本信息
        String moduleName = genTable.getModuleName();
        String businessName = genTable.getBusinessName();
        String packageName = genTable.getPackageName();
        String tplCategory = genTable.getTplCategory();
        String functionName = genTable.getFunctionName();

        // 创建Velocity上下文
        VelocityContext velocityContext = new VelocityContext();

        // 配置简单参数上下文信息
        velocityContext.put("tplCategory", tplCategory);
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("functionName", StringUtils.isNotBlank(functionName) ? functionName : "【请填写功能名称】");
		// 大写类名信息（用于实际的类名）
        velocityContext.put("ClassName", genTable.getClassName());
        // 小写类名信息（用于对象名称）
        velocityContext.put("className", StringUtils.lowerFirst(genTable.getClassName()));
		// 模块名
        velocityContext.put("moduleName", moduleName);
        // 大写业务名信息
        velocityContext.put("BusinessName", StringUtils.upperFirst(businessName));
		// 小写业务名信息
        velocityContext.put("businessName", businessName);
		// 包基础路径（除去最后一级路径的基础路径）
        velocityContext.put("basePackage", getPackagePrefix(packageName));
		// 包名
        velocityContext.put("packageName", packageName);
		// 作者信息
        velocityContext.put("author", genTable.getFunctionAuthor());
		// 创建时间
        velocityContext.put("datetime", DateUtils.getDate()); // 当前时间

        // 配置复杂参数上下文信息
		// 主键列信息
        velocityContext.put("pkColumn", genTable.getPkColumn());
		// 导包信息
        velocityContext.put("importList", getImportList(genTable));
		// 权限信息
        velocityContext.put("permissionPrefix", getPermissionPrefix(moduleName, businessName));
		// 表列信息
        velocityContext.put("columns", genTable.getColumns());
		// 整个表对象
        velocityContext.put("table", genTable);
		// 字典信息
        velocityContext.put("dictTypes", getDictTypes(genTable));

        // 菜单上下文配置
        setMenuVelocityContext(velocityContext, genTable);

        // 树形表特殊处理
        if (GenConstants.TPL_TREE.equals(tplCategory)) {
            setTreeVelocityContext(velocityContext, genTable);
        }

        // 主子表特殊处理
        if (GenConstants.TPL_SUB.equals(tplCategory)) {
            setSubVelocityContext(velocityContext, genTable);
        }

        return velocityContext;
    }

	/**
	 * 配置菜单上下文
	 *
	 * @param context Velocity上下文
	 * @param genTable 代码生成表对象
	 * @author ren
	 * @date 2025/08/13 16:51
	 */
	public static void setMenuVelocityContext(VelocityContext context, GenTable genTable) {
		// 从options字段解析JSON配置
		JSONObject paramsObj = JSON.parseObject(genTable.getOptions());
		// 默认菜单ID
		String parentMenuId = GenConstants.DEFAULT_PARENT_MENU_ID;
		// 如果表参数配置中存在上级菜单ID，则使用指定的上级菜单ID
		if (ObjUtil.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.PARENT_MENU_ID) && StringUtils.isNotBlank(paramsObj.getString(GenConstants.PARENT_MENU_ID))) {
			parentMenuId = paramsObj.getString(GenConstants.PARENT_MENU_ID);
		}
		context.put("parentMenuId", parentMenuId);
	}

    /**
     * 配置树形表上下文
     *
     * @param context Velocity上下文
     * @param genTable 代码生成表对象
     * @author ren
     * @date 2025/08/13 16:51
     */
    public static void setTreeVelocityContext(VelocityContext context, GenTable genTable) {
        JSONObject paramsObj = JSON.parseObject(genTable.getOptions());

        // 注入树形结构关键字段
        context.put("treeCodeColumn", getTreeCodeColumn(paramsObj,genTable));
        context.put("treeParentCodeColumn", getTreeParentCodeColumn(paramsObj,genTable));
        context.put("treeNameColumn", getTreeNameColumn(paramsObj,genTable));
        context.put("expandColumn", getExpandColumn(genTable));
    }

    /**
     * 配置主子表上下文
     *
     * @param context Velocity上下文
     * @param genTable 代码生成表对象
     * @author ren
     * @date 2025/08/13 16:51
     */
    public static void setSubVelocityContext(VelocityContext context, GenTable genTable) {
        GenTable subTable = genTable.getSubTable();

		//子表信息
        context.put("subTable", subTable);
		//子表名称
        context.put("subTableName", genTable.getSubTableName());
		//子表类大写名称
		context.put("subClassName", subTable.getClassName());
		//子表类小写名称
		context.put("subclassName", StringUtils.lowerFirst(subTable.getClassName()));
		//子表外键信息
        context.put("subTableFkColumn", genTable.getSubTable().getFkColumn());
		//子表主键信息
        context.put("subTablePkColumn", genTable.getSubTable().getPkColumn());
		// 字典信息
		context.put("subDictTypes", getDictTypes(subTable));

        // 注入子表导入列表
        context.put("subImportList", getImportList(subTable));
    }

    // ========================= 模板管理 =========================

    /**
     * 获取模板文件列表
     *
     * @param tplCategory 模板类型（crud/tree/sub）
     * @return 模板文件路径列表
     * @author ren
     * @date 2025/08/13 16:51
     */
    public static List<String> getTemplateList(String tplCategory) {
        List<String> templates = new ArrayList<>();

        // 基础Java模板
        templates.add("vm/java/entity.java.vm");
        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/java/service.java.vm");
        templates.add("vm/java/serviceImpl.java.vm");
        templates.add("vm/java/controller.java.vm");

        // 配置文件模板
        templates.add("vm/xml/mapper.xml.vm");
        templates.add("vm/sql/sql.vm");
        templates.add("vm/js/api.ts.vm");

        // 根据模板类型添加页面模板
        if (GenConstants.TPL_CRUD.equals(tplCategory)) {
            templates.add("vm/vue/index.vue.vm");
        } else if (GenConstants.TPL_SUB.equals(tplCategory)) {
			templates.add("vm/vue/index.vue.vm");
			templates.add("vm/vue/sub-index.vue.vm");
			templates.add("vm/java/sub-entity.java.vm");
			templates.add("vm/java/sub-mapper.java.vm");
			templates.add("vm/xml/sub-mapper.xml.vm");
			templates.add("vm/js/router.ts.vm");
			templates.add("vm/js/sub-api.ts.vm");
		} else if (GenConstants.TPL_TREE.equals(tplCategory)) {
            templates.add("vm/vue/index-tree.vue.vm");
        }

        return templates;
    }

    // ========================= 文件路径生成 =========================

    /**
     * 根据模板生成目标文件路径
     *
     * @param template 模板路径
     * @param genTable 生成表对象
     * @return 生成文件路径
     * @author ren
     * @date 2025/08/13 16:51
     */
    public static String getFileName(String template, GenTable genTable) {
        // 文件名称
        String fileName = "";
        // 包路径
        String packageName = genTable.getPackageName();
        // 大写类名
        String className = genTable.getClassName();
        // 模块名
        String moduleName = genTable.getModuleName();
        // 业务名称
        String businessName = genTable.getBusinessName();

        // Java文件基础路径
        String javaPath = GenConstants.PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/");
        // Mybatis相关文件基础路径
        String mybatisPath = GenConstants.MYBATIS_PATH + "/" + moduleName;
        String vuePath = "vue";

        // 实体类对象：main/java/com/example/domain/Entity.java
        if (template.contains("entity.java.vm")) {
            fileName = StringUtils.format("{}/core/domain/entity/{}.java", javaPath, className);
        }
        // Controller：main/java/com/example/controller/EntityController.java
        else if (template.contains("controller.java.vm")) {
            fileName = StringUtils.format("{}/controller/{}Controller.java", javaPath, className);
        }
        // Service接口：main/java/com/example/service/EntityService.java
        else if (template.contains("service.java.vm")) {
            fileName = StringUtils.format("{}/service/{}Service.java", javaPath, className);
        }
        // Service实现：main/java/com/example/service/impl/EntityServiceImpl.java
        else if (template.contains("serviceImpl.java.vm")) {
            fileName = StringUtils.format("{}/service/impl/{}ServiceImpl.java", javaPath, className);
        }
        // Mapper接口：main/java/com/example/mapper/EntityMapper.java
        else if (template.contains("mapper.java.vm")) {
            fileName = StringUtils.format("{}/mapper/{}Mapper.java", javaPath, className);
        }
        // Mapper映射文件：main/resources/mapper/example/EntityMapper.xml
        else if (template.contains("mapper.xml.vm")) {
            fileName = StringUtils.format("{}/{}Mapper.xml", mybatisPath, className);
        }
        // sql文件 example/Menu.sql
        else if (template.contains("sql.vm")) {
            fileName = businessName + "Menu.sql";
        }
        // VUE接口文件 vue/api/example/entity.ts
        else if (template.contains("api.ts.vm")) {
            fileName = StringUtils.format("{}/api/{}/{}.ts", vuePath, moduleName, businessName);
        }
        // VUE路由文件 vue/router/index.ts
        else if (template.contains("vm/js/router.ts.vm")) {
            fileName = StringUtils.format("{}/router/index.ts", vuePath, moduleName, businessName);
        }
        // VUE页面 vue/views/example/entity/index.vue
        else if (template.contains("index.vue.vm")) {
            fileName = StringUtils.format("{}/views/{}/{}/Index.vue", vuePath, moduleName, businessName);
        }
        // VUE树形页面 vue/views/example/entity/index.vue
        else if (template.contains("index-tree.vue.vm")) {
            fileName = StringUtils.format("{}/views/{}/{}/Index.vue", vuePath, moduleName, businessName);
        }

		// 实体类子类对象：main/java/com/example/domain/SubEntity.java
		if (template.contains("sub-entity.java.vm")) {
			fileName = StringUtils.format("{}/core/domain/entity/{}.java", javaPath, genTable.getSubTable().getClassName());
		}
		// Mapper接口：main/java/com/example/mapper/SubEntityMapper.java
		if (template.contains("sub-mapper.java.vm")) {
			fileName = StringUtils.format("{}/mapper/{}Mapper.java", javaPath, genTable.getSubTable().getClassName());
		}
		// Mapper映射文件：main/resources/mapper/example/SubEntityMapper.xml
		if (template.contains("sub-mapper.xml.vm")) {
			fileName = StringUtils.format("{}/{}Mapper.xml", mybatisPath, genTable.getSubTable().getClassName());
		}
		// VUE子页面 vue/views/example/entity/sub/index.vue
		if (template.contains("sub-index.vue.vm")) {
			fileName = StringUtils.format("{}/views/{}/{}/sub/Index.vue", vuePath, moduleName, businessName);
		}
		// VUE子页面接口文件 vue/api/example/sub.ts
		if (template.contains("sub-api.ts.vm")) {
			fileName = StringUtils.format("{}/api/{}/{}-sub.ts", vuePath, moduleName, businessName);
		}
        return fileName;
    }

	// ========================= 树形表专用方法 =========================

	/**
	 * 获取树形编码字段（驼峰命名）
	 *
	 * @param paramsObj 生成选项JSON对象
	 * @return 树形编码字段名
	 * @author ren
	 * @date 2025/08/13 16:51
	 */
	public static GenTableColumn getTreeCodeColumn(JSONObject paramsObj, GenTable genTable) {
		if (paramsObj.containsKey(GenConstants.TREE_CODE)) {
			for(GenTableColumn column : genTable.getColumns()){
				if (column.getColumnName().equals(paramsObj.getString(GenConstants.TREE_CODE))){
					return column;
				}
			}
		}
		return new GenTableColumn();
	}

	/**
	 * 获取树形父编码字段（驼峰命名）
	 *
	 * @param paramsObj 生成选项JSON对象
	 * @return 父编码字段名
	 * @author ren
	 * @date 2025/08/13 16:51
	 */
	public static GenTableColumn getTreeParentCodeColumn(JSONObject paramsObj, GenTable genTable) {
		if (paramsObj.containsKey(GenConstants.TREE_PARENT_CODE)) {
			for(GenTableColumn column : genTable.getColumns()){
				if (column.getColumnName().equals(paramsObj.getString(GenConstants.TREE_PARENT_CODE))){
					return column;
				}
			}
		}
		return new GenTableColumn();
	}

	/**
	 * 获取树形名称字段（驼峰命名）
	 *
	 * @param paramsObj 生成选项JSON对象
	 * @return 树形名称字段名
	 * @author ren
	 * @date 2025/08/13 16:51
	 */
	public static GenTableColumn getTreeNameColumn(JSONObject paramsObj, GenTable genTable) {
		if (paramsObj.containsKey(GenConstants.TREE_NAME)) {
			for(GenTableColumn column : genTable.getColumns()){
				if (column.getColumnName().equals(paramsObj.getString(GenConstants.TREE_NAME))){
					return column;
				}
			}
		}
		return new GenTableColumn();
	}

	/**
	 * 获取树形表格展开列位置
	 *
	 * @param genTable 生成表对象
	 * @return 展开列序号（从1开始）
	 * @author ren
	 * @date 2025/08/13 16:51
	 */
	public static int getExpandColumn(GenTable genTable) {
		JSONObject paramsObj = JSON.parseObject(genTable.getOptions());
		String treeName = paramsObj.getString(GenConstants.TREE_NAME);
		int num = 0;

		// 遍历列表字段计算位置
		for (GenTableColumn column : genTable.getColumns()) {
			if (column.isList()) {
				num++;
				if (column.getColumnName().equals(treeName)) {
					break;
				}
			}
		}
		return num;
	}

    // ========================= 工具方法 =========================

    /**
     * 获取包前缀（去掉最后一级包名）
     *
     * @param packageName 完整包名
     * @return 包前缀
     * @author ren
     * @date 2025/08/13 16:51
     */
    public static String getPackagePrefix(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        return StringUtils.sub(packageName, 0, lastIndex);
    }

    /**
     * 获取实体类需要导入的类型列表
     *
     * @param genTable 生成表对象
     * @return 需导入的Java类型集合
     * @author ren
     * @date 2025/08/13 16:51
     */
    public static HashSet<String> getImportList(GenTable genTable) {
        List<GenTableColumn> columns = genTable.getColumns();
        GenTable subGenTable = genTable.getSubTable();
        HashSet<String> importList = new HashSet<String>();

        // 主子表关系需要导入List
        if (ObjUtil.isNotEmpty(subGenTable)) {
            importList.add("java.util.List");
        }

        // 遍历列添加特殊类型
        for (GenTableColumn column : columns) {
            if (!column.isSuperColumn()) {
                // 日期类型需要特殊导入
                if (GenConstants.TYPE_DATE.equals(column.getJavaType())) {
                    importList.add("java.util.Date");
                    importList.add("com.fasterxml.jackson.annotation.JsonFormat");
                }
                // 大数字类型
                else if (GenConstants.TYPE_BIGDECIMAL.equals(column.getJavaType())) {
                    importList.add("java.math.BigDecimal");
                }
            }
        }
        return importList;
    }

    /**
     * 获取字典配置字符串
     *
     * @param genTable 生成表对象
     * @return 字典配置字符串（逗号分隔）
     * @author ren
     * @date 2025/08/13 16:51
     */
    public static String getDictTypes(GenTable genTable) {
        Set<String> dictTypes = new HashSet<String>();

        // 主表字典字段
        addDictTypes(dictTypes, genTable.getColumns());

        // 子表字典字段（子表专用）
        if (ObjUtil.isNotEmpty(genTable.getSubTable())) {
            addDictTypes(dictTypes, genTable.getSubTable().getColumns());
        }

        return StringUtils.join(", ", dictTypes);
    }

    /**
     * 添加字典字段到集合
     *
     * @param dictTypes 字典集合
     * @param columns 字段列表
     * @author ren
     * @date 2025/08/13 16:51
     */
    public static void addDictTypes(Set<String> dictTypes, List<GenTableColumn> columns) {
        for (GenTableColumn column : columns) {
            // 字典字段条件：非基础字段 + 配置了字典类型 + 特定前端组件
            if (!column.isSuperColumn() && StringUtils.isNotBlank(column.getDictType()) && StringUtils.equalsAny(column.getHtmlType(),new String[] {GenConstants.HTML_SELECT, GenConstants.HTML_RADIO, GenConstants.HTML_CHECKBOX})) {
                dictTypes.add("'" + column.getDictType() + "'");
            }
        }
    }

    /**
     * 生成权限前缀（模块名:业务名）
     *
     * @param moduleName 模块名
     * @param businessName 业务名
     * @return 权限前缀字符串
     * @author ren
     * @date 2025/08/13 16:51
     */
    public static String getPermissionPrefix(String moduleName, String businessName) {
        return StringUtils.format("{}:{}", moduleName, businessName);
    }

}