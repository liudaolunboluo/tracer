package com.liudaolunboluo.tracer.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TracerAttachParam
 * @Description: attach参数
 * @date 2023/2/1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TracerAttachParam {

    /**
     * 目标java进程pid
     */
    private String pid;

    /**
     * 是否忽略JDK
     */
    @Builder.Default
    private Boolean isSkipJdk = true;

    /**
     * 目标类名
     */
    private List<TargetClass> targetClassList;
}
