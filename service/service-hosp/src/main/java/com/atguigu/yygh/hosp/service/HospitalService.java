package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;

import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/19 10:42
 */
public interface HospitalService {
    void save(Map<String, Object> stringObjectMap);

    Hospital getHospByHoscode(String hoscode);
}
