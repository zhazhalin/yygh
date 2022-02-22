package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.util.DayOfWeekUtils;
import com.atguigu.yygh.hosp.repository.ScheduleReponsitory;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/19 23:46
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleReponsitory reponsitory;
    @Autowired
    private MongoTemplate mongoTemplate; //使用这个可以更加方便统计，聚合，分组，排序更加方便
    @Autowired
    HospitalService hospitalService;
    @Autowired
    DepartmentService departmentService;
    @Override
    public void save(Map<String, Object> paramMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(paramMap), Schedule.class);
        Schedule scheduleExit = reponsitory.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        if(null != scheduleExit) {
            scheduleExit.setStatus(1);
            scheduleExit.setUpdateTime(new Date());
            scheduleExit.setIsDeleted(0);
            reponsitory.save(scheduleExit);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            reponsitory.save(schedule);
        }
    }
    @Override
    public Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //0为第一页
        Pageable pageable = PageRequest.of(page-1, limit, sort);

        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);

        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        //创建实例
        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> pages = reponsitory.findAll(example, pageable);
        return pages;
    }

    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        //根据参数查询排班信息
        List<Schedule> scheduleList = reponsitory.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
        //根据排班信息继续封装其他信息：医院名称，科室编号，日期对应周
        scheduleList.stream().forEach(item->{
            this.setParam(item);
        });
        return scheduleList;
    }

    private void setParam(Schedule item) {
        String hosname = hospitalService.getHosname(item.getHoscode());
        String depname = departmentService.getDepname(item.getHoscode(),item.getDepcode());
        item.getParam().put("hosname",hosname);
        item.getParam().put("depname",depname);
        item.getParam().put("dayOfWeek",DayOfWeekUtils.getDayOfWeek(new DateTime(item.getWorkDate())));
    }

    @Override
    public Map<String, Object> getSchduleRule(Long page, Long limit, String hoscode, String depcode) {
        //根据医院编号和科室编号查询排班信息，用mongotemplate需要结合criteria
        Criteria criteria=Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        //根据工作日期进行分组
        Aggregation aggregation=Aggregation.newAggregation(
            Aggregation.match(criteria), //匹配条件
                Aggregation.group("workDate") //分组字段
                .first("workDate").as("workDate")
                //统计号源数量
                .count().as("docCount")
                .sum("reservedNumber").as("reservedNumber") //可预约数
                .sum("availableNumber").as("availableNumber"), //剩余预约数
                //排序
                Aggregation.sort(Sort.Direction.DESC,"workDate"),
                //实现分页  //进行分页处理
                Aggregation.skip((page-1)*limit),
                Aggregation.limit(limit)
        );
        //得到总记录数
        Aggregation arrTotal=Aggregation.newAggregation(
                Aggregation.match(criteria),Aggregation.group("workDate"));
        //该查询出来的只需要返回总记录数
        AggregationResults<BookingScheduleRuleVo> totalBooking = mongoTemplate.aggregate(arrTotal, Schedule.class, BookingScheduleRuleVo.class);
        //该查询出来的列表需要返回
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        int total=totalBooking.getMappedResults().size();
        //将预约时间转换成周几
        for (BookingScheduleRuleVo scheduleRuleVo : mappedResults) {
            String dayOfWeek = DayOfWeekUtils.getDayOfWeek(new DateTime(scheduleRuleVo.getWorkDate()));
            scheduleRuleVo.setDayOfWeek(dayOfWeek);
        }
        //设置返回集合
        Map<String,Object> result=new HashMap<>();
        result.put("total",total);
        //获取医院名称
        String hosname = hospitalService.getHosname(hoscode);
        result.put("hosname",hosname);
        result.put("bookingScheduleRuleList",mappedResults);
        //其他基础数据
        Map<String,Object> baseMap=new HashMap<>();
        result.put("baseMap",baseMap);
        return result;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule schedule = reponsitory.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if(null != schedule) {
            reponsitory.deleteById(schedule.getId());
        }
    }


}
