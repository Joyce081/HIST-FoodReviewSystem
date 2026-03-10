package com.ren.cloudstorage.core.domain.schemas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UploadSchemas 上传文件接口返回类--OpenApi使用
 *
 * @author ren
 * @version 2025/07/19 16:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "云文件上传返回模型", name = "AjaxResult")
public class UploadSchemas {

    @Schema(description = "状态码", example = "200")
    private Integer code;
    @Schema(description = "返回信息", example = "操作成功")
    private String msg;
    @Schema(description = "返回数据")
    private Object data;
    @Schema(description = "自定义返回数据--云文件路径",
        example = "https://xueyaxuetang.oss-cn-region.aliyuncs.com/xyxt_img/article/2025/07/19/12bf8f626f354aa2aa094dca8e6f8a02.jpeg")
    private String url;
    @Schema(description = "自定义返回数据--除去域名之外的文件路径",
        example = "xyxt_img/article/2025/07/19/12bf8f626f354aa2aa094dca8e6f8a02.jpeg")
    private String fileName;
    @Schema(description = "自定义返回数据--上传至云后的文件名称", example = "12bf8f626f354aa2aa094dca8e6f8a02.jpeg")
    private String newFileName;
    @Schema(description = "自定义返回数据--原文件名称", example = "1a05de9688f330b6d7a540885c5ed56a.jpeg")
    private String originalFilename;

}
