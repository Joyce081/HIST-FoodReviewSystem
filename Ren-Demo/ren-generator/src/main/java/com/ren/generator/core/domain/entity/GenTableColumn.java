package com.ren.generator.core.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ren.common.core.domain.entity.BaseEntity;
import com.ren.common.utils.StringUtils;
import com.ren.generator.core.constant.GenConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;

/**
 * GenTableColumn 字段
 *
 * @author ren
 * @version 2025/07/28 16:06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("gen_table_column")
public class GenTableColumn extends BaseEntity {

	/** 字段ID */
	@TableId(value = "column_id", type = IdType.AUTO)
	@Schema(description = "字段ID(添加时为空)")
	private Long columnId;

	/** 归属表编号 */
	@Schema(description = "归属表编号")
	@TableField(value = "table_id")
	private Long tableId;

	/** 列类型 */
	@Schema(description = "列类型")
	@TableField(value = "column_type")
	private String columnType;

	/** 列名称 */
	@Schema(description = "列名称")
	@TableField(value = "column_name")
	private String columnName;

	/** 列描述 */
	@Schema(description = "列描述")
	@TableField(value = "column_comment")
	private String columnComment;

	/** JAVA类型 */
	@Schema(description = "JAVA类型")
	@TableField(value = "java_type")
	private String javaType;

	/** JAVA字段名 */
	@NotBlank(message = "Java属性不能为空")
	@Schema(description = "JAVA字段名")
	@TableField(value = "java_field")
	private String javaField;

	/** 是否主键（1是） */
	@Schema(description = "是否主键",type = "string",allowableValues = {"0", "1"})
	@TableField(value = "is_pk")
	private String isPk;

	/** 是否自增（1是） */
	@Schema(description = "是否自增",type = "string",allowableValues = {"0", "1"})
	@TableField(value = "is_increment")
	private String isIncrement;

	/** 是否必填（1是） */
	@Schema(description = "是否必填",type = "string",allowableValues = {"0", "1"})
	@TableField(value = "is_required")
	private String isRequired;

	/** 是否为插入字段（1是） */
	@Schema(description = "是否为插入字段",type = "string",allowableValues = {"0", "1"})
	@TableField(value = "is_insert")
	private String isInsert;

	/** 是否编辑字段（1是） */
	@Schema(description = "是否编辑字段",type = "string",allowableValues = {"0", "1"})
	@TableField(value = "is_edit")
	private String isEdit;

	/** 是否列表字段（1是） */
	@Schema(description = "是否列表字段",type = "string",allowableValues = {"0", "1"})
	@TableField(value = "is_list")
	private String isList;

	/** 是否查询字段（1是） */
	@Schema(description = "是否查询字段",type = "string",allowableValues = {"0", "1"})
	@TableField(value = "is_query")
	private String isQuery;

	/** 查询方式（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围） */
	@Schema(description = "查询方式",type = "string",allowableValues = {"EQ", "NE", "GT", "LT", "LIKE", "BETWEEN"})
	@TableField(value = "query_type")
	private String queryType;

	/** 显示类型（input文本框、textarea文本域、select下拉框、checkbox复选框、radio单选框、datetime日期控件、image图片上传控件、upload文件上传控件、editor富文本控件） */
	@Schema(description = "显示类型",type = "string",allowableValues = {"input", "textarea", "select", "checkbox", "radio", "datetime", "image", "upload", "editor"})
	@TableField(value = "html_type")
	private String htmlType;

	/** 字典类型 */
	@Schema(description = "字典类型")
	@TableField(value = "dict_type")
	private String dictType;

	/** 排序 */
	@Schema(description = "排序")
	private Integer sort;

	public boolean isPk()
	{
		return isPk(this.isPk);
	}

	public boolean isPk(String isPk)
	{
		return isPk != null && StringUtils.equals("1", isPk);
	}

	public boolean isIncrement()
	{
		return isIncrement(this.isIncrement);
	}

	public boolean isIncrement(String isIncrement)
	{
		return isIncrement != null && StringUtils.equals("1", isIncrement);
	}

	public boolean isRequired()
	{
		return isRequired(this.isRequired);
	}

	public boolean isRequired(String isRequired)
	{
		return isRequired != null && StringUtils.equals("1", isRequired);
	}

	public boolean isInsert()
	{
		return isInsert(this.isInsert);
	}

	public boolean isInsert(String isInsert)
	{
		return isInsert != null && StringUtils.equals("1", isInsert);
	}

	public boolean isEdit()
	{
		return isInsert(this.isEdit);
	}

	public boolean isEdit(String isEdit)
	{
		return isEdit != null && StringUtils.equals("1", isEdit);
	}

	public boolean isList()
	{
		return isList(this.isList);
	}

	public boolean isList(String isList)
	{
		return isList != null && StringUtils.equals("1", isList);
	}

	public boolean isQuery()
	{
		return isQuery(this.isQuery);
	}

	public boolean isQuery(String isQuery)
	{
		return isQuery != null && StringUtils.equals("1", isQuery);
	}

	public boolean isSuperColumn()
	{
		return isSuperColumn(this.javaField);
	}

	//判断字段是否需要再实体类中生成
	public static boolean isSuperColumn(String javaField)
	{
		// BaseEntity，为所有实体类的父类
		// 如果数据库中的字段在父类中已经定义，则此类中不做单独定义
		return StringUtils.equalsAnyIgnoreCase(javaField,GenConstants.BASE_ENTITY);
	}

	public boolean isUsableColumn()
	{
		return isUsableColumn(javaField);
	}

	public static boolean isUsableColumn(String javaField)
	{
		// BaseEntity，为所有实体类的父类
		// 正常情况下，如果数据库中的字段在父类中已经定义，则此类中不做单独定义
		// 但是如果当前字段虽然在父类中已经存在，但是由于特殊需求，在此类中还是需要生成，则在这里进行定义
		return StringUtils.equalsAnyIgnoreCase(javaField, "parentId", "orderNum", "remark");
	}

	public String getCapJavaField()
	{
		return StringUtils.upperFirst(javaField);
	}
}
