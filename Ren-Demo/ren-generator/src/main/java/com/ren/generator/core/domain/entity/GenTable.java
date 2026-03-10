package com.ren.generator.core.domain.entity;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ren.common.core.domain.entity.BaseEntity;

import com.ren.common.utils.StringUtils;
import com.ren.generator.core.constant.GenConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;

/**
 * GenTable 表
 *
 * @author ren
 * @version 2025/07/28 16:05
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("gen_table")
public class GenTable extends BaseEntity {

	/** 表ID */
	@TableId(value = "table_id", type = IdType.AUTO)
	@Schema(description = "表ID(添加时为空)")
	private Long tableId;

	/** 表名称 */
	@NotBlank(message = "表名称不能为空")
	@Schema(description = "表名称")
	@TableField(value = "table_name")
	private String tableName;

	/** 表描述 */
	@NotBlank(message = "表描述不能为空")
	@Schema(description = "表描述")
	@TableField(value = "table_comment")
	private String tableComment;

	/** 关联子表的表名 */
	@Schema(description = "关联子表的表名")
	@TableField(value = "sub_table_name")
	private String subTableName;

	/** 关联子表的外键名 */
	@Schema(description = "关联子表的外键名")
	@TableField(value = "sub_table_fk_name")
	private String subTableFkName;

	/** 实体类名称(首字母大写) */
	@NotBlank(message = "实体类名称不能为空")
	@Schema(description = "实体类名称")
	@TableField(value = "class_name")
	private String className;

	/** 使用的模板（crud单表操作 tree树表操作 sub主子表操作） */
	@Schema(description = "使用的模板")
	@TableField(value = "tpl_category")
	private String tplCategory;

	/** 生成包路径 */
	@NotBlank(message = "生成包路径不能为空")
	@Schema(description = "生成包路径")
	@TableField(value = "package_name")
	private String packageName;

	/** 生成模块名 */
	@NotBlank(message = "生成模块名不能为空")
	@Schema(description = "生成模块名")
	@TableField(value = "module_name")
	private String moduleName;

	/** 生成业务名 */
	@NotBlank(message = "生成业务名不能为空")
	@Schema(description = "生成业务名")
	@TableField(value = "business_name")
	private String businessName;

	/** 生成功能名 */
	@NotBlank(message = "生成功能名不能为空")
	@Schema(description = "生成功能名")
	@TableField(value = "function_name")
	private String functionName;

	/** 生成作者 */
	@NotBlank(message = "作者不能为空")
	@Schema(description = "生成作者")
	@TableField(value = "function_author")
	private String functionAuthor;

	/** 生成代码方式（0zip压缩包 1自定义路径） */
	@Schema(description = "生成代码方式")
	@TableField(value = "gen_type")
	private String genType;

	/** 生成路径（不填默认项目路径） */
	@Schema(description = "生成路径")
	@TableField(value = "gen_path")
	private String genPath;

	/** 其它生成选项（暂时存储了treeCode、treeParentCode、treeName、parentMenuId） 等四个字段，之所以将当前四个字段转成Json放入options存储，是出于树形结构的不稳定性，可能频繁增加减少字段考虑，如果放为一个实体字段，则会增加表维护的困难*/
	@Schema(description = "其它生成选项")
	@TableField(value = "options")
	private String options;

	/*==================================================以下为冗余字段===================================================*/

	/** 上级菜单ID字段（存储于options） */
	@TableField(exist = false)
	@Schema(description = "上级菜单ID字段")
	private Long parentMenuId;

	/** 上级菜单名称字段 */
	@TableField(exist = false)
	@Schema(description = "上级菜单名称字段")
	private String parentMenuName;

	/** 树编码字段（存储于options） */
	@TableField(exist = false)
	@Schema(description = "树编码字段")
	private String treeCode;

	/** 树父编码字段（存储于options） */
	@TableField(exist = false)
	@Schema(description = "树父编码字段")
	private String treeParentCode;

	/** 树名称字段（存储于options） */
	@TableField(exist = false)
	@Schema(description = "树名称字段")
	private String treeName;

	/** 主键信息 */
	@TableField(exist = false)
	@Schema(description = "主键信息")
	private GenTableColumn pkColumn;

	/** 子表信息 */
	@TableField(exist = false)
	@Schema(description = "子表信息")
	private GenTable subTable;

	/** 子表外键信息 */
	@TableField(exist = false)
	@Schema(description = "子表外键信息")
	private GenTableColumn fkColumn;

	/** 表列信息 */
	@Valid
	@TableField(exist = false)
	@Schema(description = "表列信息")
	private List<GenTableColumn> columns;

	/** 树表子表信息 */
	@TableField(exist = false)
	//@JsonInclude(JsonInclude.Include.NON_EMPTY)表示在进行序列化时，如果对象属性值为NULL或空字符串或列表集合长度为0，则忽略该属性。
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Map<String, Object> optionsMaps;

	public boolean isSub()
	{
		return isSub(this.tplCategory);
	}

	public static boolean isSub(String tplCategory)
	{
		return tplCategory != null && StringUtils.equals(GenConstants.TPL_SUB, tplCategory);
	}

	public boolean isTree()
	{
		return isTree(this.tplCategory);
	}

	public static boolean isTree(String tplCategory)
	{
		return tplCategory != null && StringUtils.equals(GenConstants.TPL_TREE, tplCategory);
	}

	public boolean isCrud()
	{
		return isCrud(this.tplCategory);
	}

	public static boolean isCrud(String tplCategory)
	{
		return tplCategory != null && StringUtils.equals(GenConstants.TPL_CRUD, tplCategory);
	}

	public boolean isSuperColumn(String javaField)
	{
		return isSuperColumn(this.tplCategory, javaField);
	}

	public static boolean isSuperColumn(String tplCategory, String javaField)
	{
		return StringUtils.equalsAnyIgnoreCase(javaField, GenConstants.BASE_ENTITY);
	}

}
