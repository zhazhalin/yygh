package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/19 17:51
 */
public interface DepartmentService {
    //添加科室信息
    void save(Map<String, Object> stringObjectMap);

    Page<Department> findPageDepartment(Integer pageNum, Integer limit, DepartmentQueryVo departmentVo);

    void remove(String hoscode, String depcode);

    List<DepartmentVo> findDeptTree(String hoscode);

    String getDepname(String hoscode, String depcode);
}
