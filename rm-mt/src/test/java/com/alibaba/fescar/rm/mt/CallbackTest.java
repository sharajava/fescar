package com.alibaba.fescar.rm.mt;

import com.alibaba.fescar.core.context.RootContext;
import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.model.BranchStatus;
import com.alibaba.fescar.core.model.BranchType;
import com.alibaba.fescar.core.model.Resource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallbackTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackTest.class);

    private static String xid = "127.0.0.1:8091:1548312935803";

    private static String resourceId = "my_app.my_tx_group.order";

    private static DefaultBranchAdaptor branch;

    private static MyBusinessTransactionAdaptor adaptor;

    private static List<Long> registeredBranchIds = new ArrayList<>();

    private static Map<Long, String> bindApplicationDatas = new HashMap<>();

    private static Map<String, Resource> managedResources = new ConcurrentHashMap<>();

    private static boolean phaseOneDone = false;

    @BeforeClass
    public static void init() {

        ManualBranchManager.set(new ManualBranchManager() {
            @Override
            public Long branchRegister(BranchType branchType, String resourceId, String clientId, String xid,
                                       String lockKeys) throws TransactionException {
                Long branchId = System.currentTimeMillis();
                registeredBranchIds.add(branchId);
                return branchId;
            }

            @Override
            public void branchReport(String xid, long branchId, BranchStatus status, String applicationData)
                throws TransactionException {
                bindApplicationDatas.put(branchId, applicationData);
            }

            @Override
            public void registerResource(Resource resource) {
                managedResources.put(resource.getResourceId(), resource);
            }

            @Override
            public BranchStatus branchCommit(String xid, long branchId, String resourceId, String applicationData)
                throws TransactionException {
                PhaseTwo branchTransaction = (PhaseTwo)managedResources.get(resourceId);
                return branchTransaction.commit(xid, branchId, applicationData);
            }

            @Override
            public BranchStatus branchRollback(String xid, long branchId, String resourceId, String applicationData)
                throws TransactionException {
                PhaseTwo branchTransaction = (PhaseTwo)managedResources.get(resourceId);
                return branchTransaction.rollback(xid, branchId, applicationData);
            }
        });

        branch = new DefaultBranchAdaptor(resourceId, new MyBusinessTransactionAdaptor());
        ManualBranchManager.get().registerResource(branch);
    }

    @AfterClass
    public static void cleanup() {
        branch = null;
        registeredBranchIds.clear();
        bindApplicationDatas.clear();
        managedResources.clear();
        phaseOneDone = false;
    }

    @Test
    public void testBasic() throws Throwable {
        // Begin a global transaction
        RootContext.bind(xid);

        ManualBranchManager.get().execute(branch);

        // End the global transaction
        RootContext.unbind();
        phaseOneDone = true;

    }

    @Test
    public void testCommit() throws Throwable {
        if (!phaseOneDone) {
            testBasic();
        }
        Long branchId = registeredBranchIds.get(0);
        ManualBranchManager.get().branchCommit(xid, branchId, resourceId, bindApplicationDatas.get(branchId));

    }

    private static class MyBusinessTransactionAdaptor implements PrepareCallback, CommitCallback, RollbackCallback {

        @Override
        public void prepare() {
            String orderId = preCreateOrder();
            bindOrderId(BranchContext.getXID(), BranchContext.getBranchId(), orderId);
        }

        @Override
        public void commit() {
            String orderId = findOrderId(BranchContext.getXID(), BranchContext.getBranchId());
            doCreateOrder(orderId);
            unbindOrderId(BranchContext.getXID(), BranchContext.getBranchId());
        }

        @Override
        public void rollback() {
            String orderId = findOrderId(BranchContext.getXID(), BranchContext.getBranchId());
            doDeleteOrder(orderId);
            unbindOrderId(BranchContext.getXID(), BranchContext.getBranchId());
        }

        private Map<String, String> binds = new HashMap<>();

        private void bindOrderId(String xid, Long branchId, String orderId) {
            binds.put(xid + "-" + branchId, orderId);
            LOGGER.info("Persistent [" + xid + "-" + branchId + "] -> Order[" + orderId + "]");
        }
        private void unbindOrderId(String xid, Long branchId) {
            // Delete xid + branchId -> orderId
            String orderId = binds.remove(xid + "-" + branchId);
            LOGGER.info("Delete [" + xid + "-" + branchId + "] -> Order[" + orderId + "]");
        }

        private void doCreateOrder(String orderId) {
            LOGGER.info("Order [" + orderId + "] is finally created.");
        }

        private void doDeleteOrder(String orderId) {
            LOGGER.info("Order [" + orderId + "] is deleted.");
        }

        private String preCreateOrder() {
            String orderId = String.valueOf(System.currentTimeMillis());
            LOGGER.info("Order [" + orderId + "] is pre-created.");
            return orderId;
        }

        private String findOrderId(String xid, Long branchId) {
            String orderId = binds.get(xid + "-" + branchId);
            LOGGER.info("Found [" + xid + "-" + branchId + "] -> Order[" + orderId + "]");
            return orderId;
        }
    }


}
