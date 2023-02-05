package com.liudaolunboluo.tracer.property;

import com.liudaolunboluo.tracer.param.TracerAttachParam;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TracerProperties
 * @Description: tracer相关配置
 * @date 2023/2/1
 */
@ConfigurationProperties(prefix = "tracer")
@Data
public class TracerProperties {

    /**
     * 目标类名
     */
    private String targetClassName;

    /**
     * 目标方法名
     */
    private String targetMethodName;

    /**
     * 是否忽略jdk方法
     */
    private Boolean isSkipJdk;

    public TracerAttachParam convert2TracerAttachParam() {
        TracerAttachParam tracerAttachParam = new TracerAttachParam();
        tracerAttachParam.setTargetMethodName(this.targetMethodName);
        tracerAttachParam.setTargetClassName(this.targetClassName);
        tracerAttachParam.setIsSkipJdk(this.isSkipJdk);
        return tracerAttachParam;
    }
}
