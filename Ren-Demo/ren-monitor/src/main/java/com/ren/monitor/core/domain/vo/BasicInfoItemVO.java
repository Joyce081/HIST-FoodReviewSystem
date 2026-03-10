package com.ren.monitor.core.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicInfoItemVO {

	/**标题*/
	@Schema(description = "标题")
	String label;
	/**内容*/
	@Schema(description = "内容")
	String value;

}
