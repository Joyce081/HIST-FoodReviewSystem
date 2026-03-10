
package com.ren.monitor.core.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoryStatItemVO {

	/**最小值*/
	@Schema(description = "最小值")
	Double min;
	/**最大值*/
	@Schema(description = "最大值")
	Double max;
	/**当前值*/
	@Schema(description = "当前值")
	Double value;

}
