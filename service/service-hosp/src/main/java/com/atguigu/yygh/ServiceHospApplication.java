package com.atguigu.yygh;

import com.atguigu.yygh.common.util.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/13 10:23
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu")
@EnableDiscoveryClient  //启用nacos
@EnableFeignClients(basePackages = "com.atguigu") //启用服务调用，根据FeignClient注解找到服务，然后进行调用
public class  ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
    @Bean
    public BeanUtils beanUtil() {
        return new BeanUtils();
    }
}
