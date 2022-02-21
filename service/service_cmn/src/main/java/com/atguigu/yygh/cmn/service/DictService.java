package com.atguigu.yygh.cmn.service;

import com.atguigu.yygh.model.cmn.Dict;

import java.util.List;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/15 21:01
 */
public interface DictService {
    List<Dict> findChlidData(Long id);

    String getNameByParentDictCodeAndValue(String parentid, String value);

    List<Dict> findByDictCode(String dictcode);
}
