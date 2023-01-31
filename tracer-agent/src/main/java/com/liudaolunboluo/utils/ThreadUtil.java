package com.liudaolunboluo.utils;

import com.liudaolunboluo.spy.SpyAPI;
import com.liudaolunboluo.trace.StackModel;
import com.liudaolunboluo.trace.ThreadNode;

import java.lang.reflect.Method;

/**
 * @author hengyunabc 2015年12月7日 下午2:29:28
 */
public class ThreadUtil {

    private static boolean detectedEagleEye = false;
    public static boolean foundEagleEye = false;


    private static int MAGIC_STACK_DEPTH = 0;

    private static int findTheSpyAPIDepth(StackTraceElement[] stackTraceElementArray) {
        if (MAGIC_STACK_DEPTH > 0) {
            return MAGIC_STACK_DEPTH;
        }
        if (MAGIC_STACK_DEPTH > stackTraceElementArray.length) {
            return 0;
        }
        for (int i = 0; i < stackTraceElementArray.length; ++i) {
            if (SpyAPI.class.getName().equals(stackTraceElementArray[i].getClassName())) {
                MAGIC_STACK_DEPTH = i + 1;
                break;
            }
        }
        return MAGIC_STACK_DEPTH;
    }

    /**
     * 获取方法执行堆栈信息
     *
     * @return 方法堆栈信息
     */
    public static StackModel getThreadStackModel(ClassLoader loader, Thread currentThread) {
        StackModel stackModel = new StackModel();
        stackModel.setThreadName(currentThread.getName());
        stackModel.setThreadId(Long.toHexString(currentThread.getId()));
        stackModel.setDaemon(currentThread.isDaemon());
        stackModel.setPriority(currentThread.getPriority());
        stackModel.setClassloader(getTCCL(currentThread));

        getEagleeyeTraceInfo(loader, currentThread, stackModel);

        //stack
        StackTraceElement[] stackTraceElementArray = currentThread.getStackTrace();
        int magicStackDepth = findTheSpyAPIDepth(stackTraceElementArray);
        StackTraceElement[] actualStackFrames = new StackTraceElement[stackTraceElementArray.length - magicStackDepth];
        System.arraycopy(stackTraceElementArray, magicStackDepth, actualStackFrames, 0, actualStackFrames.length);
        stackModel.setStackTrace(actualStackFrames);
        return stackModel;
    }

    public static ThreadNode getThreadNode(ClassLoader loader, Thread currentThread) {
        ThreadNode threadNode = new ThreadNode();
        threadNode.setThreadId(currentThread.getId());
        threadNode.setThreadName(currentThread.getName());
        threadNode.setDaemon(currentThread.isDaemon());
        threadNode.setPriority(currentThread.getPriority());
        threadNode.setClassloader(getTCCL(currentThread));

        //trace_id
        StackModel stackModel = new StackModel();
        getEagleeyeTraceInfo(loader, currentThread, stackModel);
        threadNode.setTraceId(stackModel.getTraceId());
        threadNode.setRpcId(stackModel.getRpcId());
        return threadNode;
    }

    public static String getThreadTitle(StackModel stackModel) {
        StringBuilder sb = new StringBuilder("thread_name=");
        sb.append(stackModel.getThreadName()).append(";id=").append(stackModel.getThreadId()).append(";is_daemon=").append(stackModel.isDaemon())
                .append(";priority=").append(stackModel.getPriority()).append(";TCCL=").append(stackModel.getClassloader());
        if (stackModel.getTraceId() != null) {
            sb.append(";trace_id=").append(stackModel.getTraceId());
        }
        if (stackModel.getRpcId() != null) {
            sb.append(";rpc_id=").append(stackModel.getRpcId());
        }
        return sb.toString();
    }

    private static String getTCCL(Thread currentThread) {
        ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        if (null == contextClassLoader) {
            return "null";
        } else {
            return contextClassLoader.getClass().getName() + "@" + Integer.toHexString(contextClassLoader.hashCode());
        }
    }

    private static void getEagleeyeTraceInfo(ClassLoader loader, Thread currentThread, StackModel stackModel) {
        if (loader == null) {
            return;
        }
        Class<?> eagleEyeClass = null;
        if (!detectedEagleEye) {
            try {
                eagleEyeClass = loader.loadClass("com.taobao.eagleeye.EagleEye");
                foundEagleEye = true;
            } catch (Throwable e) {
                // ignore
            }
            detectedEagleEye = true;
        }

        if (!foundEagleEye) {
            return;
        }

        try {
            if (eagleEyeClass == null) {
                eagleEyeClass = loader.loadClass("com.taobao.eagleeye.EagleEye");
            }
            Method getTraceIdMethod = eagleEyeClass.getMethod("getTraceId");
            String traceId = (String) getTraceIdMethod.invoke(null);
            stackModel.setTraceId(traceId);
            Method getRpcIdMethod = eagleEyeClass.getMethod("getRpcId");
            String rpcId = (String) getRpcIdMethod.invoke(null);
            stackModel.setRpcId(rpcId);
        } catch (Throwable e) {
            // ignore
        }
    }

}
