package com.alibaba.fescar.rm.mt.aop;

import com.alibaba.fescar.rm.mt.ManualBranchManager;
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


        // TODO

        return null;
    }



    private Try getAnnotation(Method method) {
        if (method == null) {
            return null;
        }
        return method.getAnnotation(Try.class);
    }
}
