package com.ren.common.core.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginDTO 登录
 *
 * @author ren
 * @version 2025/07/18 21:15
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

	/** 用户账号 */
	@Schema(description = "用户账号")
	private String username;
	/** 登陆密码 */
	@Schema(description = "登陆密码")
	private String password;

}