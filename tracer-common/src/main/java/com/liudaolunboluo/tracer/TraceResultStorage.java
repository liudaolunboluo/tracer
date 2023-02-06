package com.liudaolunboluo.tracer;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final Map<String, List<String>> ORIGINAL_RESULT = new ConcurrentHashMap<>();

    private static final String DELIMITER = "#";

    public void saveTraceTreeResult(String traceTreeResult, String className, String methodName) {
        saveBaseResult(TRACE_TREE_RESULT, traceTreeResult, className, methodName);
    }

    public void saveOriginalResult(String originalResult, String className, String methodName) {
        saveBaseResult(ORIGINAL_RESULT, originalResult, className, methodName);
    }

    public List<String> getTraceTreeResult(String className, String methodName) {
        return TRACE_TREE_RESULT.get(className + "#" + methodName);
    }

    public List<String> getOriginalResult(String className, String methodName) {
        return ORIGINAL_RESULT.get(className + "#" + methodName);
    }

    private void saveBaseResult(Map<String, List<String>> storage, String result, String className, String methodName) {
        String key = className + DELIMITER + methodName;
        if (storage.get(key) == null) {
            List<String> resultList = new ArrayList<>();
            resultList.add(result);
            storage.put(key, resultList);
        } else {
            storage.get(key).add(result);
        }

    }
}
