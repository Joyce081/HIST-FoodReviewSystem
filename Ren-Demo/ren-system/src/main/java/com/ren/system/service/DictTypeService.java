
package com.ren.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ren.system.core.domain.entity.DictType;

import java.util.List;

public interface DictTypeService extends IService<DictType> {

    /**
     * 添加字典类型
     * @param dictType
     * @return int
     * @author ren
     * @date 2025/05/18 13:49
     */
    long addDictType(DictType dictType,String createBy);

    /**
     * 删除字典类型
     * @param dictTypeId
     * @author ren
     * @date 2025/05/18 13:49
     */
    void removeDictType(long dictTypeId);

    /**
     * 编辑字典类型
     * @param dictType
     * @author ren
     * @date 2025/05/18 13:49
     */
    void modifyDictType(DictType dictType,String updateBy);

    /**
     * 分页获取字典类型列表
     * @param searchLike
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ren.system.entity.DictType>
     * @author ren
     * @date 2025/05/18 13:50
     */
    IPage<DictType> listDictTypeByPage(String searchLike);

    /**
     * 获取字典类型列表
     * @param searchLike
     * @return java.util.List<com.ren.system.entity.DictType>
     * @author ren
     * @date 2025/05/23 13:40
     */
    List<DictType> listDictTypeByParam(String searchLike);

    /**
     * 获取字典类型详情
     * @param dictTypeId
     * @return com.ren.system.entity.DictType
     * @author ren
     * @date 2025/05/18 13:50
     */
    DictType getDictTypeById(long dictTypeId, int isHasList);

    /**
     * 获取字典类型详情
     * @param dictCode
     * @return com.ren.system.entity.DictType
     * @author ren
     * @date 2025/05/23 13:26
     */
    DictType getDictTypeByDictCode(String dictCode,int isHasList);
}