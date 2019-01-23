package com.alibaba.fescar.rm.mt;

import com.alibaba.fescar.core.context.RootContext;
import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.model.BranchStatus;
import com.alibaba.fescar.core.model.BranchType;

public class MTExecutor {

    public void execute(MTBranchManager branchManager) {
        String xid = RootContext.getXID();
        Long branchId = null;
        try {
            branchId = MTResourceManager.get().branchRegister(
                BranchType.MT,
                branchManager.getResourceId(),
                null,
                xid,
                null
            );

        } catch (TransactionException e) {
            e.printStackTrace();
        }

        String applicationData = null;
        try {
            applicationData = branchManager.prepare(xid, branchId);
        } catch (Exception e) {
            try {
                MTResourceManager.get().branchReport(xid, branchId, BranchStatus.PhaseOne_Failed, applicationData);
            } catch (TransactionException ex) {
                ex.printStackTrace();
            }
        }

        try {
            MTResourceManager.get().branchReport(xid, branchId, BranchStatus.PhaseOne_Done, applicationData);
        } catch (TransactionException e) {
            e.printStackTrace();
        }

    }
}
