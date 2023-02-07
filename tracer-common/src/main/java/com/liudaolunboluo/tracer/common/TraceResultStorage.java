package com.liudaolunboluo.tracer.common;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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

    private static final BlockingQueue<String> PRODUCE_RESULT = new LinkedBlockingQueue<>();

    private static final List<String> ALL_RESULT = new CopyOnWriteArrayList<>();

    private static final String DELIMITER = "#";

    public List<String> getAllResults() {
        return ALL_RESULT;
    }

    public String pollNewResult() throws InterruptedException {
        return PRODUCE_RESULT.poll(1, TimeUnit.SECONDS);
    }

    public void saveResult(String traceTreeResult, String className, String methodName) {
        ALL_RESULT.add(traceTreeResult);
        saveBaseResult(TRACE_TREE_RESULT, traceTreeResult, className, methodName);
        PRODUCE_RESULT.offer(traceTreeResult);
    }

    public void saveOriginalResult(String originalResult, String className, String methodName) {
        saveBaseResult(ORIGINAL_RESULT, originalResult, className, methodName);
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

    public List<String> getOriginalResult(String className, String methodName) {
        return ORIGINAL_RESULT.get(className + "#" + methodName);
    }

    private void saveBaseResult(Map<String, List<String>> storage, String result, String className, String methodName) {
        String key = className + DELIMITER + methodName;
        if (storage.get(key) == null) {
            List<String> resultList = new CopyOnWriteArrayList<>();
            resultList.add(result);
            storage.put(key, resultList);
        } else {
            storage.get(key).add(result);
        }

    }
}
