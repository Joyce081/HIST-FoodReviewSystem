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

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_dict_type")
public class DictType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 字典类型ID */
    @TableId(value = "dict_type_id", type = IdType.AUTO)
    @Schema(description = "字典类型ID")
    private Long dictTypeId;
    /** 字典名称 */
    @TableField(value = "dict_name")
    @Schema(description = "字典名称")
    private String dictName;
    /** 字典编码 */
    @TableField(value = "dict_code")
    @Schema(description = "字典编码")
    private String dictCode;
    /** 是否停用（1：是，0：否） */
    @TableField(value = "is_stop")
    @Schema(description = "是否停用")
    private Integer isStop;

    /*==================================================以下为冗余字段===================================================*/
    /** 字典数据列表 */
    @TableField(exist = false)
    @Schema(hidden = true)
    private List<DictData> dictDataList;

}