package com.atguigu.yygh.hosp.controller;

import com.atguigu.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/20 10:00
 */
@Service
@RestController
@CrossOrigin
@Api(tags = "医院信息管理")
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Integer page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,

            @ApiParam(name = "hospitalQueryVo", value = "查询对象", required = false)
                    HospitalQueryVo hospitalQueryVo) {
        return Result.ok(hospitalService.selectPage(page, limit, hospitalQueryVo));
    }
    //更新医院上线状态
    @ApiOperation(value = "更新上线状态")
    @PutMapping("/status/{id}/{status}")
    public Result updateStatus(@PathVariable String id, @PathVariable Integer status ) {
        Hospital hospital = hospitalService.updateStatus(id, status);
        if(StringUtils.isEmpty(hospital)){
            return Result.fail();
        }
        return Result.ok();
    }
    //获取医院详情信息
    @ApiOperation(value = "获取医院详情信息")
    @GetMapping("/detail/{id}")
    public Result getDetailById(@PathVariable String id) {
        Map<String,Object> hospitalDetail = hospitalService.getDetailById(id);
        if(StringUtils.isEmpty(hospitalDetail)){
            return Result.fail("无此医院信息");
        }
        return Result.ok(hospitalDetail);
    }
}
