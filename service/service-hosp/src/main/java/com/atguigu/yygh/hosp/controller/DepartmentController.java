package com.atguigu.yygh.hosp.controller;

import com.atguigu.common.result.Result;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/21 17:37
 */
@RestController
@CrossOrigin
@Api(tags = "科室信息列表")
@RequestMapping("/admin/hosp/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;
    //根据医院编号，获取所有科室列表
    @ApiOperation("查询所有科室信息")
    @GetMapping("{hoscode}")
    public Result getDeparmentList(@PathVariable String hoscode){
        //这里返回用deparmentvo对象返回比较合适，里面有children可以显示下面子节点
        List<DepartmentVo> departmentVoList= departmentService.findDeptTree(hoscode);
        return departmentVoList!=null ? Result.ok(departmentVoList):Result.fail("无数据");
    }
}
