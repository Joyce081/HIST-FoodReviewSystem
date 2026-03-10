
package com.ren.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ren.system.core.domain.entity.DictData;

import java.util.List;

public interface DictDataService extends IService<DictData> {

    /**
     * 添加字典数据
     * @param dictData
     * @return int
     * @author ren
     * @date 2025/05/18 13:49
     */
    long addDictData(DictData dictData,String createBy);

    /**
     * 删除字典数据
     * @param dictDataId
     * @author ren
     * @date 2025/05/18 13:49
     */
    void removeDictData(long dictDataId);

    /**
     * 编辑字典数据
     * @param dictData
     * @author ren
     * @date 2025/05/18 13:49
     */
    void modifyDictData(DictData dictData,String updateBy);

    /**
     * 分页获取字典数据列表
     * @param searchLike
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ren.system.entity.DictData>
     * @author ren
     * @date 2025/05/18 13:50
     */
    IPage<DictData> listDictDataByPage(String searchLike);

    /**
     * 获取字典数据详情
     * @param dictDataId
     * @return com.ren.system.entity.DictData
     * @author ren
     * @date 2025/05/18 13:50
     */
    DictData getDictDataById(long dictDataId);

    /**
     * 获取字典数据详情
     * @param dictType
     * @param isDefault
     * @return com.ren.system.entity.DictData
     * @author ren
     * @date 2025/05/23 14:04
     */
    DictData getDictDataByParam(String dictType,Integer isDefault);

    /**
     * 获取字典数据列表
     * @param dictType
     * @return java.util.List<com.ren.system.entity.DictData>
     * @author ren
     * @date 2025/05/23 13:30
     */
    List<DictData> listDictDataByDictType(String dictType);
}