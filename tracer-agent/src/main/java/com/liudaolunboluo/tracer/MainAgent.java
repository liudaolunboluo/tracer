package com.liudaolunboluo.tracer;

import com.alibaba.fastjson.JSON;
import com.liudaolunboluo.tracer.callback.TraceCallback;
import com.liudaolunboluo.tracer.listener.TraceAdviceListener;
import com.liudaolunboluo.tracer.param.TargetClass;
import com.liudaolunboluo.tracer.transformer.TracerTransformer;
import com.liudaolunboluo.tracer.common.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TestAgent
 * @date 2023/1/20
 */
@Slf4j
public class MainAgent {

    private static final String SEPARATOR = "#";

    /**
     * jvm 参数形式启动，运行此方法
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        agent(inst, agentArgs);
    }

    /**
     * 动态 attach 方式启动，运行此方法
     *
     * @param agentArgs
     * @param inst
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        agent(inst, agentArgs);
    }

    private static void agent(Instrumentation inst, String agentArgs) {
        if (StringUtils.isBlank(agentArgs)) {
            log.error("agent参数为空，结束");
            return;
        }
        String[] configArr = agentArgs.split(" ");
        List<TargetClass> targetClasses = JSON.parseArray(configArr[0], TargetClass.class);

        Set<String> targetClassNames = targetClasses.stream().map(TargetClass::getFullClassName).collect(Collectors.toSet());
        List<Class> callBackList = Arrays.stream(inst.getAllLoadedClasses()).filter(TraceCallback.class::isAssignableFrom)
                .filter(c -> !Modifier.isAbstract(c.getModifiers())).collect(Collectors.toList());
        List<Class> matchingClasses = Arrays.stream(inst.getAllLoadedClasses()).filter(clazz -> targetClassNames.contains(clazz.getName()))
                .collect(Collectors.toList());
        inst.addTransformer(new TracerTransformer(targetClasses, new TraceAdviceListener(targetClasses, callBackList),
                configArr.length <= 1 || Boolean.parseBoolean(configArr[1])), true);
        for (Class clazz : matchingClasses) {
            try {
                inst.retransformClasses(clazz);
            } catch (Exception e) {
                log.error("class:{} agent load failed!", clazz.getName(), e);
            }
        }
    }

}
