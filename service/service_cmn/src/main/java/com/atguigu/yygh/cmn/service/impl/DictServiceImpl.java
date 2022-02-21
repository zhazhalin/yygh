package com.atguigu.yygh.cmn.service.impl;

import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/15 21:31
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    /**
     * 通过id获取子节点
     * @param id
     * @return
     */
    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator") //redis缓存开启
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

    //通过parentid和数据类型获得该类型的全部数据
    @Override
    public String getNameByParentDictCodeAndValue(String codedict, String value) {
        if(StringUtils.isEmpty(codedict)){
            //直接根据value查询
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>().eq("value",value));
            return dict.getName();
        }else {
            //根据dict_code和value一同查询
            Dict dict = this.getDict(codedict);
            Long id = dict.getId();
            Dict dict1 = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", id)
                    .eq("value", value)
            );
            return dict1.getName();
        }
    }

    @Override
    public List<Dict> findByDictCode(String dictcode) {
        //首先根据dictcode查询id，然后调用上面方法根据id查询子节点
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("dict_code", dictcode);
        Dict dict = baseMapper.selectOne(queryWrapper);
        List<Dict> chlidData = this.findChlidData(dict.getId());
        return chlidData;
    }

    //判断id下面是否有子节点
    private boolean isChildren(Long id){
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("parent_id",id);
        return baseMapper.selectCount(queryWrapper)>0;
    }
    //根据dictcode获得Dict对象
    private Dict getDict(String codedict){
        QueryWrapper<Dict> wrapper=new QueryWrapper();
        wrapper.eq("dict_code",codedict);
        Dict dict = baseMapper.selectOne(wrapper);
        return dict;
    }
}
