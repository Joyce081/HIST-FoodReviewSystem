
package com.ren.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ren.system.core.domain.entity.DictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictDataMapper extends BaseMapper<DictData> {

    /**
     * 添加字典数据
     * @param dictData
     * @return int
     * @author ren
     * @date 2025/05/18 13:49
     */
    void insertDictData(DictData dictData);

    /**
     * 获取字典数据详情
     * @param dictType
     * @param isDefault
     * @return com.ren.system.entity.DictData
     * @author ren
     * @date 2025/05/23 14:05
     */
    DictData selectDictDataByParam(@Param("dictType") String dictType, @Param("isDefault") Integer isDefault);

    /**
     * 删除字典数据
     * @param dictDataId
     * @author ren
     * @date 2025/05/18 13:49
     */
    void deleteDictData(long dictDataId);

    /**
     * 编辑字典数据
     * @param dictData
     * @author ren
     * @date 2025/05/18 13:49
     */
    void updateDictData(DictData dictData);

    /**
     * 分页获取字典数据列表
     * @param searchLike
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ren.system.entity.DictData>
     * @author ren
     * @date 2025/05/18 13:50
     */
    IPage<DictData> listDictDataByPage(Page<DictData> page, @Param("searchLike")String searchLike);

    /**
     * 获取字典数据列表
     * @param dictType
     * @return java.util.List<com.ren.system.entity.DictData>
     * @author ren
     * @date 2025/05/23 13:31
     */
	List<DictData> listDictDataByDictType(String dictType);

}