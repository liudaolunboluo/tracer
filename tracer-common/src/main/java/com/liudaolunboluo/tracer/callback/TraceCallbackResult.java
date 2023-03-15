package com.liudaolunboluo.tracer.callback;

import com.liudaolunboluo.tracer.result.TraceRootResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TraceCallbackResult
 * @Description: 回调实体
 * @date 2023/2/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraceCallbackResult {

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 总耗时，单位ms
     */
    private Double cost;

    /**
     * trace树结果
     */
    private String traceTreeResult;

    /**
     * 最耗时节点类名
     */
    private String maxCostClassName;

    /**
     * 最耗时节点方法名
     */
    private String maxCostMethodName;

    /**
     * 最耗时节耗时，单位ms
     */
    private Double maxCost;

    /**
     * 原始trace结果，只有当isSaveOriginalResult为true当时候才有值
     */
    private TraceRootResult originalResult;

    /**
     * 当次调用的参数
     */
    private Object[] args;
}
