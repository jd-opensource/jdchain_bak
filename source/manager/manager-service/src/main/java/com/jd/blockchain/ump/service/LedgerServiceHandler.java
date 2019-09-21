package com.jd.blockchain.ump.service;


import com.jd.blockchain.ump.dao.DBConnection;
import com.jd.blockchain.ump.model.UmpConstant;
import com.jd.blockchain.ump.model.state.LedgerBindingConf;
import com.jd.blockchain.ump.model.state.LedgerInited;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LedgerServiceHandler implements LedgerService {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

    private static final String LEDGER_HASHS_FLAG = "ledger.bindings";

    private static final String LEDGER_NAME_FORMAT = "binding.%s.name";

    private static final String LEDGER_PARTI_ADDRESS_FORMAT = "binding.%s.parti.address";

    private static final String LEDGER_PARTI_NAME_FORMAT = "binding.%s.parti.name";

    private static final String LEDGER_DB_FORMAT = "binding.%s.db.uri";

    private static final String FILE_PEER_FLAG = "deployment-peer";

    private static final String JAR_SUFFIX = "jar";

    private static final int SEED_BYTES_LENGTH = 32;

    private static final int NAME_BYTES_LENGTH = 8;

    private static final int SEED_PART_LENGTH = 8;

    private static final Random LEDGER_RANDOM = new Random();

    @Autowired
    private UmpStateService umpStateService;

    @Autowired
    private DBConnection dbConnection;

    @Override
    public String randomSeed() {
        byte[] seedBytes = new byte[SEED_BYTES_LENGTH];

        LEDGER_RANDOM.nextBytes(seedBytes);

        char[] seedChars = Hex.encodeHex(seedBytes);

        StringBuilder sBuilder = new StringBuilder();

        for (int i = 0; i < seedChars.length; i++) {
            if (i != 0 && i % SEED_PART_LENGTH == 0) {
                sBuilder.append("-");
            }
            sBuilder.append(seedChars[i]);
        }

        return sBuilder.toString();
    }

    @Override
    public String currentCreateTime() {
        return SDF.format(new Date());
    }

    @Override
    public String ledgerInitCommand(String peerPath) {

        return String.format(UmpConstant.CMD_LEDGER_INIT,
                peerPath + UmpConstant.PATH_LEDGER_INIT_BIN);
    }

    @Override
    public String peerStartCommand(String peerPath) {
        return String.format(UmpConstant.CMD_START_UP_FORMAT,
                peerPath + UmpConstant.PATH_PEER_STARTUP_BIN);
    }

    @Override
    public LedgerBindingConf allLedgerHashs(String peerPath) {

        return allLedgerHashs(0L, peerPath);
    }

    @Override
    public LedgerBindingConf allLedgerHashs(long lastTime, String peerPath) {

        // 读取LedgerBingConf文件，假设该文件不存在则返回空值
        Set<String> allLedgerHashs = new HashSet<>();

        PropAndTime propAndTime = loadLedgerBindingConf(lastTime, peerPath);

        Properties props = propAndTime.getProp();

        if (props != null) {

            String ledgerHashChars = props.getProperty(LEDGER_HASHS_FLAG);

            if (ledgerHashChars != null && ledgerHashChars.length() > 0) {
                String[] ledgerHashArray = ledgerHashChars.split(",");
                if (ledgerHashArray.length > 0) {
                    for (String ledgerHash : ledgerHashArray) {
                        allLedgerHashs.add(ledgerHash.trim());
                    }
                }
            }
        }

        LedgerBindingConf ledgerBindingConf = new LedgerBindingConf(propAndTime.getLastTime());

        ledgerBindingConf.setLedgerHashs(allLedgerHashs);

        return ledgerBindingConf;
    }

    @Override
    public List<LedgerInited> allLedgerIniteds(String peerPath) {

        List<LedgerInited> ledgerIniteds = new ArrayList<>();

        PropAndTime propAndTime = loadLedgerBindingConf(0L, peerPath);

        Properties props = propAndTime.getProp();

        if (props != null) {

            String ledgerHashChars = props.getProperty(LEDGER_HASHS_FLAG);

            Set<String> ledgerHashSet = new HashSet<>();

            if (ledgerHashChars != null && ledgerHashChars.length() > 0) {
                String[] ledgerHashArray = ledgerHashChars.split(",");
                if (ledgerHashArray.length > 0) {
                    for (String ledgerHash : ledgerHashArray) {
                        ledgerHashSet.add(ledgerHash.trim());
                    }
                }
            }

            // 根据Hash值，遍历Prop
            for (String hash : ledgerHashSet) {

                LedgerInited ledgerInited = new LedgerInited(hash);

                String ledgerName = props.getProperty(String.format(LEDGER_NAME_FORMAT, hash));

                String partiAddress = props.getProperty(String.format(LEDGER_PARTI_ADDRESS_FORMAT, hash));

                String partiName = props.getProperty(String.format(LEDGER_PARTI_NAME_FORMAT, hash));

                String dbUri = props.getProperty(String.format(LEDGER_DB_FORMAT, hash));

                ledgerIniteds.add(
                        ledgerInited
                        .buildLedgerName(ledgerName)
                        .buildPartiAddress(partiAddress)
                        .buildPartiName(partiName)
                        .buildDbUri(dbUri));
            }
        }
        return ledgerIniteds;
    }

    @Override
    public synchronized boolean dbExist(String peerPath, String ledgerHash) {
        // 检查该账本对应的数据库是否存在

        PropAndTime propAndTime = loadLedgerBindingConf(0L, peerPath);

        // binding.j5faRYSqSqSRmSVgdmPsgq7Hzd1yP7yAGPWkTihekWms94.db.uri=rocksdb:///Users/shaozhuguang/Documents/ideaProjects/jdchain-patch/source/test/test-integration/rocks.db/rocksdb4.db
        Properties props = propAndTime.getProp();

        if (props != null) {
            String dbKey = String.format(LEDGER_DB_FORMAT, ledgerHash);

            String dbUri = props.getProperty(dbKey);

            if (dbUri != null && dbUri.length() > 0) {

                return dbConnection.exist(dbUri);
            }
        }

        return false;
    }

    @Override
    public String peerVerifyKey(String peerPath) {
        // 从libs中读取对应的Peer.jar的文件名称，配合全路径
        File libsDirectory = new File(peerPath + UmpConstant.PATH_SYSTEM);

        Collection<File> jars = FileUtils.listFiles(libsDirectory, new String[]{JAR_SUFFIX}, false);

        String peerVerifyKey = null;

        if (!jars.isEmpty()) {
            for (File jar : jars) {
                String jarName = jar.getName();
                if (jarName.startsWith(FILE_PEER_FLAG)) {
                    peerVerifyKey = jar.getPath();
                    break;
                }
            }
        }

        return peerVerifyKey;
    }

    @Override
    public void save(String ledgerAndNodeKey, String ledgerHash) {
        // 保存LedgerAndNodeKey与账本关系
        umpStateService.saveLedgerHash(ledgerAndNodeKey, ledgerHash);
    }

    @Override
    public String readLedgerHash(String ledgerAndNodeKey) {

        return umpStateService.readLedgerHash(ledgerAndNodeKey);
    }

    private PropAndTime loadLedgerBindingConf(long lastTime, String peerPath) {

        File ledgerBindingConf = new File(peerPath + UmpConstant.PATH_LEDGER_BINDING_CONFIG);

        PropAndTime propAndTime = new PropAndTime(lastTime);

        // 说明被修改过
        if (ledgerBindingConf.exists() && ledgerBindingConf.lastModified() > lastTime) {

            propAndTime.lastTime = ledgerBindingConf.lastModified();

            try (InputStream inputStream = new FileInputStream(ledgerBindingConf)) {

                Properties props = new Properties();

                props.load(inputStream);

                propAndTime.prop = props;

            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        return propAndTime;
    }

    private static class PropAndTime {

        private Properties prop;

        private long lastTime;

        public PropAndTime() {
        }

        public PropAndTime(long lastTime) {
            this.lastTime = lastTime;
        }

        public Properties getProp() {
            return prop;
        }

        public void setProp(Properties prop) {
            this.prop = prop;
        }

        public long getLastTime() {
            return lastTime;
        }

        public void setLastTime(long lastTime) {
            this.lastTime = lastTime;
        }
    }

//    private Properties loadLedgerBindingConf(String peerPath) {
//
//        File ledgerBindingConf = new File(peerPath + UmpConstant.PATH_LEDGER_BINDING_CONFIG);
//
//        if (ledgerBindingConf.exists()) {
//
//            try (InputStream inputStream = new FileInputStream(ledgerBindingConf)) {
//
//                Properties props = new Properties();
//
//                props.load(inputStream);
//
//                return props;
//
//            } catch (Exception e) {
//                throw new IllegalStateException(e);
//            }
//        }
//
//        return null;
//    }
}
