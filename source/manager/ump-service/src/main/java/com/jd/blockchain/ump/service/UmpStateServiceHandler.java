package com.jd.blockchain.ump.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.blockchain.ump.dao.DBConnection;
import com.jd.blockchain.ump.model.*;
import com.jd.blockchain.ump.model.config.LedgerIdentification;
import com.jd.blockchain.ump.model.config.PeerLocalConfig;
import com.jd.blockchain.ump.model.state.*;
import com.jd.blockchain.ump.model.user.UserKeys;
import com.jd.blockchain.ump.model.user.UserKeysVv;
import com.jd.blockchain.ump.util.CommandUtils;
import com.jd.blockchain.ump.util.HttpJsonClientUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class UmpStateServiceHandler implements UmpStateService, Closeable {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMM-ddHHmmss");

    private static final String PEER_IDENTIFICATION_FORMAT = "PEER_IDENTIFICATION_INDEX_%s";

    private static final String PEER_INSTALL_MAX_KEY = "PEER_INSTALL_MAX_INDEX";

    private static final String PEER_INSTALL_KEY_FORMAT = "PEER_INSTALL_INDEX_%s";

    private static final String MASTER_INSTALL_MAX_KEY = "MASTER_INSTALL_MAX_INDEX";

    private static final String MASTER_INSTALL_KEY_FORMAT = "MASTER_INSTALL_INDEX_%s";

    private static final String USERS_KEY_MAX_KEY = "USERS_KEY_MAX_INDEX";

    private static final String USERS_KEY_FORMAT = "USERS_%s_REGISTER";

    private static final String MAX_SIZE_KEY_SUFFIX = "_MAX_SIZE_KEY";

    private static final String LEDGER_HASH_KEY_SUFFIX = "_LEDGER_HASH_KEY";

    private static final String LEDGER_NODE_KEY_CONFIG_SUFFIX = "_LEDGER_NODE_CONFIG_KEY";

    private static final String LEDGER_NODE_KEY_SUFFIX = "_LEDGER_NODE_KEY";

    private static final String CURRENT_INDEX_KEY_SUFFIX_FORMAT = "_%s_INDEX_KEY";

    private static final String PORT_ARG = "-p";

    private static final String LOCALHOST = "127.0.0.1";

    private ExecutorService singleHttpThread = Executors.newSingleThreadExecutor();

    @Autowired
    private DBConnection dbConnection;

    @Autowired
    private LedgerService ledgerService;

    @Override
    public synchronized void save(String ledgerAndNodeKey, PeerLocalConfig localConfig) {

        String ledgerAndNodeConfigKey = ledgerAndNodeConfigKey(ledgerAndNodeKey);

        dbConnection.put(ledgerAndNodeConfigKey, JSON.toJSONString(localConfig));
    }

    @Override
    public synchronized void save(String ledgerKey, List<String> sharedConfigKeys) {

        String ledgerAllNodeKey = ledgerAllNodeKey(ledgerKey);

        StringBuilder sBuilder = new StringBuilder();

        for (String sharedConfigKey : sharedConfigKeys) {
            if (sBuilder.length() > 0) {
                sBuilder.append(";");
            }
            sBuilder.append(sharedConfigKey);
        }

        dbConnection.put(ledgerAllNodeKey, sBuilder.toString());
    }

    @Override
    public synchronized void save(InstallSchedule installSchedule, MasterAddr masterAddr) {
        try {
            String ledgerAndNodeKey = installSchedule.getLedgerAndNodeKey();
            // 不使用队列，直接将其写入数据库
            // 需要查询目前该Key对应的最大值是多少
            String maxKey = ledgerAndNodeMaxKey(ledgerAndNodeKey);
            String maxIdChars = dbConnection.get(maxKey);
            int maxId = 0;
            if (maxIdChars != null && maxIdChars.length() > 0) {
                maxId = Integer.parseInt(maxIdChars) + 1;
            }

            String newKey = ledgerAndNodeCurrentNewKey(ledgerAndNodeKey, maxId);

            // 内容写入数据库
            dbConnection.put(newKey, installSchedule, InstallSchedule.class);

            // 更新最大值
            dbConnection.put(maxKey, String.valueOf(maxId));

            if (masterAddr != null && masterAddr.legal()) {
                singleHttpThread.execute(() -> {

                    try {
                        // 发送HTTP请求
                        HttpJsonClientUtils.httpPost(masterAddr, UmpConstant.REQUEST_STATE_URL, installSchedule, String.class, false);
                    } catch (Exception e) {
                        // 暂不关注是否发送成功
                        LOGGER.error(e.toString());
                    }

                });
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public synchronized void save(UserKeys userKeys) {

        int maxIndex = maxIndex(USERS_KEY_MAX_KEY);

        String userKey = usersKey(maxIndex);

        // 重置userId
        userKeys.setId(maxIndex);

        // 将用户信息写入数据库
        dbConnection.put(userKey, JSON.toJSONString(userKeys));

        // 更新最大值
        dbConnection.put(USERS_KEY_MAX_KEY, String.valueOf(maxIndex));

        try {
            // 将其放入文件中
            String keysDirPath = UmpConstant.PROJECT_PATH + UmpConstant.PATH_CONFIG_KEYS;

            File keysDir = new File(keysDirPath);

            if (!keysDir.exists()) {
                // 创建文件夹
                keysDir.mkdir();
            }
            saveKeys2Files(keysDirPath, userKeys);
        } catch (Exception e) {
            LOGGER.error("Save Keys To File !", e);
        }
    }

    @Override
    public synchronized void save(LedgerPeerInstall peerInstall) {

        int maxIndex = maxIndex(PEER_INSTALL_MAX_KEY);

        // 将用户信息写入数据库
        dbConnection.put(peerInstallKey(maxIndex), JSON.toJSONString(peerInstall));

        // 更新最大值
        dbConnection.put(PEER_INSTALL_MAX_KEY, String.valueOf(maxIndex));

    }

    @Override
    public synchronized void save(LedgerMasterInstall masterInstall) {

        int maxIndex = maxIndex(MASTER_INSTALL_MAX_KEY);

        // 将用户信息写入数据库
        dbConnection.put(masterInstallKey(maxIndex), JSON.toJSONString(masterInstall));

        // 更新最大值
        dbConnection.put(MASTER_INSTALL_MAX_KEY, String.valueOf(maxIndex));
    }

    @Override
    public synchronized void save(LedgerIdentification identification) {

        String ledgerAndNodeKey = identification.getLedgerAndNodeKey();

        String idKey = String.format(PEER_IDENTIFICATION_FORMAT, ledgerAndNodeKey);

        dbConnection.put(idKey, JSON.toJSONString(identification));
    }

    @Override
    public void saveLedgerHash(String ledgerAndNodeKey, String ledgerHash) {

        String ledgerHashKey = ledgerAndNodeHashKey(ledgerAndNodeKey);

        dbConnection.put(ledgerHashKey, ledgerHash);
    }

    @Override
    public List<UserKeys> readUserKeysList() {

        List<UserKeys> userKeysList = new ArrayList<>();

        String maxIndexChars = dbConnection.get(USERS_KEY_MAX_KEY);

        if (maxIndexChars != null && maxIndexChars.length() > 0) {

            int maxIndex = Integer.parseInt(maxIndexChars);

            for (int i = 0; i <= maxIndex; i++) {
                try {

                    String json = dbConnection.get(usersKey(i));

                    if (json != null && json.length() > 0) {
                        userKeysList.add(JSON.parseObject(json, UserKeys.class));
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString());
                }
            }
        }

        return userKeysList;
    }

    @Override
    public List<UserKeysVv> readUserKeysVvList() {

        List<UserKeysVv> userKeysVvList = new ArrayList<>();

        List<UserKeys> userKeysList = readUserKeysList();

        if (!userKeysList.isEmpty()) {
            for (UserKeys userKeys : userKeysList) {

                userKeysVvList.add(userKeys.toUserKeysVv());
            }
        }

        return userKeysVvList;
    }

    @Override
    public UserKeys readUserKeys(int id) {

        String userKey = usersKey(id);

        String userKeysJson = dbConnection.get(userKey);

        if (userKeysJson != null && userKeysJson.length() > 0) {
            return JSON.parseObject(userKeysJson, UserKeys.class);
        }

        return null;
    }

    @Override
    public PeerLocalConfig readConfig(String ledgerAndNodeKey) {

        String json = dbConnection.get(ledgerAndNodeConfigKey(ledgerAndNodeKey));

        if (json != null && json.length() > 0) {

            return JSON.parseObject(json, PeerLocalConfig.class);
        }

        return null;
    }

    @Override
    public PeerInstallSchedules loadState(String ledgerAndNodeKey) {

        PeerInstallSchedules installSchedules = loadInitState(ledgerAndNodeKey);

        String ledgerHash = ledgerService.readLedgerHash(ledgerAndNodeKey);

        if (ledgerHash == null || ledgerHash.length() == 0) {
            throw new IllegalStateException("Can not find LedgerHash from DataBase !!!");
        }

        return installSchedules.initLedgerHash(ledgerHash);

    }

    @Override
    public PeerInstallSchedules loadInitState(String ledgerAndNodeKey) {
        // 获取LedgerIdentification
        LedgerIdentification identification = readIdentification(ledgerAndNodeKey);

        if (identification == null) {
            throw new IllegalStateException("Can not find LedgerIdentification from DataBase !!!");
        }

        return new PeerInstallSchedules(identification);
    }

    @Override
    public PeerInstallSchedules readState(final String ledgerAndNodeKey) {

        PeerInstallSchedules installSchedules = loadState(ledgerAndNodeKey);

        loadInstallSchedules(installSchedules, ledgerAndNodeKey);

        return installSchedules;
    }

    @Override
    public PeerInstallSchedules readInitState(String ledgerAndNodeKey) {

        PeerInstallSchedules installSchedules = loadInitState(ledgerAndNodeKey);

        loadInstallSchedules(installSchedules, ledgerAndNodeKey);

        return installSchedules;
    }

    @Override
    public Map<String, List<InstallSchedule>> readStates(String ledgerKey) {

        String ledgerAllNodeKey = ledgerAllNodeKey(ledgerKey);

        String ledgerAllNodeValues = dbConnection.get(ledgerAllNodeKey);

        String[] ledgerAndNodeKeys = ledgerAllNodeValues.split(";");

        Map<String, List<InstallSchedule>> allInstallSchedules = new HashMap<>();

        // 不存在就返回空值
        if (ledgerAndNodeKeys.length > 0) {

            for (String ledgerAndNodeKey : ledgerAndNodeKeys) {
                // 获取每个LedgerAndNodeKey数据
                List<InstallSchedule> installSchedules = readInstallSchedules(ledgerAndNodeKey);

                if (installSchedules != null) {

                    allInstallSchedules.put(ledgerAndNodeKey, installSchedules);
                }
            }
        }

        return allInstallSchedules;
    }

    @Override
    public LedgerIdentification readIdentification(String ledgerAndNodeKey) {

        String idKey = String.format(PEER_IDENTIFICATION_FORMAT, ledgerAndNodeKey);

        String identificationJson = dbConnection.get(idKey);

        if (identificationJson != null && identificationJson.length() > 0) {

            return JSON.parseObject(identificationJson, LedgerIdentification.class);
        }

        return null;
    }

    @Override
    public List<LedgerPeerInstall> readLedgerPeerInstalls() {

        List<LedgerPeerInstall> peerInstallList = new ArrayList<>();

        String maxIndexChars = dbConnection.get(PEER_INSTALL_MAX_KEY);

        if (maxIndexChars != null && maxIndexChars.length() > 0) {

            int maxIndex = Integer.parseInt(maxIndexChars);

            for (int i = 1; i <= maxIndex; i++) {
                try {
                    String json = dbConnection.get(peerInstallKey(i));

                    if (json != null && json.length() > 0) {
                        peerInstallList.add(JSON.parseObject(json, LedgerPeerInstall.class));
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString());
                }
            }
        }

        return peerInstallList;
    }

    @Override
    public List<LedgerMasterInstall> readLedgerMasterInstalls() {

        List<LedgerMasterInstall> masterInstalls = new ArrayList<>();

        String maxIndexChars = dbConnection.get(PEER_INSTALL_MAX_KEY);

        if (maxIndexChars != null && maxIndexChars.length() > 0) {

            int maxIndex = Integer.parseInt(maxIndexChars);

            for (int i = 1; i <= maxIndex; i++) {
                try {
                    String json = dbConnection.get(masterInstallKey(i));

                    if (json != null && json.length() > 0) {
                        masterInstalls.add(JSON.parseObject(json, LedgerMasterInstall.class));
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString());
                }
            }
        }

        return masterInstalls;
    }

    @Override
    public List<LedgerPeerInited> readLedgerPeerIniteds() {

        List<LedgerPeerInited> peerIniteds = new ArrayList<>();

        List<LedgerPeerInstall> peerInstalls = readLedgerPeerInstalls();

        if (!peerInstalls.isEmpty()) {

            LOGGER.info("Read LedgerPeerInstalls, Size = {}", peerInstalls.size());
            for (LedgerPeerInstall peerInstall : peerInstalls) {

                String ledgerAndNodeKey = peerInstall.getLedgerAndNodeKey();

                // 数据库中读取存放的LedgerHash
                String ledgerHash = readLedgerHash(ledgerAndNodeKey);

                if (ledgerHash == null || ledgerHash.length() == 0) {
                    continue;
                }

                LedgerPeerInited peerInited = new LedgerPeerInited(ledgerHash, peerInstall);

                // 检测账本中的Hash是否真正存在
                StartupState startupState = StartupState.UNKNOWN;
                try {
                    startupState = startupState(ledgerHash, peerInstall);
                } catch (Exception e) {
                    LOGGER.error("Check Ledger Hash Exist !!!", e);
                }

                // 设置账本状态
                peerInited.setStartupState(startupState);

                // 添加到集合
                peerIniteds.add(peerInited);
            }
        } else {
            LOGGER.error("Read LedgerPeerInstalls is Empty !!!");
        }
        return peerIniteds;
    }

    @Override
    public List<LedgerPeerInited> readLedgerPeerIniteds(String search) {

        List<LedgerPeerInited> initedList = readLedgerPeerIniteds();

        if (search != null && search.length() > 0 && !initedList.isEmpty()) {

            List<LedgerPeerInited> filterInitedList = new ArrayList<>();

            for (LedgerPeerInited peerInited : initedList) {
                if (isMatch(peerInited, search)) {
                    filterInitedList.add(peerInited);
                }
            }

            return filterInitedList;
        }

        return initedList;
    }

    @Override
    public List<LedgerInited> readLedgerIniteds(String search) {

        List<LedgerInited> ledgerInitedsFromConf = loadAllLedgerIniteds(UmpConstant.PROJECT_PATH);

        if (!ledgerInitedsFromConf.isEmpty()) {

            List<LedgerInited> ledgerIniteds = new ArrayList<>();

            for (LedgerInited ledgerInited : ledgerInitedsFromConf) {

                if (isMatch(ledgerInited, search)) {
                    ledgerIniteds.add(ledgerInited);
                }
            }

            return ledgerIniteds;
        }

        return ledgerInitedsFromConf;
    }

    @Override
    public String readLedgerHash(String ledgerAndNodeKey) {

        String ledgerHashKey = ledgerAndNodeHashKey(ledgerAndNodeKey);

        return dbConnection.get(ledgerHashKey);
    }

    @Override
    public int peerPort(String peerPath) {

        String peerVerify = ledgerService.peerVerifyKey(peerPath);

        try {
            if (!CommandUtils.isActive(peerVerify)) {
                // 进程不存在
                LOGGER.info("Can not find Peer Process {} !!!", peerVerify);
                return 0;
            }
            return listenPort(peerVerify);
        } catch (Exception e) {
            // 进程处理错误打印日志即可
            LOGGER.error(String.format("Peer Port Check %s !!!", peerVerify), e);
        }

        return 0;
    }

    @Override
    public int peerPort() {
        return peerPort(UmpConstant.PROJECT_PATH);
    }

    @Override
    public void close() throws IOException {
//        writeRunner.close();
    }

    private boolean isMatch(LedgerInited ledgerInited, String search) {

        if (search == null || search.length() == 0) {
            return true;
        }

        String ledgerHash = ledgerInited.getLedgerHash();
        String ledgerName = ledgerInited.getLedgerName();
        String partiName = ledgerInited.getPartiName();
        String partiAddress = ledgerInited.getPartiAddress();
        String dbUri = ledgerInited.getDbUri();
        StartupState startupState = ledgerInited.getStartupState();

        if (
                ledgerHash.contains(search)            ||
                startupState.toString().equals(search) ||
                ledgerName.contains(search)            ||
                partiName.contains(search)             ||
                partiAddress.contains(search)          ||
                dbUri.contains(search)
        ) {
            return true;
        }
        return false;
    }

    private boolean isMatch(LedgerPeerInited peerInited, String search) {

        if (search == null || search.length() == 0) {
            return true;
        }

        String ledgerHash = peerInited.getLedgerHash();
        StartupState startupState = peerInited.getStartupState();
        LedgerPeerInstall peerInstall = peerInited.getPeerInstall();

        if (ledgerHash.contains(search) ||
                startupState.toString().equals(search) ||
                peerInstall.getNodeName().contains(search) ||
                peerInstall.getCreateTime().contains(search)
        ) {
            return true;
        }
        return false;
    }

    private void loadInstallSchedules(PeerInstallSchedules installSchedules, String ledgerAndNodeKey) {
        List<InstallSchedule> schedules = readInstallSchedules(ledgerAndNodeKey);

        for (InstallSchedule installSchedule : schedules) {
            installSchedules.addInstallSchedule(
                    new PeerInstallSchedule(installSchedule.getProcess(), installSchedule.getState()));
        }
    }

    private List<InstallSchedule> readInstallSchedules(String ledgerAndNodeKey) {
        String maxKey = ledgerAndNodeMaxKey(ledgerAndNodeKey);
        String maxIdChars = dbConnection.get(maxKey);
        if (maxIdChars == null || maxIdChars.length() == 0) {
            return null;
        }
        int maxId = Integer.parseInt(maxIdChars);

        List<InstallSchedule> schedules = new ArrayList<>();

        for (int i = 0; i <= maxId; i++) {

            try {
                String currentKey = ledgerAndNodeCurrentNewKey(ledgerAndNodeKey, i);

                String jsonChars = dbConnection.get(currentKey);

                if (jsonChars != null && jsonChars.length() > 0) {
                    schedules.add(JSON.parseObject(jsonChars, InstallSchedule.class));
                }
            } catch (Exception e) {
                // 打印错误，暂不处理其他
                LOGGER.error(e.toString());
            }
        }

        return schedules;
    }

    private List<LedgerInited> loadAllLedgerIniteds(String peerPath) {

        List<LedgerInited> ledgerInitedsFromConf = ledgerService.allLedgerIniteds(peerPath);

        if (!ledgerInitedsFromConf.isEmpty()) {

            // 逐个检查其状态
            for (LedgerInited ledgerInited : ledgerInitedsFromConf) {
                // 判断该账本对应的数据库是否存在
                if (!dbConnection.exist(ledgerInited.getDbUri())) {
                    ledgerInited.setStartupState(StartupState.DB_UNEXIST);
                    continue;
                }

                String peerVerify = ledgerService.peerVerifyKey(peerPath);

                try {
                    if (!CommandUtils.isActive(peerVerify)) {
                        // 进程不存在
                        LOGGER.info("Can not find Peer Process {} !!!", peerVerify);
                        ledgerInited.setStartupState(StartupState.UNLOAD);
                        continue;
                    }
                } catch (Exception e) {
                    // 进程处理错误打印日志即可
                    LOGGER.error(String.format("Command Check %s !!!", peerVerify), e);
                }
                // 查看该进程对应的监听端口
                try {
                    int listenPort = listenPort(peerVerify);

                    LOGGER.info("Find Listen Port = {} !", listenPort);

                    if (listenPort > 0) {

                        int maxSize = 3, checkIndex = 1;

                        boolean isRead = false;

                        while (maxSize > 0) {

                            try {
                                // 发送请求到对应地址
                                JSONArray ledgerHashs = HttpJsonClientUtils.httpGet(ledgersUrl(listenPort), JSONArray.class, true);

                                if (ledgerHashs != null && !ledgerHashs.isEmpty()) {
                                    for(Object hashObj : ledgerHashs) {
                                        if (hashObj instanceof JSONObject) {
                                            if (ledgerInited.getLedgerHash().equals(((JSONObject) hashObj).getString("value"))) {
                                                // 说明该账本已经被加载
                                                ledgerInited.setStartupState(StartupState.LOADED);
                                                isRead = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (isRead) {
                                        break;
                                    }
                                }

                                // 6秒休眠
                                Thread.sleep(3000);
                            } catch (Exception e) {
                                LOGGER.error(String.format("Request LedgerHashs from PeerNode [%s]", checkIndex++), e);
                            }

                            maxSize --;
                        }

                        if (!isRead) {
                            // 表明等待加载，无须再启动
                            ledgerInited.setStartupState(StartupState.LOADING);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(String.format("Command [%s] 'Listen Port Check !!!", peerVerify), e);
                }
            }
        }

        return ledgerInitedsFromConf;
    }

    private StartupState startupState(String ledgerHash, LedgerPeerInstall peerInstall) {

        String peerPath = peerInstall.getPeerPath();

        // 首先检查文件中是否存在该Hash值
        LedgerBindingConf ledgerBindingConf = ledgerService.allLedgerHashs(peerPath);

        Set<String> allLedgerHashs = ledgerBindingConf.getLedgerHashs();

        if (!allLedgerHashs.contains(ledgerHash)) {

            // 文件中不存在
            return StartupState.UNEXIST;
        }

        // 判断该账本对应的数据库是否存在
        if (!ledgerService.dbExist(peerPath, ledgerHash)) {

            // 该账本对应数据库不存在
            return StartupState.DB_UNEXIST;
        }

        // 文件中存在则检查进程是否存在
        // 进程存在标识为LOADED，否则标识为LOADING，暂时用不到LOADING
        String peerVerify = ledgerService.peerVerifyKey(peerPath);

        try {
            if (!CommandUtils.isActive(peerVerify)) {
                // 进程不存在
                return StartupState.UNLOAD;
            }

        } catch (Exception e) {
            // 进程处理错误打印日志即可
            LOGGER.error(String.format("Command Check %s !!!", peerVerify), e);
        }

        // 查看该进程对应的监听端口
        try {
            int listenPort = listenPort(peerVerify);

            LOGGER.info("Find Listen Port = {} !", listenPort);

            if (listenPort > 0) {
                // 发送请求到对应地址
                JSONArray ledgerHashs = HttpJsonClientUtils.httpGet(ledgersUrl(listenPort), JSONArray.class, true);

                if (ledgerHashs != null && !ledgerHashs.isEmpty()) {
                    for(Object hashObj : ledgerHashs) {
                        if (hashObj instanceof JSONObject) {
                            if (ledgerHash.equals(((JSONObject) hashObj).getString("value"))) {
                                // 说明该账本已经被加载
                                return StartupState.LOADED;
                            }
                        }
                    }
                    // 表明等待加载，无须再启动
                    return StartupState.LOADING;
                }
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Command [%s] 'Listen Port Check !!!", peerVerify), e);
        }

        return StartupState.UNKNOWN;
    }

    private String ledgersUrl(int listenPort) {
        return "http://" + LOCALHOST + ":" + listenPort + "/ledgers";
    }

    private int listenPort(String peerVerify) throws Exception {

        String portArg = mainArg(peerVerify, PORT_ARG);

        if (portArg != null && portArg.length() > 0) {
            return Integer.parseInt(portArg);
        }

        return 0;
    }

    private String mainArg(String processName, String argKey) throws Exception {

        String[] cmdLineArray = mainArgs(processName);

        if (cmdLineArray != null && cmdLineArray.length > 0) {
            for (int i = 0; i < cmdLineArray.length; i++) {
                String currArg = cmdLineArray[i].trim();
                if (currArg.equals(argKey) && (i + 1) < cmdLineArray.length) {
                    return cmdLineArray[i+1].trim();
                }
            }
        }
        return null;
    }

    private String[] mainArgs(String processName) throws Exception {

        String mainArgs = CommandUtils.mainArgs(processName);

        if (mainArgs != null && mainArgs.length() > 0) {
            ///Users/shaozhuguang/Documents/newenv/peer4/system/deployment-peer-1.1.0-SNAPSHOT.jar -home=/Users/shaozhuguang/Documents/newenv/peer4 -c /Users/shaozhuguang/Documents/newenv/peer4/config/ledger-binding.conf -p 7080
            return mainArgs.split(" ");
        }

        return null;
    }

    private synchronized int maxIndex(String key) {
        int maxIndex = 1;
        String maxIndexChars = dbConnection.get(key);
        if (maxIndexChars != null && maxIndexChars.length() > 0) {
            maxIndex = Integer.parseInt(maxIndexChars) + 1;
        }
        return maxIndex;
    }

    private String usersKey(int userId) {
        return String.format(USERS_KEY_FORMAT, userId);
    }

    private String peerInstallKey(int index) {
        return String.format(PEER_INSTALL_KEY_FORMAT, index);
    }

    private String masterInstallKey(int index) {
        return String.format(MASTER_INSTALL_KEY_FORMAT, index);
    }

    private String ledgerAndNodeConfigKey(String ledgerAndNodeKey) {

        return ledgerAndNodeKey + LEDGER_NODE_KEY_CONFIG_SUFFIX;
    }

    private String ledgerAllNodeKey(String ledgerKey) {

        return ledgerKey + LEDGER_NODE_KEY_SUFFIX;
    }

    private String ledgerAndNodeMaxKey(String ledgerAndNodeKey) {

        return ledgerAndNodeKey + MAX_SIZE_KEY_SUFFIX;
    }

    private String ledgerAndNodeHashKey(String ledgerAndNodeKey) {

        return ledgerAndNodeKey + LEDGER_HASH_KEY_SUFFIX;
    }

    private String ledgerAndNodeCurrentNewKey(String ledgerAndNodeKey, int currentId) {

        return String.format(
                ledgerAndNodeKey + CURRENT_INDEX_KEY_SUFFIX_FORMAT,
                currentId);
    }

    private void saveKeys2Files(String keysDirPath, UserKeys userKeys) throws IOException {

        // 写入私钥
        write(keysDirPath, userKeys.getName(), UmpConstant.PRIVATE_KEY_SUFFIX, userKeys.getPrivKey());
        // 写入公钥
        write(keysDirPath, userKeys.getName(), UmpConstant.PUBLIC_KEY_SUFFIX, userKeys.getPubKey());
        // 写入密钥
        write(keysDirPath, userKeys.getName(), UmpConstant.PWD_SUFFIX, userKeys.getEncodePwd());
    }

    private void write(String keysDirPath, String name, String suffix, String writeContent) throws IOException {

        String keyeFilePath = keysDirPath + File.separator + name + suffix;

        File keysFile = new File(keyeFilePath);

        if (keysFile.exists()) {
            // 文件存在，备份文件
            FileUtils.copyFile(keysFile, new File(keyeFilePath + "_bak_" + currentTime()));
        }

        // 将Priv文件内容写入
        FileUtils.writeStringToFile(keysFile, writeContent, StandardCharsets.UTF_8);
    }

    private String currentTime() {
        return SDF.format(new Date());
    }
}
