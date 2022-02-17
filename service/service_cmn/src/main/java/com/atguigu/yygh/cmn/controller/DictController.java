package com.atguigu.yygh.cmn.controller;

import com.atguigu.common.result.Result;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/15 21:36
 */
@Api(tags = "数据字典")
@CrossOrigin
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {
    @Autowired(required = false)
    private DictService dictService;
    //根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id) {
        List<Dict> list = dictService.findChlidData(id);
        return Result.ok(list);
    }

}
