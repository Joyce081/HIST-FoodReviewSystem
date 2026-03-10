
package com.ren.monitor.core.domain.schemas;

import java.util.List;

import com.ren.monitor.core.domain.vo.BasicInfoItemVO;
import com.ren.monitor.core.domain.vo.CacheVO;

import com.ren.monitor.core.domain.vo.CommandStatItemVO;
import com.ren.monitor.core.domain.vo.MemoryStatItemVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CacheDetailSchemas 缓存返回模型--OpenApi使用
 *
 * @author ren
 * @version 2025/07/19 16:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "缓存返回模型", name = "AjaxResultForCache")
public class CacheDetailSchemas {

    @Schema(description = "状态码", example = "200")
    private Integer code;
    @Schema(description = "返回信息", example = "操作成功")
    private String msg;
    @Schema(description = "返回数据")
    private Object data;
    @Schema(description = "基础信息列表数据")
    private List<List<BasicInfoItemVO>> basicInfoRows;
    @Schema(description = "命令统计列表数据")
    private List<CommandStatItemVO> commandStats;
    @Schema(description = "内存统计数据")
    private MemoryStatItemVO memoryStats;

}
