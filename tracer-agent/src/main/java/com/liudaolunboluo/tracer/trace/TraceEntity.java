package com.liudaolunboluo.tracer.trace;

import com.liudaolunboluo.tracer.common.ThreadUtil;

/**
 * 用于在ThreadLocal中传递的实体
 *
 * @author ralf0131 2017-01-05 14:05.
 */
public class TraceEntity {

    public TraceTree tree;
    public int deep;

    public TraceEntity(ClassLoader loader) {
        this.tree = createTraceTree(loader);
        this.deep = 0;
    }

    private TraceTree createTraceTree(ClassLoader loader) {
         return new TraceTree(ThreadUtil.getThreadNode(loader, Thread.currentThread()));
    }

    public TraceModel getModel() {
        tree.trim();
        return new TraceModel(tree.getRoot(), tree.getNodeCount());
    }
}
