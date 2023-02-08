package com.liudaolunboluo.tracer.core;

import com.liudaolunboluo.tracer.callback.TraceCallback;
import com.liudaolunboluo.tracer.callback.TraceCallbackResult;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: SessionTraceCallback
 * @Description: 对话回调
 * @date 2023/2/8
 */
@Slf4j
@Component
public class SessionTraceCallback extends TraceCallback {

    @Override
    public void callback(TraceCallbackResult traceResult) {
        ResultSessionManger.broadcastSend(traceResult.getTraceTreeResult());
    }
}
