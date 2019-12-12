package com.jd.blockchain.ump.service;


import com.jd.blockchain.ump.dao.DBConnection;
import com.jd.blockchain.ump.dao.RocksDBConnection;
import com.jd.blockchain.ump.model.MasterAddr;
import com.jd.blockchain.ump.model.PeerSharedConfigs;
import com.jd.blockchain.ump.model.UmpConstant;
import com.jd.blockchain.ump.model.config.*;
import com.jd.blockchain.ump.model.state.*;
import com.jd.blockchain.ump.service.consensus.ConsensusService;
import com.jd.blockchain.ump.util.Base58Utils;
import com.jd.blockchain.ump.util.CommandUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

@Service
public class UmpServiceHandler implements UmpService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String SUCCESS = "SUCCESS";

    private static final String ROCKSDB_PROTOCOL = RocksDBConnection.ROCKSDB_PROTOCOL;

    private static final int DB_SUFFIX_LENGTH = 4;

    private static final Random DB_RANDOM = new Random();

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmssSSS");//yyyy-MM-dd HH:mm:ss为目标的样式

    private final Map<String, LedgerConfig> ledgerConfigs = new ConcurrentHashMap<>();

    private final Map<String, MasterConfig> masterConfigs = new ConcurrentHashMap<>();

    private final Map<String, PeerSharedConfigs> peerShareds = new ConcurrentHashMap<>();

    private final Map<String, LedgerConfig> ledgerConfigMemory = new ConcurrentHashMap<>();

    @Autowired
    private ConsensusService consensusService;

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private DBConnection dbConnection;

    @Autowired
    private UmpStateService umpStateService;

    @Autowired
    private SecurityService securityService;

    @Override
    public synchronized PeerSharedConfigs loadPeerSharedConfigs(PeerLocalConfig sharedConfig) {

        String sharedKey = sharedConfig.getSharedKey();

        PeerSharedConfigs peerSharedConfigs = peerShareds.get(sharedKey);

        if (peerSharedConfigs == null) {
            peerSharedConfigs = new PeerSharedConfigs();
            peerShareds.put(sharedKey, peerSharedConfigs);
        }

        return peerSharedConfigs.addConfig(sharedConfig);
    }

    @Override
    public LedgerConfig response(PeerSharedConfigs sharedConfigs, PeerLocalConfig localConfig) {
        try {
            // 对于Master和Peer处理方式不同
            if (localConfig.getMasterConfig().isMaster()) {

                // Master节点需要等待完成后通知其他线程
                sharedConfigs.waitAndNotify();
            } else {

                // 等待Master节点通知
                sharedConfigs.await();
            }

            // 此处需要防止并发
            final String sharedKey = sharedConfigs.getSharedKey();

            LedgerConfig savedLedgerConfig = ledgerConfigMemory.get(sharedKey);

            if (savedLedgerConfig != null) {
                return savedLedgerConfig;
            }

            // 获取当前对象锁（所有节点请求使用同一个对象）
            final Lock lock = sharedConfigs.getLock();

            lock.lock();

            try {
                // 执行到此表示获取到锁，此时需要判断是否有数据
                // Double Check !!!
                savedLedgerConfig = ledgerConfigMemory.get(sharedKey);

                if (savedLedgerConfig != null) {
                    return savedLedgerConfig;
                }

                // 校验
                verify(sharedConfigs);

                // 所有数据到达之后生成返回的应答
                LedgerInitConfig initConfig = sharedConfigs.ledgerInitConfig(
                        ledgerService.randomSeed(), ledgerService.currentCreateTime(),
                        securityService.securityConfigs(), securityService.participantRoleConfigs());

                // 生成共识文件
                String consensusConfig = consensusService.initConsensusConf(
                        sharedConfigs.getConsensusProvider(), sharedConfigs.getSharedConfigs());

                LedgerConfig ledgerConfig = new LedgerConfig(initConfig, consensusConfig);

                // 将本次LedgerKey信息写入数据库
                String ledgerKey = initConfig.ledgerKey();

                dbConnection.put(ledgerKey, ledgerConfig, LedgerConfig.class);

                // 将节点的Key信息写入数据库
                umpStateService.save(ledgerKey, sharedConfigKeys(ledgerKey, sharedConfigs));

                // 将本地生成数据的信息写入数据库
                LedgerMasterInstall masterInstall = sharedConfigs.toLedgerMasterInstall();

                umpStateService.save(masterInstall);

                // 将数据放入内存
                ledgerConfigMemory.put(sharedKey, ledgerConfig);

                return ledgerConfig;
            } finally {
                lock.unlock();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String save(MasterAddr masterAddr, LedgerConfig ledgerConfig, PeerLocalConfig localConfig) {

        String ledgerAndNodeKey = ledgerAndNodeKey(ledgerConfig, localConfig);

        ledgerConfigs.put(ledgerAndNodeKey, ledgerConfig);

        // 保存本次需要发送的Master地址
        masterConfigs.put(ledgerAndNodeKey, localConfig.getMasterConfig());

        // 保存所有的信息至本地
        umpStateService.save(ledgerAndNodeKey, localConfig);

        // 保存当前同步信息至数据库
        LedgerPeerInstall peerInstall = localConfig.toLedgerPeerInstall(ledgerConfig.getInitConfig().getNodeSize());

        // init相关配置信息
        peerInstall
                .initKey(ledgerConfig.getInitConfig().ledgerKey(), ledgerAndNodeKey)
                .initCreateTime(new Date())
                .initMasterAddr(masterAddr);

        // 写入数据库
        umpStateService.save(peerInstall);

        return ledgerAndNodeKey;
    }

    @Override
    public String ledgerAndNodeKey(LedgerConfig ledgerConfig, PeerSharedConfig sharedConfig) {

        return ledgerAndNodeKey(ledgerConfig.getInitConfig().ledgerKey(), sharedConfig);
    }

    @Override
    public PeerInstallSchedules install(LedgerIdentification identification, PeerLocalConfig localConfig, String ledgerAndNodeKey) {

        // 初始化Peer节点数据
        PeerInstallSchedules installSchedules = init(identification, localConfig, ledgerAndNodeKey);

        // Peer节点启动
        peerStart(localConfig.getPeerPath(), installSchedules);

        return installSchedules;
    }

    @Override
    public PeerInstallSchedules install(String ledgerAndNodeKey) {

        PeerLocalConfig localConfig = umpStateService.readConfig(ledgerAndNodeKey);

        if (localConfig != null) {

            // 获取LedgerIdentification
            LedgerIdentification identification = umpStateService.readIdentification(ledgerAndNodeKey);

            return install(identification, localConfig, ledgerAndNodeKey);
        }
        throw new IllegalStateException("Can not find LocalConfig from DataBase !!!");
    }

    @Override
    public PeerInstallSchedules init(LedgerIdentification identification, PeerLocalConfig localConfig, String ledgerAndNodeKey) {

        PeerInstallSchedules installSchedules = new PeerInstallSchedules(identification);

        MasterAddr masterAddr = loadMaster(localConfig);

        LedgerConfig ledgerConfig = ledgerConfigs.get(ledgerAndNodeKey);

        if (ledgerConfig == null || ledgerConfig.getInitConfig() == null) {
            saveInstallSchedule(installSchedules, masterAddr, "", ledgerAndNodeKey,
                    String.format("Ledger Key = [%s] can not find Ledger-Config !!!", ledgerAndNodeKey),
                    ScheduleState.LOAD_FAIL);
            throw new IllegalStateException(String.format("Ledger Key = [%s] can not find Ledger-Config !!!", ledgerAndNodeKey));
        }

        LedgerInitConfig initConfig = ledgerConfig.getInitConfig();

        String ledgerKey = initConfig.ledgerKey();

        List<String> localConfContents, ledgerInitContents;

        try {
            saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                    String.format("Find LedgerConfig from Memory for Key [%s] -> %s", ledgerAndNodeKey, SUCCESS),
                    ScheduleState.LOAD);

            // 首先获取当前节点的ID
            int nodeId = initConfig.nodeId(localConfig.getPubKey());

            // 生成local.conf文件内容
            localConfContents = localConfContents(localConfig, nodeId);

            saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                    String.format("Init Local.Conf's Content -> %s", SUCCESS),
                    ScheduleState.LOAD);

            // 生成LedgerInit内容
            ledgerInitContents = initConfig.toConfigChars(localConfig.consensusConfPath());

            saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                    String.format("Init Ledger.Init's Content -> %s", SUCCESS),
                    ScheduleState.LOAD);
        } catch (Exception e) {
            saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                    "Load Config's Content !!!",
                    ScheduleState.LOAD_FAIL);
            throw new IllegalStateException(e);
        }

        saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                "Load Config's Content !!!",
                ScheduleState.LOAD_SUCCESS);

        try {
            // 将该文件内容写入Local.Conf
            forceWrite(localConfContents, new File(localConfig.localConfPath()));

            saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                    String.format("Write And Backup File local.conf -> %s", SUCCESS),
                    ScheduleState.WRITE);

            // 将文件内容写入Ledger-Init
            forceWrite(ledgerInitContents, new File(localConfig.ledgerInitConfPath()));

            saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                    String.format("Write And Backup File ledger.init -> %s", SUCCESS),
                    ScheduleState.WRITE);

            // 将共识内容写入文件，例如bftsmart.conf
            String consensusFileName = writeConsensusContent(ledgerConfig.getConsensusConfig(),
                    new File(localConfig.consensusConfPath()));

            saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                    String.format("Write And Backup Consensus File %s -> %s", consensusFileName, SUCCESS),
                    ScheduleState.WRITE);

        } catch (Exception e) {
            saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                    "Write Config's Content to Config File !!!",
                    ScheduleState.WRITE_FAIL);
            throw new IllegalStateException(e);
        }

        saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                "Write Config's Content to Config File !!!",
                ScheduleState.WRITE_SUCCESS);

        // 账本初始化
        String ledgerHash = ledgerInit(localConfig.getPeerPath(), installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey);

        // 设置账本Hash
        installSchedules.setLedgerHash(ledgerHash);

        return installSchedules;
    }

    @Override
    public PeerInstallSchedules init(String ledgerAndNodeKey) {

        PeerLocalConfig localConfig = umpStateService.readConfig(ledgerAndNodeKey);

        if (localConfig != null) {

            // 获取LedgerIdentification
            LedgerIdentification identification = umpStateService.readIdentification(ledgerAndNodeKey);

            return init(identification, localConfig, ledgerAndNodeKey);
        }
        throw new IllegalStateException("Can not find LocalConfig from DataBase !!!");
    }


//    @Override
//    public PeerInstallSchedules startup(String ledgerAndNodeKey) {
//
//        PeerLocalConfig localConfig = umpStateService.readConfig(ledgerAndNodeKey);
//
//        if (localConfig != null) {
//
//            PeerInstallSchedules installSchedules = umpStateService.loadState(ledgerAndNodeKey);
//
//            // Peer节点启动
//            return peerStart(localConfig.getPeerPath(), installSchedules);
//
//        }
//        throw new IllegalStateException("Can not find LocalConfig from DataBase !!!");
//    }

    @Override
    public PeerStartupSchedules startup() {

        PeerStartupSchedules startupSchedules = new PeerStartupSchedules(UmpConstant.PROJECT_PATH);

        return peerStart(startupSchedules);
    }

    @Override
    public boolean stop(String ledgerAndNodeKey) {
        PeerLocalConfig localConfig = umpStateService.readConfig(ledgerAndNodeKey);

        if (localConfig != null) {

            // Peer节点停止
            return peerStop(localConfig.getPeerPath());
        }
        throw new IllegalStateException("Can not find LocalConfig from DataBase !!!");
    }

    @Override
    public boolean stop() {

        return peerStop(UmpConstant.PROJECT_PATH);
    }

    private MasterAddr loadMaster(PeerLocalConfig localConfig) {

        // 开始安装之后则可以将内存中的数据释放
        String sharedKey = localConfig.getSharedKey();

        if (sharedKey != null) {
            ledgerConfigMemory.remove(sharedKey);
        }

        if (localConfig.master()) {
            return null;
        }

        return localConfig.masterAddr();
    }

    private List<String> sharedConfigKeys(String ledgerKey, PeerSharedConfigs sharedConfigs) {

        List<String> sharedConfigKeys = new ArrayList<>();

        List<PeerLocalConfig> pscs = sharedConfigs.getSharedConfigs();

        for(PeerSharedConfig psc : pscs) {
            sharedConfigKeys.add(ledgerAndNodeKey(ledgerKey, psc));
        }

        return sharedConfigKeys;
    }

    private String ledgerAndNodeKey(String ledgerKey, PeerSharedConfig sharedConfig) {

        return ledgerKey + "-" + sharedConfig.getName();
    }

    private String ledgerInit(String peerPath, PeerInstallSchedules installSchedules, MasterAddr masterAddr, String ledgerKey, String ledgerAndNodeKey) {

        String newLedgerHash = "";

        saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                "Steps to start processing LedgerInit !!!",
                ScheduleState.INIT);

        // 获取当前已经存在的Ledger列表
        LedgerBindingConf ledgerBindingConf = ledgerService.allLedgerHashs(peerPath);

        Set<String> currentLedgerHashs = ledgerBindingConf.getLedgerHashs();

        long lastTime = ledgerBindingConf.getLastTime();

        saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                String.format("Find History Ledger's Size = %s", currentLedgerHashs.size()),
                ScheduleState.INIT);

        String ledgerInitCommand = ledgerService.ledgerInitCommand(peerPath);

        try {

            LOGGER.info("Execute Ledger-Init's Shell {}", ledgerInitCommand);

            Process ledgerInitProcess;

            try {
                // 调用ledgerInit初始化脚本
                ledgerInitProcess = CommandUtils.execute(CommandUtils.toCommandList(ledgerInitCommand));

                saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                        String.format("Execute LedgerInit's Command -> %s", SUCCESS),
                        ScheduleState.INIT);
            } catch (Exception e) {
                LOGGER.error("Execute Ledger-Init's Shell !!!", e);
                throw new IllegalStateException(e);
            }

            int maxSize = 512;

            boolean isInitSuccess = false;

            int checkIndex = 1;

            while (maxSize > 0) {
                // 时延
                Thread.sleep(6000);

                saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                        String.format("%s Check LedgerInit's Status ...... ", checkIndex++),
                        ScheduleState.INIT);

                // 检查账本是否增加
                CurrentLedger currentLedger = checkNewLedger(lastTime, peerPath, currentLedgerHashs);

                lastTime = currentLedger.getLastTime();

                newLedgerHash = currentLedger.getLedgerHash();

                if (newLedgerHash != null && newLedgerHash.length() > 0) {
                    isInitSuccess = true;
                    saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                            String.format("Find New Ledger = %s", newLedgerHash),
                            ScheduleState.INIT);
                    break;
                }
                maxSize --;
            }

            // 完成后，不管是否处理完，都将命令停止
            // 为防止其他应用仍在访问，延时6秒停止
            try {
                Thread.sleep(6000);
                ledgerInitProcess = ledgerInitProcess.destroyForcibly();
                if (ledgerInitProcess.isAlive()) {
                    // 再尝试一次
                    ledgerInitProcess.destroyForcibly();
                }
            } catch (Exception e) {
                // 暂时打印日志
                LOGGER.error("Stop Ledger Init Command !!!", e);
            }

            // 再次判断是否初始化账本成功
            if (newLedgerHash == null) {

                CurrentLedger currentLedger = checkNewLedger(lastTime, peerPath, currentLedgerHashs);

                newLedgerHash = currentLedger.getLedgerHash();

                if (newLedgerHash != null && newLedgerHash.length() > 0) {
                    isInitSuccess = true;
                    saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                            String.format("Final Find New Ledger = %s", newLedgerHash),
                            ScheduleState.INIT);
                }
            }

            if (!isInitSuccess) {
                // 失败则抛出异常
                throw new IllegalStateException("Can Not Find New Ledger !!!");
            }
        } catch (Exception e) {
            saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                    "Execute Ledger-Init Command Fail !!!",
                    ScheduleState.INIT_FAIL);
            LOGGER.error("Execute Ledger-Init Command Fail !!!", e);
            throw new IllegalStateException(e);
        }

        saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
                String.format("Steps to processing LedgerInit -> %s", SUCCESS),
                ScheduleState.INIT_SUCCESS);

        // 将账本Hash写入数据库
        ledgerService.save(ledgerAndNodeKey, newLedgerHash);

        return newLedgerHash;
    }

    private CurrentLedger checkNewLedger(long lastTime, String peerPath, Set<String> currentLedgerHashs) {
        // 再次判断是否初始化账本成功
        LedgerBindingConf ledgerBindingConf = ledgerService.allLedgerHashs(lastTime, peerPath);

        Set<String> newLedgerHashs = ledgerBindingConf.getLedgerHashs();

        CurrentLedger currentLedger = new CurrentLedger(ledgerBindingConf.getLastTime());

        if (newLedgerHashs.size() > currentLedgerHashs.size()) {
            // 获取其新安装的LedgerHash
            for (String ledgerHash : newLedgerHashs) {
                if (!currentLedgerHashs.contains(ledgerHash)) {
                    // 新获取的LedgerHash为当前值
                    currentLedger.ledgerHash = ledgerHash;
                    break;
                }
            }
        }
        return currentLedger;
    }


    private PeerInstallSchedules peerStart(String peerPath, PeerInstallSchedules installSchedules) {

        saveInstallSchedule(installSchedules,
                "Steps to start processing PeerNodeStart !!!",
                ScheduleState.STARTUP_START);
        // 启动Peer
        // 说明初始化成功
        // 判断是否需要启动Peer
        String peerVerify = ledgerService.peerVerifyKey(peerPath);

        try {
            if (!CommandUtils.isActive(peerVerify)) {
                // 不存在，则需要再启动
                String peerStartCmd = ledgerService.peerStartCommand(peerPath);

                LOGGER.info("Execute Peer-Startup's Shell {}", peerStartCmd);

                if (!CommandUtils.executeAndVerify(CommandUtils.toCommandList(peerStartCmd), peerVerify)) {
                    // Peer节点启动失败
                    throw new IllegalStateException("Peer Node Start UP Fail !!!");
                }
                saveInstallSchedule(installSchedules,
                        String.format("Peer's process %s start -> %s", peerVerify, SUCCESS),
                        ScheduleState.STARTUP_SUCCESS);
            } else {
                // 命令已经存在
                saveInstallSchedule(installSchedules,
                        String.format("Peer's process is exist -> %s", peerVerify),
                        ScheduleState.NO_STARTUP);
            }
        } catch (Exception e) {
            saveInstallSchedule(installSchedules,
                    e.getMessage(),
                    ScheduleState.STARTUP_FAIL);
            throw new IllegalStateException(e);
        }

        saveInstallSchedule(installSchedules,
                "Steps to start processing PeerNodeStart over !!!",
                ScheduleState.STARTUP_OVER);

        return installSchedules;
    }

    private PeerStartupSchedules peerStart(PeerStartupSchedules startupSchedules) {

        String peerPath = startupSchedules.getPeerPath();

        saveStartupSchedules(startupSchedules,
                "Steps to start processing PeerNodeStart !!!",
                ScheduleState.STARTUP_START);
        // 启动Peer
        // 说明初始化成功
        // 判断是否需要启动Peer
        String peerVerify = ledgerService.peerVerifyKey(peerPath);

        try {
            if (!CommandUtils.isActive(peerVerify)) {
                // 不存在，则需要再启动
                String peerStartCmd = ledgerService.peerStartCommand(peerPath);

                LOGGER.info("Execute Peer-Startup's Shell {}", peerStartCmd);

                if (!CommandUtils.executeAndVerify(CommandUtils.toCommandList(peerStartCmd), peerVerify)) {
                    // Peer节点启动失败
                    throw new IllegalStateException("Peer Node Start UP Fail !!!");
                }
                saveStartupSchedules(startupSchedules,
                        String.format("Peer's process %s start -> %s", peerVerify, SUCCESS),
                        ScheduleState.STARTUP_SUCCESS);
            } else {
                // 命令已经存在
                saveStartupSchedules(startupSchedules,
                        String.format("Peer's process is exist -> %s", peerVerify),
                        ScheduleState.NO_STARTUP);
            }
        } catch (Exception e) {
            saveStartupSchedules(startupSchedules,
                    e.getMessage(),
                    ScheduleState.STARTUP_FAIL);
            throw new IllegalStateException(e);
        }

        saveStartupSchedules(startupSchedules,
                "Steps to start processing PeerNodeStart over !!!",
                ScheduleState.STARTUP_OVER);

        return startupSchedules;
    }

//    private PeerInstallSchedules peerStart(String peerPath, PeerInstallSchedules installSchedules) {
//
//        MasterAddr masterAddr = installSchedules.getIdentification().getMasterAddr();
//
//        String ledgerKey = installSchedules.getIdentification().getLedgerKey();
//
//        String ledgerAndNodeKey = installSchedules.getIdentification().getLedgerAndNodeKey();
//
//        saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
//                "Steps to start processing PeerNodeStart !!!",
//                ScheduleState.STARTUP_START);
//        // 启动Peer
//        // 说明初始化成功
//        // 判断是否需要启动Peer
//        String peerVerify = ledgerService.peerVerifyKey(peerPath);
//
//        try {
//            if (!CommandUtils.isActive(peerVerify)) {
//                // 不存在，则需要再启动
//                String peerStartCmd = ledgerService.peerStartCommand(peerPath);
//
//                LOGGER.info("Execute Peer-Startup's Shell {}", peerStartCmd);
//
//                if (!CommandUtils.executeAndVerify(CommandUtils.toCommandList(peerStartCmd), peerVerify)) {
//                    // Peer节点启动失败
//                    throw new IllegalStateException("Peer Node Start UP Fail !!!");
//                }
//                saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
//                        String.format("Peer's process %s start -> %s", peerVerify, SUCCESS),
//                        ScheduleState.STARTUP_SUCCESS);
//            } else {
//                // 命令已经存在
//                saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
//                        String.format("Peer's process is exist -> %s", peerVerify),
//                        ScheduleState.NO_STARTUP);
//            }
//        } catch (Exception e) {
//            saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
//                    e.getMessage(),
//                    ScheduleState.STARTUP_FAIL);
//            throw new IllegalStateException(e);
//        }
//
//        saveInstallSchedule(installSchedules, masterAddr, ledgerKey, ledgerAndNodeKey,
//                "Steps to start processing PeerNodeStart over !!!",
//                ScheduleState.STARTUP_OVER);
//
//        return installSchedules;
//    }

    private boolean peerStop(String peerPath) {

        // 判断是否需要停止Peer
        String peerVerify = ledgerService.peerVerifyKey(peerPath);

        try {
            if (CommandUtils.isActive(peerVerify)) {

                LOGGER.info("We need stop peer {}", peerVerify);
                // 需要停止Peer节点
                CommandUtils.killVm(peerVerify);

                // 最多循环5次进行判断
                int maxSize = 5;

                while (maxSize > 0) {
                    try {
                        Thread.sleep(3000);
                        if (!CommandUtils.isActive(peerVerify)) {
                            return true;
                        }
                    } catch (Exception e) {
                        LOGGER.error("Check Peer Stop State !!!", e);
                    } finally {
                        maxSize--;
                    }
                }
            } else {
                LOGGER.info("We do not need stop peer {}", peerVerify);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Stop Peer Node", e);
            throw new IllegalStateException(e);
        }
        return false;
    }

    private String writeConsensusContent(String consensusContent, File consensusFile) throws IOException {
        // 将字符串转换为字节数组
        byte[] consensusBytes = Base58Utils.decode(consensusContent);
        forceWrite(consensusBytes, consensusFile);
        return consensusFile.getName();
    }

    private void forceWrite(List<String> lines, File file) throws IOException {
        if (file.exists()) {
            FileUtils.moveFile(file, new File(file.getPath() + "_bak_" + currentDate()));
        }

        FileUtils.writeLines(file, StandardCharsets.UTF_8.toString(), lines);
    }

    private void forceWrite(byte[] content, File file) throws IOException {
        if (file.exists()) {
            FileUtils.moveFile(file, new File(file.getPath() + "_bak_" + currentDate()));
        }

        FileUtils.writeByteArrayToFile(file, content);
    }

    private void verify(PeerSharedConfigs peerSharedConfigs) {
        // 校验其中内容
        List<PeerLocalConfig> sharedConfigs = peerSharedConfigs.getSharedConfigs();

        // 首先保证其中的数据一致性
        // 1、name不能重复；
        // 2、pubKey不能重复；
        // 3、ipAddr + initPort不能重复;

        Set<String> nameSet = new HashSet<>(),
                pubKeySet = new HashSet<>(),
                addrSet = new HashSet<>();

        for (PeerSharedConfig sharedConfig : sharedConfigs) {
            String name = sharedConfig.getName(),
                    pubKey = sharedConfig.getPubKey(),
                    addr = sharedConfig.addr();
            if (nameSet.contains(name)) {
                throw new IllegalStateException(String.format("Name [%s] is Conflict !!!", name));
            } else {
                nameSet.add(name);
            }

            if (pubKeySet.contains(pubKey)) {
                throw new IllegalStateException(String.format("PubKey [%s] is Conflict !!!", pubKey));
            } else {
                pubKeySet.add(pubKey);
            }

            if (addrSet.contains(addr)) {
                throw new IllegalStateException(String.format("Address [%s] is Conflict !!!", addr));
            } else {
                addrSet.add(addr);
            }
        }
    }

    private void saveInstallSchedule(PeerInstallSchedules installSchedules, MasterAddr masterAddr, String ledgerKey, String ledgerAndNodeKey, String content, ScheduleState state) {

        // 日志打印相关内容
        LOGGER.info(content);

        // 生成InstallSchedule对象
        InstallSchedule schedule = installSchedule(ledgerKey, ledgerAndNodeKey, content, state);

        // 加入反馈列表
        installSchedules.addInstallSchedule(
                new PeerInstallSchedule(new InstallProcess(content), state));

        // 将InstallSchedule写入数据库
        umpStateService.save(schedule, masterAddr);
    }

    private void saveInstallSchedule(PeerInstallSchedules installSchedules, String content, ScheduleState state) {

        // 日志打印相关内容
        LOGGER.info(content);

        // 加入反馈列表
        installSchedules.addInstallSchedule(
                new PeerInstallSchedule(new InstallProcess(content), state));
    }

    private void saveStartupSchedules(PeerStartupSchedules startupSchedules, String content, ScheduleState state) {

        // 日志打印相关内容
        LOGGER.info(content);

        // 加入反馈列表
        startupSchedules.addInstallSchedule(
                new PeerInstallSchedule(new InstallProcess(content), state));
    }

    private InstallSchedule installSchedule(String ledgerKey, String ledgerAndNodeKey, String content, ScheduleState state) {

        InstallProcess process = new InstallProcess(content);

        return new InstallSchedule(ledgerKey, ledgerAndNodeKey, process, state);

    }

    private List<String> localConfContents(PeerLocalConfig localConfig, int nodeId) {
        /**
         * #当前参与方的 id，与ledger.init文件中cons_parti.id一致，默认从0开始
         * local.parti.id=0
         *
         * #当前参与方的公钥
         * local.parti.pubkey=
         *
         * #当前参与方的私钥（密文编码）
         * local.parti.privkey=
         *
         * #当前参与方的私钥解密密钥(原始口令的一次哈希，Base58格式)，如果不设置，则启动过程中需要从控制台输入
         * local.parti.pwd=
         *
         * #账本初始化完成后生成的"账本绑定配置文件"的输出目录
         * #推荐使用绝对路径，相对路径以当前文件(local.conf）所在目录为基准
         * ledger.binding.out=../
         *
         * #账本数据库的连接字符
         * #rocksdb数据库连接格式：rocksdb://{path}，例如：rocksdb:///export/App08/peer/rocks.db/rocksdb0.db
         * #redis数据库连接格式：redis://{ip}:{prot}/{db}，例如：redis://127.0.0.1:6379/0
         * ledger.db.uri=
         *
         * #账本数据库的连接口令
         * ledger.db.pwd=
         */

        List<String> localContents = new ArrayList<>();

        localContents.add(valueToConfig(UmpConstant.LOCAL_PARTI_ID_PREFIX, nodeId));

        localContents.add(valueToConfig(UmpConstant.LOCAL_PARTI_PUBKEY_PREFIX, localConfig.getPubKey()));

        localContents.add(valueToConfig(UmpConstant.LOCAL_PARTI_PRIVKEY_PREFIX, localConfig.getPrivKey()));

        localContents.add(valueToConfig(UmpConstant.LOCAL_PARTI_PWD_PREFIX, localConfig.getEncodePwd()));

        localContents.add(valueToConfig(UmpConstant.LEDGER_BINDING_OUT_PREFIX, localConfig.bindingOutPath()));

        localContents.add(valueToConfig(UmpConstant.LEDGER_DB_URI_PREFIX, dbUri(localConfig.getDbName(), localConfig.getPeerPath())));

        localContents.add(valueToConfig(UmpConstant.LEDGER_DB_PWD_PREFIX, ""));

        return localContents;
    }

    private String valueToConfig(String prefix, Object value) {
        return prefix + "=" + value;
    }

    private String currentDate() {
        return SDF.format(new Date());
    }

    private String dbUri(final String dbName, final String peerPath) {

        String dbDirectoryPath = peerPath + File.separator + dbName;

        String dbUri = ROCKSDB_PROTOCOL + dbDirectoryPath;

        File dbDirectory = new File(dbDirectoryPath);

        if (!dbDirectory.exists()) {
            return dbUri;
        }
        throw new IllegalStateException(String.format("DB name = %s, path = %s is Exist !!!", dbName, dbDirectoryPath));
    }

    private static class CurrentLedger {

        private String ledgerHash;

        private long lastTime;

        public CurrentLedger() {
        }

        public CurrentLedger(long lastTime) {
            this.lastTime = lastTime;
        }

        public String getLedgerHash() {
            return ledgerHash;
        }

        public void setLedgerHash(String ledgerHash) {
            this.ledgerHash = ledgerHash;
        }

        public long getLastTime() {
            return lastTime;
        }

        public void setLastTime(long lastTime) {
            this.lastTime = lastTime;
        }
    }
}
