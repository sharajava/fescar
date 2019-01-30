package com.alibaba.fescar.rm.mt.aop;

public @interface TCC {

    String name();

    String confirm();

    String cancel();
}
