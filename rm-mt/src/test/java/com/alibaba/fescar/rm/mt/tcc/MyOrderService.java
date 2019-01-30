package com.alibaba.fescar.rm.mt.tcc;

import com.alibaba.fescar.rm.mt.aop.Cancel;
import com.alibaba.fescar.rm.mt.aop.Confirm;
import com.alibaba.fescar.rm.mt.aop.Try;

public interface MyOrderService {

    @Try(name = "order")
    void preCreateOrder(String x, int y, boolean z) throws MyBusinessException;

    @Confirm(name = "order")
    void doSubmitOrder(String x, int y, boolean z) throws MyBusinessException;

    @Cancel(name = "order")
    void doAbortOrder(String x, int y, boolean z) throws MyBusinessException;

}
