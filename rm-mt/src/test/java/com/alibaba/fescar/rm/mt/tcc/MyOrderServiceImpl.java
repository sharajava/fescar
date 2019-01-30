package com.alibaba.fescar.rm.mt.tcc;

import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.model.BranchStatus;
import com.alibaba.fescar.rm.mt.BranchUtils;
import com.alibaba.fescar.rm.mt.ManualBranchManager;
import com.alibaba.fescar.rm.mt.PhaseTwo;
import com.alibaba.fescar.rm.mt.api.ManualBranchHelper;

public class MyOrderServiceImpl implements MyOrderService, PhaseTwo {


    public void init() {
        // This could be done with Spring bean initializing mechanism.
        ManualBranchManager.get().registerResource(this);
    }

    @Override
    public void preCreateOrder(String x, int y, boolean z) throws MyBusinessException {
        Long branchId = null;
        try {
            branchId = ManualBranchHelper.branchRegister(this, BranchUtils.encode(new Object[] {x, y, z}));
        } catch (TransactionException e) {
            throw new MyBusinessException(e);
        }

        // Do your business


        // Report status, it's optional.
        try {
            ManualBranchHelper.branchReport(branchId, BranchStatus.PhaseOne_Done, null);
        } catch (TransactionException e) {
            throw new MyBusinessException(e);
        }


    }

    public void commit(String xid, long branchId, String branchKey, String applicationData) throws Throwable {
        Object[] args = BranchUtils.decode(branchKey);
        if (args == null || args.length != 3) {
            throw new RuntimeException("IllegalBranchKey: " + branchKey);
        }

        String x = (String)args[0];
        int y = (int)args[1];
        boolean z = (boolean)args[2];

        doSubmitOrder(x, y, z);
    }

    @Override
    public void doSubmitOrder(String x, int y, boolean z) {

    }

    public void rollback(String xid, long branchId, String branchKey, String applicationData) throws Throwable {
        Object[] args = BranchUtils.decode(branchKey);
        if (args == null || args.length != 3) {
            throw new RuntimeException("IllegalBranchKey: " + branchKey);
        }

        String x = (String)args[0];
        int y = (int)args[1];
        boolean z = (boolean)args[2];

        doAbortOrder(x, y, z);
    }

    @Override
    public void doAbortOrder(String x, int y, boolean z) {

    }

    @Override
    public String getResourceGroupId() {
        return "DEFAULT";
    }

    @Override
    public String getResourceId() {
        return "xxxxxxxxxxxxxxxxxxx";
    }
}
