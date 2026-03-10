package com.ren.common.core.domain.schemas;

import com.ren.common.core.domain.bo.LoginUser;
import com.ren.common.core.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserInfoV2Schemas 用户数据返回模型--OpenApi使用
 *
 * @author ren
 * @version 2025/07/19 16:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户数据返回模型", name = "AjaxResultForUserInfoV2")
public class UserInfoV2Schemas {

    @Schema(description = "状态码", example = "200")
    private Integer code;
    @Schema(description = "返回信息", example = "操作成功")
    private String msg;
    @Schema(description = "返回数据")
    private Object data;
    @Schema(description = "用户数据")
    private User userInfo;
    @Schema(description = "是否超级管理员",example = "1")
    private Long[] roleIdArr;
    @Schema(description = "角色数组")
    private Long[] postIdArr;

}
