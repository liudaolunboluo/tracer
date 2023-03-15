package com.liudaolunboluo.tracer.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TraceNodeResult
 * @Description: trace结果子结点对象
 * @date 2023/3/15
 */
@Data
public class TraceNodeResult implements Serializable {

    private static final long serialVersionUID = -552137590240659358L;

    /**
     * 子节点
     */
    private List<TraceNodeResult> children;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 耗时
     */
    private Long cost;

    /**
     * 最大耗时
     */
    private Long maxCost;

    /**
     * 最小耗时
     */
    private Long minCost;

    /**
     * 总耗时
     */
    private Long totalCost;

    /**
     * 行数
     */
    private Integer lineNumber;

    /**
     * 执行次数
     */
    private Integer times;

}
