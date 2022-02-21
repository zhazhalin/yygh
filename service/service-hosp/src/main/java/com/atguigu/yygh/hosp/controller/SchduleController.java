package com.atguigu.yygh.hosp.controller;

import com.atguigu.common.result.Result;
import com.atguigu.yygh.hosp.service.ScheduleService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/21 22:09
 */
@CrossOrigin
@RestController
@RequestMapping("/admin/hosp/schdule")
public class SchduleController {
    @Autowired
    private ScheduleService scheduleService;
    //根据医院编号和科室信息查询排班信息
    @ApiOperation("查询排班信息")
    @GetMapping("/getSchduleRule/{page}/{limit}/{hoscode},{depcode}")
    public Result getSchduleRule(
            @PathVariable Long page,
            @PathVariable Long limit,
            @PathVariable String hoscode,
            @PathVariable String depcode){
        Map<String, Object> map = scheduleService.getSchduleRule(page, limit, hoscode, depcode);
        return Result.ok(map);
    }
}
