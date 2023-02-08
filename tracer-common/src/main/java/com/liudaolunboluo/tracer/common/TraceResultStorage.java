package com.liudaolunboluo.tracer.common;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TraceResultStorage
 * @Description: 结果存储器
 * @date 2023/2/6
 */
@UtilityClass
public class TraceResultStorage {

    private static final Map<String, List<String>> TRACE_TREE_RESULT = new ConcurrentHashMap<>();

    private static final List<String> ALL_RESULT = new CopyOnWriteArrayList<>();

    private static final String DELIMITER = "#";

    public List<String> getAllResults() {
        return ALL_RESULT;
    }

    public void saveResult(String traceTreeResult, String className, String methodName) {
        ALL_RESULT.add(traceTreeResult);
        saveBaseResult(traceTreeResult, className, methodName);
    }

    public List<String> getTraceTreeResult(String className, String methodName) {
        return TRACE_TREE_RESULT.get(className + "#" + methodName);
    }

    public int getTraceTreeResultCount(String className, String methodName) {
        if (TRACE_TREE_RESULT.get(className + "#" + methodName) == null) {
            return 0;
        }
        return TRACE_TREE_RESULT.get(className + "#" + methodName).size();
    }

    private void saveBaseResult(String result, String className, String methodName) {
        String key = className + DELIMITER + methodName;
        if (TraceResultStorage.TRACE_TREE_RESULT.get(key) == null) {
            List<String> resultList = new CopyOnWriteArrayList<>();
            resultList.add(result);
            TraceResultStorage.TRACE_TREE_RESULT.put(key, resultList);
        } else {
            TraceResultStorage.TRACE_TREE_RESULT.get(key).add(result);
        }

    }
}
