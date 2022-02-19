package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentReponsitory;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/19 17:52
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentReponsitory reponsitory;

    /**
     * 上传科室接口
     * @param stringObjectMap
     */
    @Override
    public void save(Map<String, Object> stringObjectMap) {
        //将map转成Department对象
        String string = JSONObject.toJSONString(stringObjectMap);
        Department department = JSONObject.parseObject(string,Department.class);
        Department exitDepartment = reponsitory.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());
        if(exitDepartment!=null){
            //已经存在
            exitDepartment.setUpdateTime(new Date());
            exitDepartment.setIsDeleted(0); //表示如果已经删除则重新添加
            reponsitory.save(exitDepartment);
        }else {
            department.setIsDeleted(0);
            department.setUpdateTime(new Date());
            department.setCreateTime(new Date());
            reponsitory.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(Integer pageNum, Integer limit, DepartmentQueryVo departmentVo) {
//        创建pageable对象，设置pageNum和limit
        Pageable pageable = PageRequest.of(pageNum - 1, limit);
        Department department=new Department();
        BeanUtils.copyProperties(departmentVo,department);
        department.setIsDeleted(0);
        //创建Example对象
        ExampleMatcher matcher=ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Department> example=Example.of(department,matcher);
        Page<Department> departments = reponsitory.findAll(example, pageable);
        return departments;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        //根据医院编号和科室编号查询科室信息
        Department department = reponsitory.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department!=null){
            //调用方法删除
            reponsitory.deleteById(department.getId());
        }
    }
}
