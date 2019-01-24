package com.alibaba.fescar.rm.mt;

import org.junit.Assert;
import org.junit.Test;

public class BranchTransactionContextTest {

    @Test
    public void testBranchContext() {
        String xid = "xxx";
        Long branchId = 12987L;
        String applicationData = "app_data";


        BranchContext.bindXID(xid);
        BranchContext.bindBranchId(branchId);
        BranchContext.bindApplicationData("app_data");

        Assert.assertEquals(xid, BranchContext.getXID());
        Assert.assertEquals(branchId,  BranchContext.unbindBranchId());
        Assert.assertNull(BranchContext.getBranchId());
        Assert.assertEquals(applicationData, BranchContext.getApplicationData());

    }
}
