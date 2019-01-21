package com.alibaba.fescar.rm;

import com.alibaba.fescar.core.rpc.netty.RmRpcClient;

public class RMClientMT {

    public static void init(String applicationId, String transactionServiceGroup) {
        RmRpcClient rmRpcClient = RmRpcClient.getInstance(applicationId, transactionServiceGroup);
        rmRpcClient.init();
    }
}
