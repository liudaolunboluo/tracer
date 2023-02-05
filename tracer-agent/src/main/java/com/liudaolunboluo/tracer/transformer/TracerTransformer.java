package com.liudaolunboluo.tracer.transformer;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.asm.location.Location;
import com.alibaba.bytekit.asm.location.LocationType;
import com.alibaba.bytekit.asm.location.MethodInsnNodeWare;
import com.alibaba.bytekit.asm.location.filter.GroupLocationFilter;
import com.alibaba.bytekit.asm.location.filter.InvokeCheckLocationFilter;
import com.alibaba.bytekit.asm.location.filter.InvokeContainLocationFilter;
import com.alibaba.bytekit.asm.location.filter.LocationFilter;
import com.alibaba.bytekit.utils.AsmOpUtils;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.deps.org.objectweb.asm.ClassReader;
import com.alibaba.deps.org.objectweb.asm.Opcodes;
import com.alibaba.deps.org.objectweb.asm.Type;
import com.alibaba.deps.org.objectweb.asm.tree.AbstractInsnNode;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodInsnNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import com.liudaolunboluo.tracer.listener.AdviceListener;
import com.liudaolunboluo.tracer.listener.AdviceListenerManager;
import com.liudaolunboluo.tracer.spy.SpyAPI;
import com.liudaolunboluo.tracer.spy.SpyImpl;
import com.liudaolunboluo.tracer.spy.SpyInterceptors;
import com.liudaolunboluo.tracer.common.ArthasCheckUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyunfan@fiture.com
 * @version 1.0
 * @ClassName: TestTransformer
 * @date 2023/1/20
 */
@Slf4j
@NoArgsConstructor
public class TracerTransformer implements ClassFileTransformer {

    private boolean skipJDKTrace = true;

    private String targetClassName;

    private String targetMethodName;

    private AdviceListener listener;

    private static SpyImpl spyImpl = new SpyImpl();

    static {
        SpyAPI.setSpy(spyImpl);
    }

    public TracerTransformer(String targetClassName, String targetMethodName, boolean skipJDKTrace, AdviceListener listener) {
        this.skipJDKTrace = skipJDKTrace;
        this.targetClassName = targetClassName;
        this.targetMethodName = targetMethodName;
        this.listener = listener;
    }

    @Override
    public byte[] transform(ClassLoader inClassLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!className.equalsIgnoreCase(targetClassName.replace(".", "/"))) {
            return classfileBuffer;
        }
        try {
            // 检查classloader能否加载到 SpyAPI，如果不能，则放弃增强
            try {
                if (inClassLoader != null) {
                    inClassLoader.loadClass(SpyAPI.class.getName());
                }
            } catch (Throwable e) {
                log.error("无法加载SpyAPI", e);
                return null;
            }
            //保留原始类读取器以进行字节码优化，避免 JVM 元空间 OOM。
            ClassNode classNode = new ClassNode(Opcodes.ASM9);
            ClassReader classReader = AsmUtils.toClassNode(classfileBuffer, classNode);
            // remove JSR https://github.com/alibaba/arthas/issues/1304
            classNode = AsmUtils.removeJSRInstructions(classNode);
            // 生成增强字节码
            DefaultInterceptorClassParser defaultInterceptorClassParser = new DefaultInterceptorClassParser();
            final List<InterceptorProcessor> interceptorProcessors = new ArrayList<>();
            interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyInterceptor1.class));
            interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyInterceptor2.class));
            interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyInterceptor3.class));
            if (!this.skipJDKTrace) {
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceInterceptor1.class));
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceInterceptor2.class));
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceInterceptor3.class));
            } else {
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceExcludeJDKInterceptor1.class));
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceExcludeJDKInterceptor2.class));
                interceptorProcessors.addAll(defaultInterceptorClassParser.parse(SpyInterceptors.SpyTraceExcludeJDKInterceptor3.class));
            }
            List<MethodNode> matchedMethods = new ArrayList<>();
            for (MethodNode methodNode : classNode.methods) {
                if (!isIgnore(methodNode)) {
                    matchedMethods.add(methodNode);
                }
            }
            // https://github.com/alibaba/arthas/issues/1690
            if (AsmUtils.isEnhancerByCGLIB(className)) {
                for (MethodNode methodNode : matchedMethods) {
                    if (AsmUtils.isConstructor(methodNode)) {
                        AsmUtils.fixConstructorExceptionTable(methodNode);
                    }
                }
            }

            // 用于检查是否已插入了 spy函数，如果已有则不重复处理
            GroupLocationFilter groupLocationFilter = new GroupLocationFilter();

            LocationFilter enterFilter = new InvokeContainLocationFilter(Type.getInternalName(SpyAPI.class), "atEnter", LocationType.ENTER);
            LocationFilter existFilter = new InvokeContainLocationFilter(Type.getInternalName(SpyAPI.class), "atExit", LocationType.EXIT);
            LocationFilter exceptionFilter = new InvokeContainLocationFilter(Type.getInternalName(SpyAPI.class), "atExceptionExit",
                    LocationType.EXCEPTION_EXIT);

            groupLocationFilter.addFilter(enterFilter);
            groupLocationFilter.addFilter(existFilter);
            groupLocationFilter.addFilter(exceptionFilter);

            LocationFilter invokeBeforeFilter = new InvokeCheckLocationFilter(Type.getInternalName(SpyAPI.class), "atBeforeInvoke",
                    LocationType.INVOKE);
            LocationFilter invokeAfterFilter = new InvokeCheckLocationFilter(Type.getInternalName(SpyAPI.class), "atInvokeException",
                    LocationType.INVOKE_COMPLETED);
            LocationFilter invokeExceptionFilter = new InvokeCheckLocationFilter(Type.getInternalName(SpyAPI.class), "atInvokeException",
                    LocationType.INVOKE_EXCEPTION_EXIT);
            groupLocationFilter.addFilter(invokeBeforeFilter);
            groupLocationFilter.addFilter(invokeAfterFilter);
            groupLocationFilter.addFilter(invokeExceptionFilter);

            for (MethodNode methodNode : matchedMethods) {
                if (AsmUtils.isNative(methodNode)) {
                    log.info("ignore native method: {}", AsmUtils.methodDeclaration(Type.getObjectType(classNode.name), methodNode));
                    continue;
                }
                // 先查找是否有 atBeforeInvoke 函数，如果有，则说明已经有trace了，则直接不再尝试增强，直接插入 listener
                if (AsmUtils.containsMethodInsnNode(methodNode, Type.getInternalName(SpyAPI.class), "atBeforeInvoke")) {
                    for (AbstractInsnNode insnNode = methodNode.instructions.getFirst(); insnNode != null; insnNode = insnNode.getNext()) {
                        if (insnNode instanceof MethodInsnNode) {
                            final MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                            if (this.skipJDKTrace) {
                                if (methodInsnNode.owner.startsWith("java/")) {
                                    continue;
                                }
                            }
                            // 原始类型的box类型相关的都跳过
                            if (AsmOpUtils.isBoxType(Type.getObjectType(methodInsnNode.owner))) {
                                continue;
                            }
                            AdviceListenerManager.registerTraceAdviceListener(inClassLoader, className, methodInsnNode.owner, methodInsnNode.name,
                                    methodInsnNode.desc, listener);
                        }
                    }
                } else {
                    MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode, groupLocationFilter);
                    for (InterceptorProcessor interceptor : interceptorProcessors) {
                        try {
                            List<Location> locations = interceptor.process(methodProcessor);
                            for (Location location : locations) {
                                if (location instanceof MethodInsnNodeWare) {
                                    MethodInsnNodeWare methodInsnNodeWare = (MethodInsnNodeWare) location;
                                    MethodInsnNode methodInsnNode = methodInsnNodeWare.methodInsnNode();
                                    AdviceListenerManager.registerTraceAdviceListener(inClassLoader, className, methodInsnNode.owner,
                                            methodInsnNode.name, methodInsnNode.desc, listener);
                                }
                            }

                        } catch (Throwable e) {
                            log.error("enhancer error, class: {}, method: {}, interceptor: {}", classNode.name, methodNode.name,
                                    interceptor.getClass().getName(), e);
                        }
                    }
                }
                AdviceListenerManager.registerAdviceListener(inClassLoader, className, methodNode.name, methodNode.desc, listener);
            }
            return AsmUtils.toBytes(classNode, inClassLoader, classReader);
        } catch (Throwable t) {
            log.error("方法:{}增强失败", targetClassName, t);
            return null;
        }

    }

    /**
     * 是否需要忽略
     */
    private boolean isIgnore(MethodNode methodNode) {
        return null == methodNode || isAbstract(methodNode.access) || !methodNode.name.equals(targetMethodName) || ArthasCheckUtils.isEquals(
                methodNode.name, "<clinit>");
    }

    /**
     * 是否抽象属性
     */
    private boolean isAbstract(int access) {
        return (Opcodes.ACC_ABSTRACT & access) == Opcodes.ACC_ABSTRACT;
    }
}