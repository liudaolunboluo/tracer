package com.liudaolunboluo.view;

import com.liudaolunboluo.trace.ResultModel;

/**
 * Command result view for telnet term/tty.
 * Note: Result view is a reusable and stateless instance
 *
 * @author gongdewei 2020/3/27
 */
public abstract class ResultView<T extends ResultModel> {

    /**
     * formatted printing data to term/tty
     *
     * @param process
     */
    public abstract void draw(T result);

    /**
     * write str and append a new line
     *
     * @param process
     * @param str
     */
    protected void writeln(String str) {

    }
}
