package com.jd.blockchain.ump.model;

import com.jd.blockchain.ump.model.config.LedgerInitConfig;
import com.jd.blockchain.ump.model.config.MasterConfig;
import com.jd.blockchain.ump.model.config.PeerLocalConfig;
import com.jd.blockchain.ump.model.config.PeerSharedConfig;
import com.jd.blockchain.ump.model.state.LedgerMasterInstall;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PeerSharedConfigs {

    /**
     * 默认的一次邀请码最长等待时间，单位分钟，默认30分钟
     */
    private static final int MAX_WAIT_MINUTE = 30;

    private CountDownLatch latch = null;

    private Lock lock = new ReentrantLock();

    private Lock waitLock = new ReentrantLock();

    private Condition sizeCondition = waitLock.newCondition();

    private List<PeerLocalConfig> sharedConfigs = new ArrayList<>();

    private int waitNodeSize;

    private String consensusProvider;

    private String sharedKey;

    private String ledgerName;

    private LedgerInitConfig ledgerInitConfig;

    public synchronized PeerSharedConfigs addConfig(PeerLocalConfig sharedConfig) {

        // 判断内容是否存在重复
        for (PeerSharedConfig innerSharedConfig : sharedConfigs) {
            if (innerSharedConfig.getName().equals(sharedConfig.getName())
                || innerSharedConfig.getPubKey().equals(sharedConfig.getPubKey())) {
                return null;
            }
        }

        if (sharedConfig.getMasterConfig().isMaster()) {
            initDataByMaster(sharedConfig);
        }

        sharedConfigs.add(sharedConfig);

        if (latch != null) {
            // 不管是Master还是普通用户都需要-1
            latch.countDown();
        }
        return this;
    }

    /**
     * 由Master节点传入的信息对数据进行初始化
     *
     * @param sharedConfig
     */
    private void initDataByMaster(PeerLocalConfig sharedConfig) {

        MasterConfig masterConfig = sharedConfig.getMasterConfig();

        // master需要对数据进行组织
        if (latch == null) {
            latch = new CountDownLatch(masterConfig.getNodeSize() - sharedConfigs.size());
        }
        if (consensusProvider == null) {
            consensusProvider = sharedConfig.getConsensusProvider();
        }
        if (sharedKey == null) {
            sharedKey = sharedConfig.getSharedKey();
        }
        if (ledgerName == null) {
            ledgerName = masterConfig.getLedgerName();
        }
        waitNodeSize = masterConfig.getNodeSize();
    }

    /**
     * 线程等待
     *     一直处于等待状态(30分钟），直到有线程调用single方法
     *
     */
    public void await() {
        waitLock.lock();
        try {
            sizeCondition.await(MAX_WAIT_MINUTE, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            waitLock.unlock();
        }
    }

    /**
     * 通知其他线程等待状态结束
     *
     */
    public void single() {
        waitLock.lock();
        try {
            sizeCondition.signalAll();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            waitLock.unlock();
        }
    }

    /**
     * Master线程调用，等待数据满足后通知其他线程
     *
     */
    public void waitAndNotify() {
        if (this.latch == null) {
            throw new IllegalStateException("Please init MasterConfig first !!!");
        }
        try {
            latch.await(MAX_WAIT_MINUTE, TimeUnit.MINUTES);
            single(); // 通知其他线程释放
        } catch (Exception e) {
            if (sharedConfigs.size() >= waitNodeSize) {
                // 成功
                single();
            }
        }
    }

    public synchronized LedgerInitConfig ledgerInitConfig(String seed, String createTime,
                                                          List<String> securityConfigs, List<String> partiRoleConfigs) {
        if (ledgerInitConfig != null) {
            return ledgerInitConfig;
        }

        // 处理该ledgerInitConfig
        ledgerInitConfig = new LedgerInitConfig(seed, ledgerName, createTime, consensusProvider, waitNodeSize,
                securityConfigs, partiRoleConfigs);

        // 添加参与方
        for (int i = 0; i < sharedConfigs.size(); i++) {
            PeerLocalConfig sharedConfig = sharedConfigs.get(i);
            ledgerInitConfig.addPartiNode(sharedConfig.toPartiNode(i));
        }

        return ledgerInitConfig;
    }

    public String getConsensusProvider() {
        return consensusProvider;
    }

    public void setConsensusProvider(String consensusProvider) {
        this.consensusProvider = consensusProvider;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public Lock getLock() {
        return lock;
    }

    public String getLedgerName() {
        return ledgerName;
    }

    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }

    public List<PeerLocalConfig> getSharedConfigs() {
        return sharedConfigs;
    }

    public void setSharedConfigs(List<PeerLocalConfig> sharedConfigs) {
        this.sharedConfigs = sharedConfigs;
    }

    public LedgerInitConfig getLedgerInitConfig() {
        return ledgerInitConfig;
    }

    public void setLedgerInitConfig(LedgerInitConfig ledgerInitConfig) {
        this.ledgerInitConfig = ledgerInitConfig;
    }

    public LedgerMasterInstall toLedgerMasterInstall() {

        // String ledgerKey, String sharedKey, int totalNodeSize
        LedgerMasterInstall masterInstall = new LedgerMasterInstall(
                ledgerInitConfig.ledgerKey(), sharedConfigs.size())
                .initCreateTime(ledgerInitConfig.getCreateTime());

        for (PeerLocalConfig sharedConfig : sharedConfigs) {

            masterInstall.add(sharedConfig.toPeerInstall());
        }

        return masterInstall;
    }
}
