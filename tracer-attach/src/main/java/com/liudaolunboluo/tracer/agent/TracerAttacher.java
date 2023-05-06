package com.liudaolunboluo.tracer.agent;

import com.alibaba.fastjson.JSON;
import com.liudaolunboluo.tracer.param.TracerAttachParam;
import com.liudaolunboluo.tracer.common.CommonUtil;

import com.sun.tools.attach.VirtualMachine;
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
public class TracerAttacher {

    private static final String AGENT_ZIP = "tracer-agent.zip";

    private static final String AGENT_JAR = "tracer-agent.jar";

    public void attach(TracerAttachParam param) {
        URL coreJarUrl = this.getClass().getClassLoader().getResource(AGENT_ZIP);
        if (coreJarUrl == null) {
            System.out.println(AGENT_ZIP + " is missing!");
            return;
        }
        try {
            File tempDir = CommonUtil.createTempDir();
            ZipUtil.unpack(coreJarUrl.openStream(), tempDir);
            String agentHome = tempDir.getAbsolutePath();
            File agentJarFile = new File(agentHome, AGENT_JAR);
            VirtualMachine vm = VirtualMachine.attach(param.getPid());
            vm.loadAgent(agentJarFile.getAbsolutePath(), JSON.toJSONString(param.getTargetClassList()) + " " + param.getIsSkipJdk());
            vm.detach();
            System.out.println("attach " + param.getPid() + " success!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("attach tracer fail ,error:" + e);
        }
    }
}
