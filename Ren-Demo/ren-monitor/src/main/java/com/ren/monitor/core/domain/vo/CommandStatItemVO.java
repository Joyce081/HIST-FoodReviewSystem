package com.ren.monitor.core.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandStatItemVO {

	/**命令名称*/
	@Schema(description = "命令名称")
	String name;
	/**命令执行数量*/
	@Schema(description = "命令执行数量")
	Integer value;

}
