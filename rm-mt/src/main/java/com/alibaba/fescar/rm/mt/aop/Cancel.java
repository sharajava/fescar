package com.alibaba.fescar.rm.mt.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Cancel {

    String name();
}
