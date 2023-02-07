package com.liudaolunboluo.tracer.core;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: ResultSessionManger
 * @Description: 结果显示页面会话管理类
 * @date 2023/2/7
 */
@UtilityClass
@Slf4j
public class ResultSessionManger {

    private static final Map<String, SseEmitter> SESSION_CACHE = new ConcurrentHashMap<>();

    /**
     * 注册一个会话
     *
     * @param id:会话ID
     * @return SseEmitter
     * @author zhangyunfan
     * @date 2023/2/7
     */
    public SseEmitter register(String id) {
        // 设置前端的重试时间为1s
        SseEmitter sseEmitter = new SseEmitter(3600_000L);
        SESSION_CACHE.put(id, sseEmitter);
        sseEmitter.onCompletion(() -> log.info("会话ID:{} 初始化完成", id));
        return sseEmitter;
    }

    /**
     * 广播发送内容给所有会话
     *
     * @param content:发送内容
     * @throws
     * @author zhangyunfan
     * @date 2023/2/7
     */
    public void broadcastSend(String content) {
        List<String> expiredSessionId = new ArrayList<>();
        SESSION_CACHE.forEach((id, sseEmitter) -> {
            try {
                sseEmitter.send(content.replace("\n", "<br>"));
            } catch (IOException e) {
                expiredSessionId.add(id);
            } catch (Exception ex) {
                log.error("会话ID：{} 发送广播消息失败", id, ex);
            }
        });
        if (!expiredSessionId.isEmpty()) {
            expiredSessionId.forEach(SESSION_CACHE::remove);
        }
    }
}
