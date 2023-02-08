package com.liudaolunboluo.tracer.callback;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TraceCallback
 * @Description: trace完成之后回调接口
 * @date 2023/2/8
 */
public abstract class TraceCallback {

    /**
     * trace之后调用的逻辑
     *
     * @param traceResult:trace结果
     * @author zhangyunfan
     * @date 2023/2/8
     */
   public abstract void callback(TraceCallbackResult traceResult);
}
