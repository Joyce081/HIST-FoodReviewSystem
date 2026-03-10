package com.ren.common.core.domain.schemas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AjaxResultSchemas 返回模型--OpenApi使用
 *
 * @author ren
 * @version 2025/07/19 16:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "定时任务返回模型", name = "AjaxResult")
public class AjaxResultSchemas {

    @Schema(description = "状态码", example = "200")
    private Integer code;
    @Schema(description = "返回信息", example = "操作成功")
    private String msg;
    @Schema(description = "返回数据")
    private Object data;

}
