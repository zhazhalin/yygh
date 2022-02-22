package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.cmn.client.DictFeignClient;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/19 10:42
 */
@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;
    //注入服务调用接口DictFeignClient
    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> stringObjectMap) {
        //首先将参数map集合转换成Hospital
        String mapString = JSONObject.toJSONString(stringObjectMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);
        //判断是否存在
        Hospital ExitHospital = hospitalRepository.getHospitalByHoscode(hospital.getHoscode());
        //如果存在，则更新
        if (ExitHospital != null) {
            hospital.setStatus(ExitHospital.getStatus());
            hospital.setCreateTime(ExitHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }//如果存在，则添加
        else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getHospByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //0为第一页
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        //创建实例
        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);
        pages.get().forEach(item->{
            this.setHostype(item);
        });//得到所有的医院信息，遍历进行医院等级的封装

        return pages;
    }

    @Override
    public Hospital updateStatus(String id, Integer status) {
        //这里的医院信息是在mongodb中的，所以可以用hospitalRepository来实现
        //首先先根据id查询出数据
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setUpdateTime(new Date());
        hospital.setStatus(status);
        Hospital save = hospitalRepository.save(hospital);
        return save;
    }

    //前台接口，根据医院名称进行模糊查询
    @Override
    public List<Hospital> findByHosname(String hosname) {
        List<Hospital> hospitals=hospitalRepository.findHospitalByHosnameLike(hosname);
        return hospitals;
    }

    @Override
    public String getHosname(String hoscode) {
        Hospital hosp = this.getHospByHoscode(hoscode);
        return hosp!=null? hosp.getHosname():"无法得到此医院名称";
    }

    @Override
    public Map<String,Object> getDetailById(String id) {
        Map<String,Object> map=new HashMap<>() ;
        //查询出医院详细信息
        Hospital hospitalDetail = setHostype(hospitalRepository.findById(id).get());
        //查询出预约信息，然后将之一起封装
        map.put("hospitalDetail",hospitalDetail);
        map.put("bookingRule",hospitalDetail.getBookingRule());
        return map;
    }

    private Hospital setHostype(Hospital hospital) {
        //根据dictcode和value值获取医院等级名称
        String hosType = dictFeignClient.getName("HosType", hospital.getHostype());
        //根据省代码获取省的名称
        String province = dictFeignClient.getName(hospital.getProvinceCode());
        //根据市代码获取市的名称
        String city = dictFeignClient.getName(hospital.getCityCode());
        //根据区代码获取区名称
        String district = dictFeignClient.getName(hospital.getDistrictCode());
        //将医院等级封装到医院对象中
        hospital.getParam().put("hosType",hosType);
        hospital.getParam().put("detailAddress",province+city+district+hospital.getAddress());
        return hospital;
    }
}
