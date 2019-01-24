package com.alibaba.fescar.rm.mt;

import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.model.BranchStatus;
import com.alibaba.fescar.core.model.Resource;

public interface PhaseOne extends Resource {

    String prepare(String xid, long branchId) throws TransactionException;

}
