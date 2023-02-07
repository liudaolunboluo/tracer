package com.liudaolunboluo.tracer.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TargetMethod
 * @Description: 目标方法
 * @date 2023/2/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetMethod {

    /**
     * 目标方法名
     */
    private String methodName;

    /**
     * 是否保存原始json内容
     */
    @Builder.Default
    private Boolean isSaveOriginalResult = false;

    /**
     * 过滤耗时大于多少,单位ms
     */
    private Long costMoreThan;

    /**
     * 最大输出数，默认为1000
     */
    @Builder.Default
    private Integer maxOutput = 1000;

}
