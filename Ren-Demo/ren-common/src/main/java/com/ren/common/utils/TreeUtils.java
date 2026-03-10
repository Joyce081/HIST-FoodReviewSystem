package com.ren.common.utils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.ren.common.core.domain.vo.TreeSelectVO;

import cn.hutool.core.bean.BeanUtil;

/**
 * 格式化树结构方法
 * 
 * @author ren
 */
public class TreeUtils {

    /**
     * 通用树形结构构建方法
     * @param list 原始扁平列表（必传）
     * @param isRoot 判断是否为根节点的条件函数（非必传，不传则将第一层无父节点的节点升级为根节点）
     * @param idFieldName 主键Id字段名 默认为id（如果主键字段名正好为id，则可不传，否则必传）
     * @param parentIdFieldName 父节点Id字段名 默认为parentId（如果父节点Id字段名正好为parentId，则可不传，否则必传）
     * @param childrenFieldName 子节点列表字段名 默认为children（如果子节点列表字段名正好为children，则可不传，否则必传）
     * @param orderNumFieldName 排序字段名（非必传，不传则不进行排序）
     * @return 树形结构列表
     */
    public static <T> List<T> formatTree(
            List<T> list,
            Predicate<T> isRoot,
            String idFieldName,String parentIdFieldName,String childrenFieldName,String orderNumFieldName) {

        if(list == null){
            throw new RuntimeException("需格式化列表不可为空");
        }

        idFieldName = StringUtils.isBlank(idFieldName) ? "id" : idFieldName;
        parentIdFieldName = StringUtils.isBlank(parentIdFieldName) ? "parentId" : parentIdFieldName;
        childrenFieldName = StringUtils.isBlank(childrenFieldName) ? "children" : childrenFieldName;

        // 1. 创建节点映射表：以节点ID为key，节点对象为value
        Map<Object, T> nodeMap = new HashMap<>();
        for (T node : list) {
            // 通过 BeanUtil 动态根据属性名获取属性值（获取主键Id值）
            Object id = BeanUtil.getProperty(node, idFieldName);
            if(id == null){
                throw new RuntimeException("所传入的idFieldName字段名为无效值");
            }
            nodeMap.put(id, node);
        }

        // 2.1 初始化根节点列表
        List<T> roots = new ArrayList<>();
        // 2.2 记录节点是否有父节点(用于处理不存在根节点的情况)
        Map<Object, Boolean> hasParentMap = new HashMap<>();

        // 3. 遍历所有节点，添加空的根节点列表，同时构建树形结构，并将数据存储到节点映射表中
        for (T node : list) {
            // 3.1 判断当前节点是否为根节点，如果是根节点，直接插入根节点列表，并结束本次循环，继续下一次循环
            if (isRoot != null && isRoot.test(node)) {
                roots.add(node);
                continue;
            }

            // 3.2 如果不是父节点，通过 BeanUtil 动态根据属性名获取属性值（获取父节点ID值）
            Object parentId = BeanUtil.getProperty(node, parentIdFieldName);
            if(parentId == null){
                throw new RuntimeException("所传入的parentIdFieldName字段名为无效值");
            }

            // 3.3 判断父节点是否存在于节点映射表中，如果不存在，则将当前节点标记为没有父节点
            if (!nodeMap.containsKey(parentId)) {
                hasParentMap.put(BeanUtil.getProperty(node, idFieldName), false);
            } else {
                hasParentMap.put(BeanUtil.getProperty(node, idFieldName), true);
            }

            // 3.4 根据上方获取到的父节点值在节点映射表中查找出对应的对象
            T parent = nodeMap.get(parentId);

            // 3.5 若存在父节点，将当前节点加入父节点的子列表
            if (parent != null) {
                // 通过 BeanUtil 动态根据属性名获取属性值（获取父节点的子节点列表内容）
                List<T> children = BeanUtil.getProperty(parent, childrenFieldName);
                // 如果不存在，则重新创建一个空列表
                if (children == null) {
                    throw new RuntimeException("所传入的childrenFieldName字段名为无效值");
                }
                // 将该子节点添加到父节点的子列表中
                children.add(node);
                // 通过函数式接口将子列表设置回父节点
                BeanUtil.setProperty(parent, childrenFieldName, children);
                // 3.6 将父节点重新放入节点映射表中，以便后续递归使用
                nodeMap.put(parentId, parent);
            }
        }

        // 将所有没有父节点的节点都升级为根节点
        for (T node : list) {
            Object nodeId = BeanUtil.getProperty(node, idFieldName);
            // getOrDefault表示从Map中安全取值，如果存在，则返回值，不存在返回默认值true
            if (!hasParentMap.getOrDefault(nodeId, true)) {
                roots.add(node);
            }
        }

        // 4. 遍历所以根节点，然后从节点映射表中取出对应的节点，进行替换
        for(int i=0;i<roots.size();i++){
            Object parentId = BeanUtil.getProperty(roots.get(i), idFieldName);
            if (parentId == null) {
                throw new RuntimeException("所传入的idFieldName字段名为无效值");
            }
            T parent = nodeMap.get(parentId);
            if (parent != null) {
                roots.set(i, parent);
            }
        }

        // 5. 递归排序所有子节点（按 orderNum 排序）
        // 只有排序字段不为空才排序，否则不排序
        if(StringUtils.isNotBlank(orderNumFieldName)){
            sortChildren(roots,childrenFieldName,orderNumFieldName);
        }
        return roots;
    }

    /**
     * 递归排序子节点
     * @param nodes 当前层节点列表
     * @param childrenFieldName 子节点列表字段名 默认为children
     * @param orderNumFieldName 排序字段名 默认为orderNum
     */
    private static <T> void sortChildren(List<T> nodes,String childrenFieldName,String orderNumFieldName) {
        if(nodes == null || nodes.isEmpty()){
            return;
        }

        orderNumFieldName = StringUtils.isBlank(orderNumFieldName) ? "orderNum" : orderNumFieldName;
        String finalOrderNumFieldName = orderNumFieldName;

        if (BeanUtil.getProperty(nodes.get(0), finalOrderNumFieldName) == null) {
            throw new RuntimeException("所传入的finalOrderNumFieldName字段名为无效值");
        }
        // 5.1 按 orderNum 排序当前层节点,通过 BeanUtil 动态根据属性名获取属性值（获取序号值）
        nodes.sort(Comparator.comparingInt(node -> BeanUtil.getProperty(node, finalOrderNumFieldName)));
        //下面这种写法是上面写法的详细写法
        //nodes.sort((o1,o2) -> Integer.compare(BeanUtil.getProperty(o1, finalOrderNumFieldName),BeanUtil.getProperty(o2, finalOrderNumFieldName)));

        for (T node : nodes) {
            // 5.2 获取当前节点的子节点列表
            List<T> children = BeanUtil.getProperty(node, childrenFieldName);
            // 5.3 如果子节点列表不为空，则递归排序子节点
            if (children != null && !children.isEmpty()) {
                sortChildren(children,childrenFieldName,finalOrderNumFieldName);
            }
            // 5.4 将排序后的子节点列表设置回当前节点
            BeanUtil.setProperty(node, childrenFieldName, children);
        }
    }

    /**
     * 将普通树形结构转换为下拉框树形结构
     * @param tList 需要转换的列表
     * @param idFieldName 树形下拉框值字段名 默认为id
     * @param labelFieldName 树形下拉框名称字段名 默认为label
     * @param disabledFieldName 树形下拉框是否禁用字段名 默认为disabled
     * @param childrenFieldName 树形下拉框子节点列表字段名 默认为children（注意：该字段名对应的字段需要为是与所需要转换的列表类型相同的子列表）
     * @return java.util.List<com.ren.common.core.vo.TreeSelectVO>
     * @author ren
     * @date 2025/05/09 22:35
     */
    public static <T,ID> List<TreeSelectVO<ID>> convertTreeSelectForAll(List<T> tList,String idFieldName, String labelFieldName, String disabledFieldName, String childrenFieldName){
        //判断是否存在，不存在则设置默认字段名
        idFieldName = StringUtils.isBlank(idFieldName) ? "id" : idFieldName;
        labelFieldName = StringUtils.isBlank(labelFieldName) ? "label" : labelFieldName;
        disabledFieldName = StringUtils.isBlank(disabledFieldName) ? "isDisabled" : disabledFieldName;
        childrenFieldName = StringUtils.isBlank(childrenFieldName) ? "children" : childrenFieldName;

        //由于需要调用流式处理，所以需要将字段重新设置一遍，变为一个未曾改变的对象
        String finalIdFieldName = idFieldName;
        String finalLabelFieldName = labelFieldName;
        String finalDisabledFieldName = disabledFieldName;
        String finalChildrenFieldName = childrenFieldName;

        return tList.stream().map(children -> new TreeSelectVO<ID>(children, finalIdFieldName, finalLabelFieldName, finalDisabledFieldName, finalChildrenFieldName)).collect(Collectors.toList());
    }

    /**
     * 组装节点的祖级列表
     * @param list 需组装列表
     * @param idFieldName 节点Id字段名 默认为id
     * @param parentIdFieldName 父节点Id字段名 默认为parentId
     * @param superNodeId 超级节点Id值（这个节点用于给所有节点的祖级节点添加一个共同的值，例如如果传0，一级节点的祖级列表就是"0"，二级节点祖级列表就是"0,一级节点Id"）
     * @return java.util.HashMap<java.lang.String,java.lang.String>
     * @author ren
     * @date 2025/08/26 10:16
     */
	public static <T> HashMap<String, String> assembleAncestorsMap(List<T> list,String idFieldName,String parentIdFieldName, String superNodeId) {
        // 结果Mao
        HashMap<String, String> ancestorsMap = new HashMap<>();
        if(list == null){
            throw new RuntimeException("需组装列表不可为空");
        }

        // 设置默认字段名
        idFieldName = StringUtils.isBlank(idFieldName) ? "id" : idFieldName;
        parentIdFieldName = StringUtils.isBlank(parentIdFieldName) ? "parentId" : parentIdFieldName;

        // 创建节点映射表：以节点ID为key，节点对象为value
        Map<Object, T> nodeMap = new HashMap<>();
        for (T node : list) {
            // 通过 BeanUtil 动态根据属性名获取属性值（获取主键Id值）
            Object id = BeanUtil.getProperty(node, idFieldName);
            if(id == null){
                throw new RuntimeException("所传入的idFieldName字段名为无效值");
            }
            nodeMap.put(id, node);
        }

        for (T node : list) {
            // 通过 BeanUtil 动态根据属性名获取属性值（获取父主键）
            Object parentId = BeanUtil.getProperty(node, parentIdFieldName);

            // 根据parentId获取父元素节点
            StringJoiner ancestorsJoiner = new StringJoiner(",");
            if (superNodeId != null){
                ancestorsJoiner.add(superNodeId);
            }
            if (parentId != null) {
                Set<Object> visitedNodes = new HashSet<>();
                getAncestorsStr(ancestorsJoiner, nodeMap, parentId, parentIdFieldName, visitedNodes);
            }

            // 将祖级菜单添加到结果中
            // 通过 BeanUtil 动态根据属性名获取属性值（获取主键Id值）
            Object id = BeanUtil.getProperty(node, idFieldName);
            if(id == null){
                throw new RuntimeException("所传入的idFieldName字段名为无效值");
            }
            ancestorsMap.put(id.toString(), ancestorsJoiner.toString());
        }
        return ancestorsMap;
	}

    /**
     * 回调获取单个节点的祖级菜单
     * @param ancestorsJoiner 祖级菜单
     * @param nodeMap 节点映射表
     * @param parentId 父级ID
     * @param parentIdFieldName 父级ID字段名
     * @param visitedNodes 访问过的节点（用于防止循环引用）
     * @author ren
     * @date 2025/08/26 09:41
     */
    private static <T> void getAncestorsStr(StringJoiner ancestorsJoiner, Map<Object, T> nodeMap,Object parentId, String parentIdFieldName, Set<Object> visitedNodes) {
        // 检测循环引用
        if (visitedNodes.contains(parentId)) {
            return; // 发现循环引用，直接返回避免无限递归
        }
        visitedNodes.add(parentId);

        // 获取父级元素
        T node = nodeMap.get(parentId);
        // 父级元素不为空
        if (node != null) {
            // 将当前父级ID拼接到祖级菜单中
            ancestorsJoiner.add(parentId.toString());
            // 判断当前元素是否还存在父级元素，如果存在，则回调该方法，继续拼接
            Object parentIdV2 = BeanUtil.getProperty(node, parentIdFieldName);
            if (parentIdV2 != null) {
                getAncestorsStr(ancestorsJoiner, nodeMap, parentIdV2, parentIdFieldName, visitedNodes);
            }
        }
    }
}