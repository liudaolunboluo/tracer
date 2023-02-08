package com.liudaolunboluo.tracer.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: ApplicationContextHolder
 * @date 2022/10/13
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public ApplicationContextHolder() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> tClass) {
        return applicationContext != null ? applicationContext.getBean(tClass) : null;
    }

    public static <T> List<T> getBeansOfType(@Nullable Class<T> type) {
        if (applicationContext == null) {
            return new ArrayList<>();
        }
        Map<String, T> beansOfType = applicationContext.getBeansOfType(type);
        return new ArrayList<>(beansOfType.values());
    }

}
