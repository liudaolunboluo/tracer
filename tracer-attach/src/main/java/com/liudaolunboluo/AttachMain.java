package com.liudaolunboluo;

import com.liudaolunboluo.agent.TracerAttacher;
import com.liudaolunboluo.param.TracerAttachParam;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: AttachMain
 * @Description: attach主入口
 * @date 2023/2/3
 */
public class AttachMain {

    public static void main(String[] args) {
        TracerAttacher tracerAttacher = new TracerAttacher();
        tracerAttacher.attach(TracerAttachParam.builder().targetClassName(args[0]).targetMethodName(args[1]).pid(args[2]).build());
    }
}



