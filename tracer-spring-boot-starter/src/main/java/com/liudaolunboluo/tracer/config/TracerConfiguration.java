package com.liudaolunboluo.tracer.config;

import com.liudaolunboluo.tracer.launcher.TracerLauncher;
import com.liudaolunboluo.tracer.param.TracerAttachParam;
import com.liudaolunboluo.tracer.property.TracerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.StringUtils;

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
public class TracerConfiguration {

    @Autowired
    private TracerProperties tracerProperties;

    @PostConstruct
    public void startAttach() {
        if (!StringUtils.hasText(tracerProperties.getTargetClassName()) || !StringUtils.hasText(tracerProperties.getTargetMethodName())) {
            log.warn("please config tracer!");
            return;
        }
        TracerLauncher tracerLauncher = new TracerLauncher();
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        TracerAttachParam tracerAttachParam = tracerProperties.convert2TracerAttachParam();
        tracerAttachParam.setPid(pid);
        tracerLauncher.launcher(tracerAttachParam);
        log.info("tracer launch success");
    }
}
