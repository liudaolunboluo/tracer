package com.liudaolunboluo.tracer.callback;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TracerCallbackManger
 * @Description: callback管理类
 * @date 2023/2/8
 */
@UtilityClass
@Slf4j
public class TracerCallbackManger {

    private static final Map<String, TraceCallback> CALLBACK_CACHE = new ConcurrentHashMap<>();

    public void registerCallback(TraceCallback traceCallbackInstance) {
        CALLBACK_CACHE.put(traceCallbackInstance.getClass().getName(), traceCallbackInstance);
    }

    public TraceCallback getCallbackInstance(Class<? extends TraceCallback> callbackClass) {
        TraceCallback traceCallback = CALLBACK_CACHE.get(callbackClass.getName());
        if (traceCallback == null) {
            //这里可能会有多个线程同时获取到为空的情况然后重复newInstance put，我想了一下，如果加上synchronized那开销太大了，如果是重复newInstance put无非就是多点空间
            //而且多余的会被回收掉，典型的空间换时间，如果你有更好的解决方案请pr和提issue，感谢！
            try {
                traceCallback = callbackClass.newInstance();
                CALLBACK_CACHE.put(callbackClass.getName(), traceCallback);
            } catch (Exception e) {
                log.error("load callback:{} faild", callbackClass.getName(), e);
            }
        }
        return traceCallback;
    }

}
