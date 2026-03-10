package com.ren.common.core.domain.vo;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Treeselect树结构实体类
 *
 * @author ren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeSelectVO<ID> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private ID id;

    /** 节点名称 */
    private String label;

    /** 节点禁用 */
    private boolean disabled = false;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelectVO<ID>> children;

    /**
     * 将传进来的普通树形结构转换为下拉框树形结构
     * @param t
     * @param idFieldName
     * @param labelFieldName
     * @param disabledFieldName
     * @param childrenFieldName
     * @author ren
     * @date 2025/05/09 22:35
     */
    public <T> TreeSelectVO(
            T t,
            String idFieldName,
            String labelFieldName,
            String disabledFieldName,
            String childrenFieldName
    ) {

        // 通过 BeanUtil 动态根据属性名获取属性值（获取主键值）
        Object idInline = BeanUtil.getProperty(t, idFieldName);
        // 如果获取不到相关内容，则报错
        if(idInline == null){
            throw new RuntimeException("所传入的idFieldName字段名为无效值");
        }
        // 直接赋值，不进行类型转换
        this.id = (ID) idInline;

        // 通过 BeanUtil 动态根据属性名获取属性值（获取名称值）
        Object labelInline = BeanUtil.getProperty(t, labelFieldName);
        // 如果获取不到相关内容，则报错
        if(labelInline == null){
            throw new RuntimeException("所传入的labelFieldName字段名为无效值");
        }
        this.label = Convert.toStr(labelInline);

        // 通过 BeanUtil 动态根据属性名获取属性值（获取是否禁用值，之后判断是否禁用是否等于1，等于一则为禁用，否则为不禁用）
        Object disabledInline = BeanUtil.getProperty(t, disabledFieldName);
        // 如果有禁用相关内容，则进行判断，否则默认为不禁用
        if(disabledInline != null){
            // 支持多种类型的禁用状态表示
            if (disabledInline instanceof Boolean) {
                this.disabled = (Boolean) disabledInline;
            } else if (disabledInline instanceof Number) {
                this.disabled = ((Number) disabledInline).intValue() == 1;
            } else if (disabledInline instanceof String) {
                String disabledStr = (String) disabledInline;
                this.disabled = "1".equals(disabledStr) || "true".equalsIgnoreCase(disabledStr);
            } else {
                throw new IllegalArgumentException("disabledFieldName 必须对应 Boolean、Number 或 String 类型属性");
            }
        }

        // 通过 BeanUtil 动态根据属性名获取属性值（获取子列表值）
        Object rawChildList = BeanUtil.getProperty(t, childrenFieldName);
        // 如果获取不到相关内容，则报错
        if (rawChildList == null) {
            throw new RuntimeException("所传入的childrenFieldName字段名为无效值");
        }
        // 判断子列表是不是List结构，如果不是则报错
        if (!(rawChildList instanceof List<?>)) {
            throw new IllegalArgumentException("childrenFieldName 必须对应 List 类型属性");
        }
        // 递归调用该方法，构建子元素树形结构
        this.children = ((List<T>) rawChildList).stream()
                .map(child -> new TreeSelectVO<ID>(
                        child,
                        idFieldName,
                        labelFieldName,
                        disabledFieldName,
                        childrenFieldName
                ))
                .collect(Collectors.toList());
    }

}
