package com.liudaolunboluo.tracer.property;

import com.liudaolunboluo.tracer.param.TargetClass;
import com.liudaolunboluo.tracer.param.TracerAttachParam;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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
     * 是否忽略JDK
     */
    private Boolean isSkipJdk = true;

    List<TargetClass> targetClassList;

    public TracerAttachParam buildTracerAttachParam(String pid) {
        return TracerAttachParam.builder().targetClassList(targetClassList).isSkipJdk(isSkipJdk).pid(pid).build();
    }
}
