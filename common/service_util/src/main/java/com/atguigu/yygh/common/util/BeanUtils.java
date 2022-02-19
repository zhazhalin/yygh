package com.atguigu.yygh.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author zhazhalin
 * @version 1.0
 * @date 2022/2/19 21:14
 */
@Slf4j
public class BeanUtils implements ApplicationContextAware, DisposableBean {
    private static ApplicationContext context=null;

    public static <T> T getBean(Class<T> requiredType){
        if(context==null){
            throw new IllegalStateException("applicaitonContext属性未注入, 请在SpringBoot启动类中注册BeanUtil.");
        }
        return context.getBean(requiredType);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (BeanUtils.context != null) {
            log.warn("BeanUtil中的ApplicationContext被覆盖, 原有ApplicationContext为:" + BeanUtils.context);
        }
        BeanUtils.context=applicationContext;
    }

    @Override
    public void destroy() throws Exception {

    }
}
