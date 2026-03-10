package com.ren.system.core.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ResetPasswordDTO 重置密码
 *
 * @author ren
 * @version 2025/07/18 22:04
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "重置密码模型")
public class ResetPasswordDTO {

	/** 对应数据库中的名称为userId，并且是主键自增 */
	@Schema(description = "用户标识")
	private Long userId;
	/** 登陆密码 */
	@Schema(description = "密码")
	private String password;
}
