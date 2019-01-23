package com.alibaba.fescar.rm.mt;

public class BranchContext {

    private String xid;

    private Long branchId;

    public String getXid() {
        return xid;
    }

    void setXid(String xid) {
        this.xid = xid;
    }

    public Long getBranchId() {
        return branchId;
    }

    void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

}
