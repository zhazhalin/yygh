package com.atguigu.yygh.cmn.service.impl;

import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/15 21:31
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChlidData(Long id) {
        QueryWrapper<Dict> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("parent_id",id);
        List<Dict> dicts = baseMapper.selectList(queryWrapper);
        for (Dict dict:dicts) {
            Long dId=dict.getId();
            dict.setHasChildren(isChildren(dId));
        }
        return dicts;
    }
    //判断id下面是否有子节点
    private boolean isChildren(Long id){
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("parent_id",id);
        return baseMapper.selectCount(queryWrapper)>0;
    }
}
