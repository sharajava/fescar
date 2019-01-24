package com.alibaba.fescar.rm.mt.tcc;

import com.alibaba.fescar.rm.mt.aop.Cancel;
import com.alibaba.fescar.rm.mt.aop.Confirm;
import com.alibaba.fescar.rm.mt.aop.Try;

public interface MyOrderService {

    @Try(name = "order", bindResult = true)
    String createOrder(String x, int y, boolean z) throws MyBusinessException;

    @Confirm(name = "order")
    void commitOrder(String orderId) throws MyBusinessException;

    @Cancel(name = "order")
    void rollbackOrder(String orderId) throws MyBusinessException;


}
