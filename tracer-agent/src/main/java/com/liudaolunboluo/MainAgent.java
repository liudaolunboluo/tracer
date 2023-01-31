package com.liudaolunboluo;

import com.liudaolunboluo.transformer.TracerTransformer;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TestAgent
 * @date 2023/1/20
 */
@Slf4j
public class MainAgent {

    private static final String CLASS_NAME = "com.zyf.jinxServer.agent.Base";

    /**
     * jvm 参数形式启动，运行此方法
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        agent(inst);
    }

    /**
     * 动态 attach 方式启动，运行此方法
     *
     * @param agentArgs
     * @param inst
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        agent(inst);
    }

    private static void agent(Instrumentation inst) {
        inst.addTransformer(new TracerTransformer(), true);
        try {
            Class targetClass = Arrays.stream(inst.getAllLoadedClasses()).filter(clazz -> clazz.getName().equalsIgnoreCase(CLASS_NAME)).findFirst()
                    .orElse(null);
            if (targetClass != null) {
                inst.retransformClasses(targetClass);
            } else {
                log.warn("class:{} not found!", CLASS_NAME);
            }
        } catch (Exception e) {
            log.error("agent load failed!", e);
        }
    }

}
