package com.liudaolunboluo.tracer.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TracerAttachParam
 * @Description: attach参数
 * @date 2023/2/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TracerAttachParam {

    /**
     * 目标java进程pid
     */
    private String pid;

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
}
