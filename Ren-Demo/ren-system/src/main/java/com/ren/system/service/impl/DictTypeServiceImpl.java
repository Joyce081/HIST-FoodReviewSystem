package com.ren.system.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ren.common.utils.DateUtils;
import com.ren.common.utils.PageUtils;
import com.ren.common.utils.StringUtils;
import com.ren.system.core.domain.entity.DictData;
import com.ren.system.core.domain.entity.DictType;
import com.ren.system.mapper.DictTypeMapper;
import com.ren.system.service.DictDataService;
import com.ren.system.service.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {

    @Autowired
    private DictTypeMapper dictTypeMapper;
    @Autowired
    private DictDataService dictDataService;

    /**
     * 添加字典类型
     * @param dictType
     * @return int
     * @author ren
     * @date 2025/05/18 13:49
     */
    @Override
    public long addDictType(DictType dictType,String createBy) {
        dictType.setCreateBy(createBy);
        dictType.setCreateTime(DateUtils.currentSeconds());
        dictTypeMapper.insertDictType(dictType);
        return dictType.getDictTypeId();
    }

    /**
     * 删除字典类型
     * @param dictTypeId
     * @author ren
     * @date 2025/05/18 13:49
     */
    @Override
    public void removeDictType(long dictTypeId) {
        dictTypeMapper.deleteDictType(dictTypeId);
    }

    /**
     * 编辑字典类型
     * @param dictType
     * @author ren
     * @date 2025/05/18 13:49
     */
    @Override
    public void modifyDictType(DictType dictType,String updateBy) {
        dictType.setUpdateBy(updateBy);
        dictType.setUpdateTime(DateUtils.currentSeconds());
        dictTypeMapper.updateDictType(dictType);
    }

    /**
     * 分页获取字典类型列表
     * @param searchLike
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ren.system.entity.DictType>
     * @author ren
     * @date 2025/05/18 13:50
     */
    @Override
    public IPage<DictType> listDictTypeByPage(String searchLike) {
        if(StringUtils.isNotBlank(searchLike)){
            searchLike = StringUtils.format("%%{}%%",searchLike);
        }
        IPage<DictType> dictTypeList = dictTypeMapper.listDictTypeByPage(PageUtils.createPage(DictType.class),searchLike);
        return dictTypeList;
    }

    /**
     * 获取字典类型列表
     * @param searchLike
     * @return java.util.List<com.ren.system.entity.DictType>
     * @author ren
     * @date 2025/05/23 13:40
     */
    @Override
    public List<DictType> listDictTypeByParam(String searchLike) {
        if(StringUtils.isNotBlank(searchLike)){
            searchLike = StringUtils.format("%%{}%%",searchLike);
        }
        List<DictType> dictTypeList = dictTypeMapper.listDictTypeByParam(searchLike);
        return dictTypeList;
    }

    /**
     * 获取字典类型详情
     * @param dictTypeId
     * @return com.ren.system.entity.DictType
     * @author ren
     * @date 2025/05/18 13:50
     */
    @Override
    public DictType getDictTypeById(long dictTypeId, int isHasList) {
        DictType dictType = dictTypeMapper.selectById(dictTypeId);
        if(ObjUtil.isNotNull(dictType) && isHasList == 1){
            List<DictData> dictDataList = dictDataService.listDictDataByDictType(dictType.getDictCode());
            dictType.setDictDataList(dictDataList);
        }
        return dictType;
    }

    /**
     * 获取字典类型详情
     * @param dictCode
     * @param isHasList
     * @return com.ren.system.entity.DictType
     * @author ren
     * @date 2025/05/23 13:27
     */
    @Override
    public DictType getDictTypeByDictCode(String dictCode, int isHasList) {
        DictType dictType = dictTypeMapper.selectDictTypeByDictCode(dictCode);
        if(ObjUtil.isNotNull(dictType) && isHasList == 1){
            List<DictData> dictDataList = dictDataService.listDictDataByDictType(dictCode);
            dictType.setDictDataList(dictDataList);
        }
        return dictType;
    }
}