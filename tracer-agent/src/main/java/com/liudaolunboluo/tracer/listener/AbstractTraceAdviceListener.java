package com.liudaolunboluo.tracer.listener;

import com.alibaba.fastjson.JSON;
import com.liudaolunboluo.tracer.callback.TraceCallbackResult;
import com.liudaolunboluo.tracer.callback.TracerCallbackManger;
import com.liudaolunboluo.tracer.common.TraceResultStorage;
import com.liudaolunboluo.tracer.param.TargetMethod;
import com.liudaolunboluo.tracer.trace.ThreadLocalWatch;
import com.liudaolunboluo.tracer.trace.TraceEntity;
import com.liudaolunboluo.tracer.view.TraceView;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * ；
 *
 * @author ralf0131 2017-01-06 16:02.
 */
@Slf4j
public class AbstractTraceAdviceListener extends AdviceListenerAdapter {

    protected final ThreadLocalWatch threadLocalWatch = new ThreadLocalWatch();
    protected final ThreadLocal<TraceEntity> threadBoundEntity = new ThreadLocal<>();

    @Setter
    @Getter
    protected List<Class> callbackList;

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
        finishing(loader, clazz.getName(), method.getName(), args);
    }

    @Override
    public void afterThrowing(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args, Throwable throwable) {
        int lineNumber = -1;
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        if (stackTrace.length != 0) {
            lineNumber = stackTrace[0].getLineNumber();
        }

        threadLocalTraceEntity(loader).tree.end(throwable, lineNumber);
        finishing(loader, clazz.getName(), method.getName(), args);
    }

    private void finishing(ClassLoader loader, String className, String methodName, Object[] args) {
        // 本次调用的耗时
        TraceEntity traceEntity = threadLocalTraceEntity(loader);
        if (traceEntity.deep >= 1) {
            traceEntity.deep--;
        }
        if (traceEntity.deep == 0) {
            try {
                TargetMethod targetMethod = targetMethodMap.get(getMethodKey(className, methodName));
                if (targetMethod == null) {
                    return;
                }
                //hack oom risk，traceView这里如果new的话确实有OOM风险，其实这里不new也可以，就是要自己再去分析一下最大耗时节点等等信息，空间换时间吧，主要原因
                //还是直接复用了arthas对trace结果的分析，后面有时间自己重写一套算法来解析，就不用new了，这里先记一下，如果因为用了tracer之后OOM请重点排查这里
                TraceView traceView = new TraceView();
                TraceCallbackResult traceCallbackResult = traceView.generateResult(traceEntity.getModel(), className, methodName);
                double cost = traceCallbackResult.getCost();
                boolean isSave = conditionSave(className, methodName, targetMethod, cost);
                if (isSave) {
                    TraceResultStorage.saveResult(traceCallbackResult.getTraceTreeResult(), className, methodName);
                }
                traceCallbackResult.setArgs(args);
                //最后回调
                if (callbackList != null && !callbackList.isEmpty() && isSave) {
                    for (Class callback : callbackList) {
                        if (Boolean.TRUE.equals(targetMethod.getIsSaveOriginalResult())) {
                            traceCallbackResult.setOriginalResult(JSON.toJSONString(traceEntity.getModel()));
                        }
                        try {
                            TracerCallbackManger.getCallbackInstance(callback).callback(traceCallbackResult);
                        } catch (Exception e) {
                            log.error("执行:{}回调的时候异常", callback.getName(), e);
                        }
                    }
                }
            } catch (Throwable e) {
                log.warn("trace failed.", e);
            } finally {
                threadBoundEntity.remove();
            }
        }
    }

    private boolean conditionSave(String className, String methodName, TargetMethod targetMethod, double cost) {
        boolean isSave = true;
        if (targetMethod.getCostMoreThan() != null && cost < targetMethod.getCostMoreThan()) {
            isSave = false;
        }
        if (TraceResultStorage.getTraceTreeResultCount(className, methodName) >= targetMethod.getMaxOutput()) {
            isSave = false;
        }
        return isSave;
    }
}
