package com.liudaolunboluo.tracer.controller;

import com.alibaba.fastjson.JSON;
import com.liudaolunboluo.tracer.common.TraceResultStorage;
import com.liudaolunboluo.tracer.core.ResultSessionManger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: ViewController
 * @Description: 网页视觉接口
 * @date 2023/2/7
 */
@Controller
@RequestMapping(path = "/view")
@Slf4j
@ConditionalOnProperty(name = "spring.tracer.enabled", matchIfMissing = false)
@EnableScheduling
public class ViewController {

    @GetMapping(path = "")
    public String index() {
        return "view.html";
    }

    /**
     * 开始链接的时候调用，就是打开网页的时候调用
     */
    @GetMapping(path = "/init")
    public SseEmitter init(String id) throws IOException {
        SseEmitter sseEmitter = ResultSessionManger.register(id);
        List<String> newResultList = new ArrayList<>();
        TraceResultStorage.getAllResults().forEach(result -> newResultList.add(result.replace("\n", "<br>")));
        sseEmitter.send(SseEmitter.event().reconnectTime(1000).data(JSON.toJSONString(newResultList)));
        return sseEmitter;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 2000)
    public void run() {
        try {
            String result = TraceResultStorage.pollNewResult();
            if (!StringUtils.hasText(result)) {
                return;
            }
            ResultSessionManger.broadcastSend(result);
        } catch (Exception e) {
            log.error("推送结果失败", e);
        }
    }

}
