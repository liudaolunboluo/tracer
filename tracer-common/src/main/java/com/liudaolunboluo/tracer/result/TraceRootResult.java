package com.liudaolunboluo.tracer.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TraceRootResult
 * @Description: trace结果根结点对象
 * @date 2023/3/15
 */
@Data
public class TraceRootResult implements Serializable {

    private static final long serialVersionUID = 5772948070021647479L;

    /**
     * 子节点
     */
    private List<TraceNodeResult> children;

    /**
     * 类加载器
     */
    private String classloader;

    /**
     * 是否守护
     */
    private Boolean daemon;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 线程ID
     */
    private Integer threadId;

    /**
     * 线程名称
     */
    private String threadName;

    /**
     * 时间戳
     */
    private Long timestamp;
}
