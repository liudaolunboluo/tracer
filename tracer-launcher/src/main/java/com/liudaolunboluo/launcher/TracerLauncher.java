package com.liudaolunboluo.launcher;

import com.liudaolunboluo.param.TracerAttachParam;
import com.liudaolunboluo.common.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.zip.ZipUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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

    public void launcher(TracerAttachParam param) {
        URL coreJarUrl = this.getClass().getClassLoader().getResource(ATTACH_ZIP);
        if (coreJarUrl == null) {
            log.warn("{} is missing!", ATTACH_ZIP);
            return;
        }
        try {
            File tempDir = CommonUtil.createTempDir();
            ZipUtil.unpack(coreJarUrl.openStream(), tempDir);
            String agentHome = tempDir.getAbsolutePath();
            File agentJarFile = new File(agentHome, ATTACH_JAR);
            Runtime.getRuntime()
                    .exec("java -jar " + agentJarFile.getAbsolutePath() + " " + param.getTargetClassName() + " " + param.getTargetMethodName() + " "
                            + param.getPid());
        } catch (Exception e) {
            log.warn("launch tracer fail", e);
        }

    }

}
