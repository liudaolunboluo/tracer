package com.liudaolunboluo.tracer.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TargetClass
 * @Description: 目标类
 * @date 2023/2/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetClass {

    /**
     * 类的全路径，包含包名
     */
    private String fullClassName;

    /**
     * 目标方法
     */
    private List<TargetMethod> targetMethodList;
}
