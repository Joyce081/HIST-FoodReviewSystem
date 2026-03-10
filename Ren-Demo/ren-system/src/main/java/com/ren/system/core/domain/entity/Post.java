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
@TableName("sys_post")
public class Post extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 岗位ID */
    @TableId(value = "post_id", type = IdType.AUTO)
    @Schema(description = "岗位ID(添加时为空)")
    private Long postId;
    /** 岗位编码 */
    @TableField(value = "post_code")
    @Schema(description = "岗位编码")
    private String postCode;
    /** 岗位名称 */
    @TableField(value = "post_name")
    @Schema(description = "岗位名称")
    private String postName;
    /** 显示顺序 */
    @TableField(value = "post_sort")
    @Schema(description = "显示顺序")
    private Integer postSort;
    /** 是否停用 */
    @TableField(value = "is_stop")
    @Schema(description = "是否停用",type = "integer",allowableValues = {"0", "1"})
    private Integer isStop;

}