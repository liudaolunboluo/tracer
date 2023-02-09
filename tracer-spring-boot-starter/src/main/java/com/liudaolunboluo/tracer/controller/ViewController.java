package com.liudaolunboluo.tracer.controller;

import com.alibaba.fastjson.JSON;
import com.liudaolunboluo.tracer.common.TraceResultStorage;
import com.liudaolunboluo.tracer.core.ResultSessionManger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
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
@RequestMapping(path = "/tracer/view")
@Slf4j
@ConditionalOnProperty(name = "spring.tracer.enabled", matchIfMissing = false)
@EnableScheduling
public class ViewController {

    @Value("${server.servlet.context-path:''}")
    private String baseUrl;

    @GetMapping(path = "")
    public ModelAndView index() {
        return new ModelAndView("redirect:/view.html？baseUrl=" + baseUrl);
    }

    /**
     * 开始链接的时候调用，就是打开网页的时候调用
     */
    @GetMapping(path = "/init")
    public SseEmitter init(String id) {
        SseEmitter sseEmitter = ResultSessionManger.register(id);
        List<String> allResults = TraceResultStorage.getAllResults();
        if (allResults == null || allResults.isEmpty()) {
            return sseEmitter;
        }
        allResults.forEach(result -> {
            try {
                sseEmitter.send(result.replace("\n", "<br>"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return sseEmitter;
    }

}
