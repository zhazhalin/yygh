package com.atguigu.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/20 16:04
 */
@Repository
@FeignClient("service-cmn") //要调用的服务名称
public interface DictFeignClient {

    //然后将调用的接口列出来
    @GetMapping(value = "/admin/cmn/dict/getName/{parentid}/{value}")
    public String getName(
            @PathVariable("parentid") String parentid,
            @PathVariable("value") String value);
    @GetMapping(value = "/admin/cmn/dict/getName/{value}")
    //根据value查询
    public String getName(
            @PathVariable("value") String value);
}
