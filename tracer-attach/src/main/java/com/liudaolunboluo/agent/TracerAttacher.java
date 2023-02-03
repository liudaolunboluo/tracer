package com.liudaolunboluo.agent;

import com.liudaolunboluo.param.TracerAttachParam;
import com.liudaolunboluo.common.CommonUtil;

import com.sun.tools.attach.VirtualMachine;
import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.net.URL;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TracerAgent
 * @Description: attach类
 * @date 2023/2/1
 */
@Slf4j
public class TracerAttacher {

    private static final String AGENT_ZIP = "tracer-agent.zip";

    private static final String AGENT_JAR = "tracer-agent.jar";

    public void attach(TracerAttachParam param) {
        URL coreJarUrl = this.getClass().getClassLoader().getResource(AGENT_ZIP);
        if (coreJarUrl == null) {
            log.warn("{} is missing!", AGENT_ZIP);
            return;
        }
        try {
            File tempDir = CommonUtil.createTempDir();
            ZipUtil.unpack(coreJarUrl.openStream(), tempDir);
            String agentHome = tempDir.getAbsolutePath();
            File agentJarFile = new File(agentHome, AGENT_JAR);
            VirtualMachine vm = VirtualMachine.attach(param.getPid());
            vm.loadAgent(agentJarFile.getAbsolutePath(), param.getTargetClassName() + "#" + param.getTargetMethodName());
            vm.detach();
        } catch (Exception e) {
            log.warn("attach tracer fail", e);
        }
    }
}