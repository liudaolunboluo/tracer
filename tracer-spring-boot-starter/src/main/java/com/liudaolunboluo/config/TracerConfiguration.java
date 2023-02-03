package com.liudaolunboluo.config;

import com.liudaolunboluo.agent.TracerAttacher;
import com.liudaolunboluo.launcher.TracerLauncher;
import com.liudaolunboluo.param.TracerAttachParam;
import com.liudaolunboluo.property.TracerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TracerConfiguration
 * @Description: tracer配置
 * @date 2023/2/1
 */
@ConditionalOnProperty(name = "spring.tracer.enabled", matchIfMissing = false)
@EnableConfigurationProperties({ TracerProperties.class })
@Slf4j
public class TracerConfiguration implements ApplicationListener<AvailabilityChangeEvent> {

    @Autowired
    private TracerProperties tracerProperties;

    @Override
    public void onApplicationEvent(AvailabilityChangeEvent event) {
        if (ReadinessState.ACCEPTING_TRAFFIC == event.getState()) {
            TracerLauncher tracerLauncher = new TracerLauncher();
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            TracerAttachParam tracerAttachParam = tracerProperties.convert2TracerAttachParam();
            tracerAttachParam.setPid(pid);
            tracerLauncher.launcher(tracerAttachParam);
            log.info("tracer launch success");
        }
    }

}
