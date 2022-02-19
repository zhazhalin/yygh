package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.common.exception.YyghException;
import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.yygh.common.helper.HttpRequestHelper;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.hosp.utils.CheckSignKeyUtils;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/19 10:46
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    private HospitalService hospitalService;
    //注入目的是为了查询hospitalset里面的signkey，然后和上传进来的signkey进行对比
    @Autowired
    private HospitalSetService hospitalSetService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;

    //上传医院接口
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        //获取传递过来的医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        boolean isSame = CheckSignKeyUtils.checkSignKey(parameterMap, request);//判断签名是否一致
        if (isSame) {
            //logoData在传输过程中会把+转变成空格，所以我们要重新处理一下
            String logoData = parameterMap.get("logoData")[0];
            String logoDataNow = logoData.replaceAll(" ", "+");
            stringObjectMap.put("logoData", logoDataNow);
            //然后调用service的方法
            hospitalService.save(stringObjectMap);
            return Result.ok();
        } else {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
    }

    //显示医院信息接口
    @PostMapping("/hospital/show")
    public Result hospitalShow(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        boolean isSame = CheckSignKeyUtils.checkSignKey(parameterMap, request);//判断签名是否一致
        if (!isSame) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //根据医院编号查询该医院
        Hospital hospital = hospitalService.getHospByHoscode(parameterMap.get("hoscode")[0]);
        return Result.ok(hospital);
    }

    //上传科室信息接口
    @ApiOperation(value = "上传科室")
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        //获取传递过来的医院信息
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean isSame = CheckSignKeyUtils.checkSignKey(parameterMap, request);
        if (!isSame) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //然后调用service的方法
        departmentService.save(stringObjectMap);
        return Result.ok();
    }

    //科室信息列表
    @PostMapping("/department/list")
    public Result departmentList(HttpServletRequest request) {
        //获取传递过来的医院信息
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //获取hoscode
        String hoscode = (String) stringObjectMap.get("hoscode");
        //如果pageNum和limit均为空，则设置默认值（三元表达式）
        Integer pageNum = StringUtils.isEmpty(stringObjectMap.get("page")) ? 1 : Integer.parseInt((String) stringObjectMap.get("page"));
        Integer limit = StringUtils.isEmpty(stringObjectMap.get("limit")) ? 1 : Integer.parseInt((String) stringObjectMap.get("limit"));
        //验证签名
        boolean isLegal = CheckSignKeyUtils.checkSignKey(request.getParameterMap(), request);
        if (!isLegal) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        DepartmentQueryVo departmentVo = new DepartmentQueryVo();
        departmentVo.setHoscode(hoscode);
        //然后调用service的方法
        Page<Department> departments = departmentService.findPageDepartment(pageNum, limit, departmentVo);
        return Result.ok(departments);
    }

    //删除科室信息
    @PostMapping("department/remove")
    public Result departmentRemove(HttpServletRequest request) {
        //获取传递过来的医院信息
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String hoscode = (String) stringObjectMap.get("hoscode");
        String depcode = (String) stringObjectMap.get("depcode");
        boolean isLegal = CheckSignKeyUtils.checkSignKey(request.getParameterMap(), request);
        if (!isLegal) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }

    //上传排班信息
    @ApiOperation(value = "上传排版信息接口")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        //获取传递过来的排班信息
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean isLegal = CheckSignKeyUtils.checkSignKey(request.getParameterMap(), request);
        if (!isLegal) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.save(stringObjectMap);
        return Result.ok();
    }

    @ApiOperation(value = "获取排班分页列表")
    @PostMapping("schedule/list")
    public Result schedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        //非必填
        String depcode = (String) paramMap.get("depcode");
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String) paramMap.get("limit"));
//必须参数校验 略
        boolean isLegal = CheckSignKeyUtils.checkSignKey(request.getParameterMap(), request);
        if (isLegal) {
            String hoscode = (String) paramMap.get("hoscode");
            if (StringUtils.isEmpty(hoscode)) {
                throw new YyghException(ResultCodeEnum.PARAM_ERROR);
            }
////签名校验
//            if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
//                throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//            }

            ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
            scheduleQueryVo.setHoscode(hoscode);
            scheduleQueryVo.setDepcode(depcode);
            Page<Schedule> pageModel = scheduleService.selectPage(page, limit, scheduleQueryVo);
            return Result.ok(pageModel);
        } else {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

    }
    @ApiOperation(value = "删除科室")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
//必须参数校验 略
        String hoscode = (String)paramMap.get("hoscode");
//必填
        String hosScheduleId = (String)paramMap.get("hosScheduleId");
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
//签名校验
        if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }


}
