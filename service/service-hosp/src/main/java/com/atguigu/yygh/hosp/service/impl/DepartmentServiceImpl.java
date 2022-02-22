package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentReponsitory;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    /**
     *根据医院code查询科室所有信息
     */
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        List<DepartmentVo> list=new ArrayList<>(); //用于封装,创建用于返回的数据集合
        //首先根据hoscode查询所有科室信息,然后再层层查询子节点
        Department departmentQuery=new Department();
        Example<Department> example =Example.of(departmentQuery);
        List<Department> departmentList = reponsitory.findAll(example);
        //这里是使用java8中新特性stream流的方式对数据继续宁分组
        Map<String, List<Department>> bigcodeList = departmentList.stream()
                .collect(Collectors.groupingBy(Department::getBigcode));

        //接着对分组集合里面的数据进行遍历
        for(Map.Entry<String,List<Department>> entry:bigcodeList.entrySet()){
            //大科室编号
            String bigcode=entry.getKey();
            //大科室下的小科室
            List<Department> departments = entry.getValue();
            //对大科室进行封装,这是一个大科室对象
            DepartmentVo departmentVo=new DepartmentVo();
            departmentVo.setDepcode(bigcode);
            departmentVo.setDepname(departments.get(0).getBigname());
            //对小科室进行封装,通过遍历将Department的小科室封装到vo类里，然后获得小科室集合，然后添加到大科室的子节点中
            List<DepartmentVo> children=new ArrayList<>();
            for (Department department : departments) {
                DepartmentVo departmentVo1=new DepartmentVo();
                departmentVo1.setDepname(department.getDepname());
                departmentVo1.setDepcode(department.getDepcode());
                children.add(departmentVo1);
            }
            departmentVo.setChildren(children);
            list.add(departmentVo);
        }
        return list;
    }

    @Override
    public String getDepname(String hoscode, String depcode) {
        //根据科室编号和医院编号查询科室名称
        Department department = reponsitory.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        return department!=null? department.getDepname():null;
    }
}
