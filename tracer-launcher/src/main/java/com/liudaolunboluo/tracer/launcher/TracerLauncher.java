package com.liudaolunboluo.tracer.launcher;

import com.alibaba.fastjson.JSON;
import com.liudaolunboluo.tracer.callback.TraceCallback;
import com.liudaolunboluo.tracer.callback.TracerCallbackManger;
import com.liudaolunboluo.tracer.param.TracerAttachParam;
import com.liudaolunboluo.tracer.common.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TracerLauncher
 * @Description: tracer启动类
 * @date 2023/2/3
 */
@Slf4j
public class TracerLauncher {

    private static final String ATTACH_ZIP = "tracer-attach.zip";

    private static final String ATTACH_JAR = "tracer-attach.jar";

    /**
     * 启动tracer
     *
     * @param param:attach参数
     * @param callbackList:回调类实例集合
     * @author zhangyunfan
     * @date 2023/2/8
     */
    public void launch(TracerAttachParam param, List<TraceCallback> callbackList) {
        if (callbackList != null && !callbackList.isEmpty()) {
            callbackList.forEach(TracerCallbackManger::registerCallback);
        }
        URL coreJarUrl = this.getClass().getClassLoader().getResource(ATTACH_ZIP);
        if (coreJarUrl == null) {
            log.warn("{} is missing!", ATTACH_ZIP);
            return;
        }
        try {
            File tempDir = CommonUtil.createTempDir();
            ZipUtil.unpack(coreJarUrl.openStream(), tempDir);
            String agentHome = tempDir.getAbsolutePath();
            File attachJar = new File(agentHome, ATTACH_JAR);
            //java -jar tracer-attach.jar configJson
            Process process = Runtime.getRuntime().exec("java -jar " + attachJar.getAbsolutePath() + " " + JSON.toJSONString(param));
            printResults(process);
        } catch (Exception e) {
            log.warn("launch tracer-attach fail", e);
        }

    }

    public static void printResults(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        }
    }

}
