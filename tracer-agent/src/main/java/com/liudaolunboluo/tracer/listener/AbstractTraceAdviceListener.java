package com.liudaolunboluo.tracer.listener;

import com.alibaba.fastjson.JSON;
import com.liudaolunboluo.tracer.TraceResultStorage;
import com.liudaolunboluo.tracer.trace.ThreadLocalWatch;
import com.liudaolunboluo.tracer.trace.TraceEntity;
import com.liudaolunboluo.tracer.view.TraceView;
import lombok.extern.slf4j.Slf4j;

/**
 * ；
 *
 * @author ralf0131 2017-01-06 16:02.
 */
@Slf4j
public class AbstractTraceAdviceListener extends AdviceListenerAdapter {

    protected final ThreadLocalWatch threadLocalWatch = new ThreadLocalWatch();
    protected final ThreadLocal<TraceEntity> threadBoundEntity = new ThreadLocal<>();
    protected final TraceView traceView = new TraceView();

    protected TraceEntity threadLocalTraceEntity(ClassLoader loader) {
        TraceEntity traceEntity = threadBoundEntity.get();
        if (traceEntity == null) {
            traceEntity = new TraceEntity(loader);
            threadBoundEntity.set(traceEntity);
        }
        return traceEntity;
    }

    @Override
    public void destroy() {
        threadBoundEntity.remove();
    }

    @Override
    public void before(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args) throws Throwable {
        TraceEntity traceEntity = threadLocalTraceEntity(loader);
        traceEntity.tree.begin(clazz.getName(), method.getName(), -1, false);
        traceEntity.deep++;
        // 开始计算本次方法调用耗时
        threadLocalWatch.start();
    }

    @Override
    public void afterReturning(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args, Object returnObject) {
        threadLocalTraceEntity(loader).tree.end();
        finishing(loader, clazz.getName(), method.getName());
    }

    @Override
    public void afterThrowing(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args, Throwable throwable) {
        int lineNumber = -1;
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        if (stackTrace.length != 0) {
            lineNumber = stackTrace[0].getLineNumber();
        }

        threadLocalTraceEntity(loader).tree.end(throwable, lineNumber);
        finishing(loader, clazz.getName(), method.getName());
    }

    private void finishing(ClassLoader loader, String className, String methodName) {
        // 本次调用的耗时
        TraceEntity traceEntity = threadLocalTraceEntity(loader);
        if (traceEntity.deep >= 1) {
            traceEntity.deep--;
        }
        if (traceEntity.deep == 0) {
            try {
                String result = traceView.draw(traceEntity.getModel());
                TraceResultStorage.saveTraceTreeResult(result, className, methodName);
                String methodKey = getMethodKey(className, methodName);
                if (targetMethodMap.get(methodKey) != null && targetMethodMap.get(methodKey).getIsSaveOriginalResult()) {
                    TraceResultStorage.saveOriginalResult(JSON.toJSONString(traceEntity.getModel()), className, methodName);
                }
                System.out.println(result);
            } catch (Throwable e) {
                log.warn("trace failed.", e);
            } finally {
                threadBoundEntity.remove();
            }
        }
    }
}
