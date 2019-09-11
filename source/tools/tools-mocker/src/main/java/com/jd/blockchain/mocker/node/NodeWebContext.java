package com.jd.blockchain.mocker.node;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.core.LedgerInitDecision;
import com.jd.blockchain.ledger.core.LedgerInitProposal;
import com.jd.blockchain.ledger.core.LedgerManager;
import com.jd.blockchain.ledger.core.LedgerQuery;
import com.jd.blockchain.mocker.config.LedgerInitWebConfiguration;
import com.jd.blockchain.storage.service.DbConnection;
import com.jd.blockchain.storage.service.impl.composite.CompositeConnectionFactory;
import com.jd.blockchain.tools.initializer.DBConnectionConfig;
import com.jd.blockchain.tools.initializer.LedgerInitProcess;
import com.jd.blockchain.tools.initializer.Prompter;
import com.jd.blockchain.tools.initializer.web.LedgerInitializeWebController;
import com.jd.blockchain.utils.concurrent.ThreadInvoker;
import com.jd.blockchain.utils.net.NetworkAddress;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.CountDownLatch;

public class NodeWebContext {

    private NetworkAddress serverAddress;

    private DBConnectionConfig dbConnConfig;

    private volatile ConfigurableApplicationContext ctx;

    private volatile LedgerInitProcess initProcess;

    private volatile LedgerInitializeWebController controller;

    private volatile LedgerManager ledgerManager;

    private volatile CompositeConnectionFactory db;

    private int id;

    public int getId() {
        return controller.getId();
    }

    public TransactionContent getInitTxContent() {
        return controller.getInitTxContent();
    }

    public LedgerInitProposal getLocalPermission() {
        return controller.getLocalPermission();
    }

    public LedgerInitDecision getLocalDecision() {
        return controller.getLocalDecision();
    }

    public NodeWebContext(int id, NetworkAddress serverAddress) {
        this.id = id;
        this.serverAddress = serverAddress;
    }

    public LedgerQuery registLedger(HashDigest ledgerHash) {
        DbConnection conn = db.connect(dbConnConfig.getUri());
        LedgerQuery ledgerRepo = ledgerManager.register(ledgerHash, conn.getStorageService());
        return ledgerRepo;
    }

    public ThreadInvoker.AsyncCallback<HashDigest> startInit(PrivKey privKey, LedgerInitProperties setting,
                                                             DBConnectionConfig dbConnConfig, Prompter prompter, CountDownLatch quitLatch) {

        ThreadInvoker<HashDigest> invoker = new ThreadInvoker<HashDigest>() {
            @Override
            protected HashDigest invoke() throws Exception {
                doStartServer();

                NodeWebContext.this.dbConnConfig = dbConnConfig;
                HashDigest ledgerHash = NodeWebContext.this.initProcess.initialize(id, privKey, setting,
                        dbConnConfig, prompter);

                System.out.printf("ledgerHash = %s \r\n", ledgerHash.toBase58());

                quitLatch.countDown();
                return ledgerHash;
            }
        };

        return invoker.start();
    }

    public void doStartServer() {
        String argServerAddress = String.format("--server.address=%s", serverAddress.getHost());
        String argServerPort = String.format("--server.port=%s", serverAddress.getPort());
        String nodebug = "--debug=false";
        String[] innerArgs = { argServerAddress, argServerPort, nodebug };

        ctx = SpringApplication.run(LedgerInitWebConfiguration.class, innerArgs);

        ctx.setId("Node-" + id);
        controller = ctx.getBean(LedgerInitializeWebController.class);
        ledgerManager = ctx.getBean(LedgerManager.class);
        db = ctx.getBean(CompositeConnectionFactory.class);
        initProcess = ctx.getBean(LedgerInitProcess.class);
    }
}
