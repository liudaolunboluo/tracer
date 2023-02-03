package com.liudaolunboluo.spy;

import com.liudaolunboluo.listener.AdviceListener;
import com.liudaolunboluo.listener.AdviceListenerManager;
import com.liudaolunboluo.listener.InvokeTraceable;
import com.liudaolunboluo.common.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <pre>
 * 怎么从 className|methodDesc 到 id 对应起来？？
 * 当id少时，可以id自己来判断是否符合？
 *
 * 如果是每个 className|methodDesc 为 key ，是否
 * </pre>
 *
 * @author hengyunabc 2020-04-24
 */
@Slf4j
public class SpyImpl extends SpyAPI.AbstractSpy {

    @Override
    public void atEnter(Class<?> clazz, String methodInfo, Object target, Object[] args) {
        ClassLoader classLoader = clazz.getClassLoader();

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[0];
        String methodDesc = info[1];
        // TODO listener 只用查一次，放到 thread local里保存起来就可以了！
        List<AdviceListener> listeners = AdviceListenerManager.queryAdviceListeners(classLoader, clazz.getName(), methodName, methodDesc);
        if (listeners != null) {
            for (AdviceListener adviceListener : listeners) {
                try {
                    adviceListener.before(clazz, methodName, methodDesc, target, args);
                } catch (Throwable e) {
                    log.error("class: {}, methodInfo: {}", clazz.getName(), methodInfo, e);
                }
            }
        }

    }

    @Override
    public void atExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Object returnObject) {
        ClassLoader classLoader = clazz.getClassLoader();

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[0];
        String methodDesc = info[1];

        List<AdviceListener> listeners = AdviceListenerManager.queryAdviceListeners(classLoader, clazz.getName(), methodName, methodDesc);
        if (listeners != null) {
            for (AdviceListener adviceListener : listeners) {
                try {
                    adviceListener.afterReturning(clazz, methodName, methodDesc, target, args, returnObject);
                } catch (Throwable e) {
                    log.error("class: {}, methodInfo: {}", clazz.getName(), methodInfo, e);
                }
            }
        }
    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Throwable throwable) {
        ClassLoader classLoader = clazz.getClassLoader();

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[0];
        String methodDesc = info[1];

        List<AdviceListener> listeners = AdviceListenerManager.queryAdviceListeners(classLoader, clazz.getName(), methodName, methodDesc);
        if (listeners != null) {
            for (AdviceListener adviceListener : listeners) {
                try {
                    adviceListener.afterThrowing(clazz, methodName, methodDesc, target, args, throwable);
                } catch (Throwable e) {
                    log.error("class: {}, methodInfo: {}", clazz.getName(), methodInfo, e);
                }
            }
        }
    }

    @Override
    public void atBeforeInvoke(Class<?> clazz, String invokeInfo, Object target) {
        ClassLoader classLoader = clazz.getClassLoader();
        String[] info = StringUtils.splitInvokeInfo(invokeInfo);
        String owner = info[0];
        String methodName = info[1];
        String methodDesc = info[2];

        List<AdviceListener> listeners = AdviceListenerManager.queryTraceAdviceListeners(classLoader, clazz.getName(), owner, methodName, methodDesc);
        if (listeners != null) {
            for (AdviceListener adviceListener : listeners) {
                try {
                    //                    if (skipAdviceListener(adviceListener)) {
                    //                        continue;
                    //                    }
                    final InvokeTraceable listener = (InvokeTraceable) adviceListener;
                    listener.invokeBeforeTracing(classLoader, owner, methodName, methodDesc, Integer.parseInt(info[3]));
                } catch (Throwable e) {
                    log.error("class: {}, invokeInfo: {}", clazz.getName(), invokeInfo, e);
                }
            }
        }
    }

    @Override
    public void atAfterInvoke(Class<?> clazz, String invokeInfo, Object target) {
        ClassLoader classLoader = clazz.getClassLoader();
        String[] info = StringUtils.splitInvokeInfo(invokeInfo);
        String owner = info[0];
        String methodName = info[1];
        String methodDesc = info[2];
        List<AdviceListener> listeners = AdviceListenerManager.queryTraceAdviceListeners(classLoader, clazz.getName(), owner, methodName, methodDesc);

        if (listeners != null) {
            for (AdviceListener adviceListener : listeners) {
                try {

                    final InvokeTraceable listener = (InvokeTraceable) adviceListener;
                    listener.invokeAfterTracing(classLoader, owner, methodName, methodDesc, Integer.parseInt(info[3]));
                } catch (Throwable e) {
                    log.error("class: {}, invokeInfo: {}", clazz.getName(), invokeInfo, e);
                }
            }
        }

    }

    @Override
    public void atInvokeException(Class<?> clazz, String invokeInfo, Object target, Throwable throwable) {
        ClassLoader classLoader = clazz.getClassLoader();
        String[] info = StringUtils.splitInvokeInfo(invokeInfo);
        String owner = info[0];
        String methodName = info[1];
        String methodDesc = info[2];

        List<AdviceListener> listeners = AdviceListenerManager.queryTraceAdviceListeners(classLoader, clazz.getName(), owner, methodName, methodDesc);

        if (listeners != null) {
            for (AdviceListener adviceListener : listeners) {
                try {
                    final InvokeTraceable listener = (InvokeTraceable) adviceListener;
                    listener.invokeThrowTracing(classLoader, owner, methodName, methodDesc, Integer.parseInt(info[3]));
                } catch (Throwable e) {
                    log.error("class: {}, invokeInfo: {}", clazz.getName(), invokeInfo, e);
                }
            }
        }
    }

}