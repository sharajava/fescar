package com.alibaba.fescar.rm.mt.tcc;

public class MyOrderServiceImpl implements MyOrderService {
    @Override
    public String createOrder(String x, int y, boolean z) throws MyBusinessException {
        return null;
    }

    @Override
    public void commitOrder(String orderId) throws MyBusinessException {

    }

    @Override
    public void rollbackOrder(String orderId) throws MyBusinessException {

    }
}
