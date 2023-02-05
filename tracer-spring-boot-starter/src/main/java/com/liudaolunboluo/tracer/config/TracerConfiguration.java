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
        if (tracerProperties.getTargetClassList() == null || tracerProperties.getTargetClassList().size() == 0) {
            log.warn("plz config tracer!");
            return;
        }
        TracerLauncher tracerLauncher = new TracerLauncher();
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!StringUtils.hasText(name)) {
            log.warn("can not get current pid,plz check it!");
            return;
        }
        String pid = name.split("@")[0];
        TracerAttachParam tracerAttachParam = tracerProperties.buildTracerAttachParam(pid);
        tracerLauncher.launch(tracerAttachParam);
        log.info("tracer launch success");
    }
}
