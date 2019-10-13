package com.jd.blockchain.ump.model.config;


import com.jd.blockchain.ump.model.MasterAddr;
import com.jd.blockchain.ump.model.PartiNode;
import com.jd.blockchain.ump.model.UmpConstant;
import com.jd.blockchain.ump.model.state.LedgerMasterInstall;
import com.jd.blockchain.ump.model.state.LedgerPeerInstall;

import java.io.File;

/**
 * Peer本地配置信息
 */
public class PeerLocalConfig extends PeerSharedConfig {

    private String peerPath;

    private String consensusConf = "bftsmart.default.config"; // 默认为bftsmart配置

    private String privKey;

    private String encodePwd;

    private String dbName;

    private MasterConfig masterConfig;

    public String bindingOutPath() {
        return peerPath + UmpConstant.PATH_CONFIG;
    }

    public String localConfPath() {
        return peerPath + UmpConstant.PATH_LOCAL_CONFIG;
    }

    public String ledgerInitConfPath() {
        return peerPath + UmpConstant.PATH_LEDGER_INIT_CONFIG;
    }

    public String consensusConfPath() {
        return peerPath + UmpConstant.PATH_CONFIG_INIT + File.separator + consensusConf;
    }

    public String libsDirectory() {
        return peerPath + UmpConstant.PATH_LIBS;
    }

    public String getPeerPath() {
        return peerPath;
    }

    public void setPeerPath(String peerPath) {
        this.peerPath = peerPath;
    }

    public String getConsensusConf() {
        return consensusConf;
    }

    public void setConsensusConf(String consensusConf) {
        this.consensusConf = consensusConf;
    }

    public String getPrivKey() {
        return privKey;
    }

    public void setPrivKey(String privKey) {
        this.privKey = privKey;
    }

    public String getEncodePwd() {
        return encodePwd;
    }

    public void setEncodePwd(String encodePwd) {
        this.encodePwd = encodePwd;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public MasterConfig getMasterConfig() {
        return masterConfig;
    }

    public void setMasterConfig(MasterConfig masterConfig) {
        this.masterConfig = masterConfig;
    }

    public synchronized PartiNode toPartiNode(int nodeId) {
        if (this.partiNode != null) {
            return partiNode;
        }
        partiNode = new PartiNode();
        partiNode.setId(nodeId);
        partiNode.setName(name);
        partiNode.setInitHost(initAddr);
        partiNode.setInitPort(initPort);
        partiNode.setPubKey(pubKey);
        partiNode.setSecure(false);
        return partiNode;
    }

    public LedgerPeerInstall toLedgerPeerInstall(int totalNodeSize) {
        return new LedgerPeerInstall(name, sharedKey, peerPath, totalNodeSize);
    }

    public LedgerMasterInstall.PeerInstall toPeerInstall() {
        return new LedgerMasterInstall.PeerInstall(name, pubKey, initAddr, initPort, consensusNode, consensusProvider);
    }

    public void verify() {

        // 主要校验dbName地址是否存在
        String dbPath = peerPath + File.separator + dbName;
        File dbDir = new File(dbPath);
        if (dbDir.exists()) {
            throw new IllegalStateException(String.format("DB name = %s, path = %s is exist !!!", dbName,  dbPath));
        }

        // 其他配置信息是否正确
        if (masterConfig == null) {
            // Master不能为空
            throw new IllegalStateException("Master Config can not be NULL !!!");
        }
        if (masterConfig.isMaster()) {
            // 账本名字及NodeSize不能为空
            if (masterConfig.getLedgerName() == null || masterConfig.getLedgerName().length() == 0) {
                throw new IllegalStateException("Master 's LedgerName can not be empty !!!");
            }
            if (masterConfig.getNodeSize() == 0) {
                throw new IllegalStateException("Master 's NodeSize can not be Zero !!!");
            }
        } else {
            // 普通Peer需要检查Master的IP地址及端口
            if (masterConfig.getMasterAddr() == null || masterConfig.getMasterAddr().length() == 0) {
                throw new IllegalStateException("Master 's IP Address can not be empty !!!");
            }

            if (masterConfig.getMasterPort() == 0) {
                throw new IllegalStateException("Master 's Port must be Set !!!");
            }
        }
    }

    public boolean master() {
        return masterConfig.isMaster();
    }

    public MasterAddr masterAddr() {
        return masterConfig.toMasterAddr();
    }
}
