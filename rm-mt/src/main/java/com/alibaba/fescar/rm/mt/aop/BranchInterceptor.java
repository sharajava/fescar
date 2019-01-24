package com.alibaba.fescar.rm.mt.aop;

import com.alibaba.fescar.rm.mt.*;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class BranchInterceptor implements MethodInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchInterceptor.class);

    private ManualBranchManager branchManager = ManualBranchManager.get();

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        final Try anno = getAnnotation(methodInvocation.getMethod());
        if (anno == null) {
            return methodInvocation.proceed();
        }

        final Object[] rt = {null};
        branchManager.execute(new DefaultBranchAdaptor(anno.name(), new PrepareCallback() {
            @Override
            public void prepare() throws Throwable {
                Object result = methodInvocation.proceed();
                rt[0] = result;
                if (anno.bindResult()) {
                    BranchContext.bindApplicationData(result.toString());
                }
            }
        }));
        return rt[0];
    }



    private Try getAnnotation(Method method) {
        if (method == null) {
            return null;
        }
        return method.getAnnotation(Try.class);
    }
}
