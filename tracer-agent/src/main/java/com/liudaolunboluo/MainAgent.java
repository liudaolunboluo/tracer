package com.liudaolunboluo;

import com.liudaolunboluo.listener.TraceAdviceListener;
import com.liudaolunboluo.transformer.TracerTransformer;
import com.liudaolunboluo.common.StringUtils;
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
        System.out.println("agentArgs:" + agentArgs);
        agent(inst, agentArgs);
    }

    private static void agent(Instrumentation inst, String agentArgs) {
        //hack agentArgs暂时为类名+#+方法名，例如：com.test.Base#process
        if (StringUtils.isBlank(agentArgs)) {
            log.error("agent参数为空，结束");
            return;
        }
        String[] args = agentArgs.split(SEPARATOR);
        if (args.length != 2) {
            log.error("agent参数不符合格式，正确格式是类名+空格+方法名，例如：com.test.Base#process，结束");
            return;
        }
        inst.addTransformer(new TracerTransformer(args[0], args[1], true, new TraceAdviceListener(false)), true);
        try {
            Class targetClass = Arrays.stream(inst.getAllLoadedClasses()).filter(clazz -> clazz.getName().equalsIgnoreCase(args[0])).findFirst()
                    .orElse(null);
            if (targetClass != null) {
                inst.retransformClasses(targetClass);
            } else {
                log.warn("class:{} not found!", args[0]);
            }
        } catch (Exception e) {
            log.error("agent load failed!", e);
        }
    }

}
