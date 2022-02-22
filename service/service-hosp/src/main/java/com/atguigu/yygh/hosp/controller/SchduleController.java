package com.atguigu.yygh.hosp.controller;

import com.atguigu.common.result.Result;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/21 22:09
 */
//@CrossOrigin
@RestController
@Api(tags = "医院排班信息")
@RequestMapping("/admin/hosp/schedule")
public class SchduleController {
    @Autowired
    private ScheduleService scheduleService;

    //根据医院编号和科室信息查询排班信息
    @ApiOperation("查询排班信息")
    @GetMapping("/getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(
            @PathVariable Long page,
            @PathVariable Long limit,
            @PathVariable String hoscode,
            @PathVariable String depcode) {
        Map<String, Object> map = scheduleService.getSchduleRule(page, limit, hoscode, depcode);
        return Result.ok(map);
    }

    //根据医院编号，科室编号和预约日期，查询详细的预约信息
    @ApiOperation("查询排班详细信息")
    @GetMapping("/getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(
            @PathVariable String hoscode,
            @PathVariable String depcode,
            @PathVariable String workDate){
        List<Schedule> scheduleList=scheduleService.getScheduleDetail(hoscode,depcode,workDate);
        return Result.ok(scheduleList);
    }
}
