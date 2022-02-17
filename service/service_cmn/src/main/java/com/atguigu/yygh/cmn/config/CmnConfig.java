package com.atguigu.yygh.cmn.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/15 9:49
 */
@Configuration //分页插件
@MapperScan("com.atguigu.yygh.cmn.mapper")
public class CmnConfig {
    @Bean
    public PaginationInterceptor getPaginationInterceptor(){
        return new PaginationInterceptor();
    }
}
