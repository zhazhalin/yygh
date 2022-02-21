package com.atguigu.yygh.cmn.controller;

import com.atguigu.common.result.Result;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    //首先通过dictcode(字典类型)获得该类型全部数据，然后通过parentid获得该类型的全部数据
    @ApiOperation(value = "获取数据字典名称")
    @GetMapping(value = "/getName/{parentid}/{value}")
    public String getName(
            @ApiParam(name = "parentid", value = "上级编码", required = true)
            @PathVariable("parentid") String parentid,
            @ApiParam(name = "value", value = "值", required = true)
            @PathVariable("value") String value) {
        return dictService.getNameByParentDictCodeAndValue(parentid, value);
    }

    @ApiOperation(value = "获取数据字典名称")
    @ApiImplicitParam(name = "value", value = "值", required = true, dataType = "Long", paramType = "path")
    @GetMapping(value = "/getName/{value}")
    public String getName(
            @ApiParam(name = "value", value = "值", required = true)
            @PathVariable("value") String value) {
        return dictService.getNameByParentDictCodeAndValue("", value);
    }

    //根据dictcode获取下级节点，实现省市联动效果
    @ApiOperation(value = "根据dictcode获取下级节点")
    @GetMapping(value = "/findByDictCode/{dictCode}")
    public Result<List<Dict>> findByDictCode(
            @PathVariable("dictCode") String dictCode) {
        List<Dict> dicts = dictService.findByDictCode(dictCode);
        return Result.ok(dicts);
    }
}
