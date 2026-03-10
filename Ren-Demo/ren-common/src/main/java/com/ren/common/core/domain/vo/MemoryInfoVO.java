package com.ren.common.core.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 内存相关信息
 * @author ren
 * @date 2025/06/07 17:19
 */
@Data
@Accessors(chain = true) // 开启链式调用
@AllArgsConstructor
@NoArgsConstructor
public class MemoryInfoVO {

	/** 内存总量 */
	@Schema(description = "内存总量")
	private String total;
	/** 可用内存 */
	@Schema(description = "可用内存")
	private String available;
	/** 已用内存 */
	@Schema(description = "已用内存")
	private String used;
	/** 占用率 */
	@Schema(description = "占用率")
	private String occupancyRate;

	/** JVM内存总量 */
	@Schema(description = "JVM内存总量")
	private String jvmTotal;
	/** JVM可用内存 */
	@Schema(description = "JVM可用内存")
	private String jvmAvailable;
	/** JVM已用内存 */
	@Schema(description = "JVM已用内存")
	private String jvmUsed;
	/** JVM占用率 */
	@Schema(description = "JVM占用率")
	private String jvmOccupancyRate;
	/** JVM最大可用内存 */
	@Schema(description = "JVM最大可用内存")
	private String jvmMax;
}