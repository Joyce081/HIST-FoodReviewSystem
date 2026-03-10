package com.ren.common.core.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 * 
 * @author ren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页列表返回模型")
public class TableDataInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    @Schema(description = "总记录数",example = "100")
    private long total;

    /** 每页显示数量 */
    @Schema(description = "每页显示数量",example = "10")
    private long pageSize;

    /** 当前页数 */
    @Schema(description = "当前页数",example = "1")
    private long pageNum;

    /** 总页数 */
    @Schema(description = "总页数",example = "10")
    private long totalPage;

    /** 列表数据 */
    @Schema(description = "列表数据")
    private List<?> rows;

    /** 消息状态码 */
    @Schema(description = "状态码")
    private int code;

    /** 消息内容 */
    @Schema(description = "返回信息")
    private String msg;

    /**
     * 分页
     * 
     * @param list 列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<?> list, long total)
    {
        this.rows = list;
        this.total = total;
    }

}
