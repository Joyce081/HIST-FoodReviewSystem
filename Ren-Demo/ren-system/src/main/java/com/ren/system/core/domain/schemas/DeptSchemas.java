package com.ren.system.core.domain.schemas;

import java.util.List;

import com.ren.common.core.domain.entity.Dept;
import com.ren.common.core.domain.entity.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DeptSchemas 部门返回类--OpenApi使用
 *
 * @author ren
 * @version 2025/07/19 16:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "部门返回类", name = "AjaxResultForDept")
public class DeptSchemas {

    @Schema(description = "状态码", example = "200")
    private Integer code;
    @Schema(description = "返回信息", example = "操作成功")
    private String msg;
    @Schema(description = "返回数据")
    private Object data;
    @Schema(description = "自定义返回数据--部门列表")
    private List<Dept> deptList;
    @Schema(description = "自定义返回数据--部门ID列表")
    private Long[] deptIdArr;

}
