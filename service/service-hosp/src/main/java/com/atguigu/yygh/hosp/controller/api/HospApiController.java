package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/22 23:05
 */
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {
    @Autowired
    HospitalService hospitalService;
    //查询医院列表功能
    @ApiOperation("查询医院列表功能")
    @GetMapping("/findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page
            , @PathVariable Integer limit
            , HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitals = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }
    //根据医院名称模糊查询查询
    @ApiOperation("根据医院名称进行模糊查询查询")
    @GetMapping("/findByHosname/{hosname}")
    public Result findByHosname(@PathVariable String hosname){
        List<Hospital> hospitals = hospitalService.findByHosname(hosname);
        return Result.ok(hospitals);
    }
}
