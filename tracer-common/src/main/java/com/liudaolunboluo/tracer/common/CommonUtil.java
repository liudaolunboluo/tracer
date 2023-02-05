package com.liudaolunboluo.tracer.common;

import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: CommonUtil
 * @Description: 一些公共的工具方法
 * @date 2023/2/3
 */
@UtilityClass
public class CommonUtil {

    private static final int TEMP_DIR_ATTEMPTS = 10000;

    public File createTempDir() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = "tracer-" + System.currentTimeMillis() + "-";
        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried " + baseName + "0 to " + baseName
                + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }
}
