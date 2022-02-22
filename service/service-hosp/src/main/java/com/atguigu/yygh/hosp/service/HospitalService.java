package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/19 10:42
 */
public interface HospitalService {
    void save(Map<String, Object> stringObjectMap);

    Hospital getHospByHoscode(String hoscode);

    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    Hospital updateStatus(String id, Integer status);

    Map<String,Object> getDetailById(String id);

    String getHosname(String hoscode);

    List<Hospital> findByHosname(String hosname);
}
