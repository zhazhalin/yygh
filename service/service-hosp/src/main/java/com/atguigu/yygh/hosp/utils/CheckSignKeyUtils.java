package com.atguigu.yygh.hosp.utils;

import com.atguigu.yygh.common.util.BeanUtils;
import com.atguigu.yygh.common.util.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/19 18:13
 */
@Repository
public class CheckSignKeyUtils {
    private static HospitalSetService hospitalSetService=null;
    public static boolean checkSignKey(Map<String, String[]> map, HttpServletRequest request){
        hospitalSetService= BeanUtils.getBean(HospitalSetService.class);
        String sign = map.get("sign")[0]; //获取传递过来的signkey，这里是已经用MD5加密过后的
        String signKey=hospitalSetService.getSignKey(map.get("hoscode")[0]);//根据医院编码查询签名
        return sign.equalsIgnoreCase(MD5.encrypt(signKey))? true:false; //判断签名是否一致
    }
}
