
package com.ren.monitor.core.domain.schemas;

import java.util.List;

import com.ren.common.core.domain.vo.*;
import com.ren.monitor.core.domain.vo.BasicInfoItemVO;
import com.ren.monitor.core.domain.vo.CommandStatItemVO;
import com.ren.monitor.core.domain.vo.MemoryStatItemVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ServerMonitorSchemas 服务返回模型--OpenApi使用
 *
 * @author ren
 * @version 2025/07/19 16:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "服务返回模型", name = "AjaxResultForServerMonitor")
public class ServerMonitorSchemas {

    @Schema(description = "状态码", example = "200")
    private Integer code;
    @Schema(description = "返回信息", example = "操作成功")
    private String msg;
    @Schema(description = "返回数据")
    private Object data;
    @Schema(description = "获取Cpu相关信息")
    private CpuInfoVO cpuInfo;
    @Schema(description = "获取内存信息")
    private MemoryInfoVO memoryInfo;
    @Schema(description = "获取服务器信息")
    private ComputerSystemInfoVO computerSystemInfo;
    @Schema(description = "获取Java虚拟机信息")
    private JvmInfoVO jvmInfo;
    @Schema(description = "获取Java信息")
    private JavaInfoVO javaInfo;
    @Schema(description = "获取项目信息")
    private ProjectInfoVO projectInfo;
    @Schema(description = "获取磁盘信息")
    private List<DiskInfoVO> diskInfoList;

}
