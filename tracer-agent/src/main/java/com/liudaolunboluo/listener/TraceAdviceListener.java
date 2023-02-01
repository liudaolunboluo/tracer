package com.liudaolunboluo.listener;

import lombok.extern.slf4j.Slf4j;

/**
 * @author beiwei30 on 29/11/2016.
 */
@Slf4j
public class TraceAdviceListener extends AbstractTraceAdviceListener implements InvokeTraceable {

    /**
     * Constructor
     */
    public TraceAdviceListener(boolean verbose) {
        super.setVerbose(verbose);
    }

    /**
     * trace 会在被观测的方法体中，在每个方法调用前后插入字节码，所以方法调用开始，结束，抛异常的时候，都会回调下面的接口
     */
    @Override
    public void invokeBeforeTracing(ClassLoader classLoader, String tracingClassName, String tracingMethodName, String tracingMethodDesc, int tracingLineNumber)
            throws Throwable {
        //hack 这里我也不知道为什么spyApi的atExit方法会调用一次，arthas的话是不会的，这里先强制手动排除掉
        if (tracingClassName.equals("com/liudaolunboluo/spy/SpyAPI") && tracingMethodName.equals("atExit")) {
            return;
        }
        threadLocalTraceEntity(classLoader).tree.begin(tracingClassName, tracingMethodName, tracingLineNumber, true);

    }

    @Override
    public void invokeAfterTracing(ClassLoader classLoader, String tracingClassName, String tracingMethodName, String tracingMethodDesc, int tracingLineNumber)
            throws Throwable {
        //hack 这里我也不知道为什么atBeforeInvoke会调用一次，如果让atBeforeInvoke调用了的话，从第三个方法开始parent就会打乱，导致最后结果不对
        if (tracingMethodName.equals("atBeforeInvoke")) {
            return;
        }
        threadLocalTraceEntity(classLoader).tree.end();

    }

    @Override
    public void invokeThrowTracing(ClassLoader classLoader, String tracingClassName, String tracingMethodName, String tracingMethodDesc, int tracingLineNumber)
            throws Throwable {
        if (tracingMethodName.equals("atBeforeInvoke")) {
            return;
        }
        threadLocalTraceEntity(classLoader).tree.end(true);
    }

}
