package com.liudaolunboluo.tracer;

import com.alibaba.fastjson.JSON;
import com.liudaolunboluo.tracer.agent.TracerAttacher;
import com.liudaolunboluo.tracer.param.TracerAttachParam;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: AttachMain
 * @Description: attach主入口
 * @date 2023/2/3
 */
public class AttachMain {

    public static void main(String[] args) {
        //hack 整个attach是在java -jar下完成的，所以打日志的话可能会造成主程序的日志打印到attach日志里来，所以就暂时用system来打印信息然后launcher中用日志显示，后面想办法优化
        System.out.println("begin attach!");
        if (args.length == 0) {
            System.out.println("no attach config!");
            return;
        }
        TracerAttacher tracerAttacher = new TracerAttacher();
        tracerAttacher.attach(JSON.parseObject(args[0], TracerAttachParam.class));
    }
}



