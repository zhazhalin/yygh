package com.atguigu.yygh.cmn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/15 20:30
 */
@SpringBootApplication
@ComponentScan("com.atguigu.yygh")
public class ServiceCmnApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class,args);
    }
}
