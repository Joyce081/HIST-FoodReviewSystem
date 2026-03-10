package com.ren.system.core.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ren.common.core.domain.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_dict_data")
public class DictData extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 字典编码 */
    @TableId(value = "dict_data_id", type = IdType.AUTO)
    @Schema(description = "字典编码")
    private Long dictDataId;
    /** 字典排序 */
    @TableField(value = "dict_sort")
    @Schema(description = "字典排序")
    private Integer dictSort;
    /** 字典标签 */
    @TableField(value = "dict_label")
    @Schema(description = "字典标签")
    private String dictLabel;
    /** 字典键值 */
    @TableField(value = "dict_value")
    @Schema(description = "字典键值")
    private String dictValue;
    /** 字典类型 */
    @TableField(value = "dict_type")
    @Schema(description = "字典类型")
    private String dictType;
    /** 样式属性（其他样式扩展） */
    @TableField(value = "css_class")
    @Schema(description = "样式属性")
    private String cssClass;
    /** 表格回显样式 */
    @TableField(value = "list_class")
    @Schema(description = "表格回显样式")
    private String listClass;
    /** 是否默认（1：是，0：否） */
    @TableField(value = "is_default")
    @Schema(description = "是否默认",type = "integer",allowableValues = {"0", "1"})
    private Integer isDefault;
    /** 是否停用（1：是，0：否） */
    @TableField(value = "is_stop")
    @Schema(description = "是否停用",type = "integer",allowableValues = {"0", "1"})
    private Integer isStop;

}