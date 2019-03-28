/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: BftsmartConfig
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/10 下午3:43
 * Description:
 */
package test.com.jd.blockchain.intgr.batch.bftsmart;

import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.blockchain.utils.security.ShaUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author shaozhuguang
 * @create 2019/1/10
 * @since 1.0.0
 */

public class BftsmartConfig {

    public static final String BFTSMART_DIR = "bftsmart" + File.separator;

    public static final String[] PUB_KEY = new String[64];

    public static final String[] PRIV_KEY = new String[64];

    public static final String PWD = Base58Utils.encode(ShaUtils.hash_256("abc".getBytes()));;

    public static final int startBftsmartPort = 26000;

    public static final int startInitPort = 15000;

    static {
        load();
    }

    @Test
    public void test4ConfigLoad() {
        bftsmartConfigInit(4);
        bftsmartLedgerInit(4);
    }

    @Test
    public void test8ConfigLoad() {
        bftsmartConfigInit(8);
        bftsmartLedgerInit(8);
    }

    @Test
    public void test16ConfigLoad() {
        bftsmartConfigInit(16);
        bftsmartLedgerInit(16);
    }

    @Test
    public void test32ConfigLoad() {
        bftsmartConfigInit(32);
        bftsmartLedgerInit(32);
    }

    @Test
    public void test64ConfigLoad() {
        bftsmartConfigInit(64);
        bftsmartLedgerInit(64);
    }

    public void bftsmartLedgerInit(int size) {
        String file = BFTSMART_DIR + "ledger_init_bftsmart-" + size + ".init";
        ClassPathResource res = new ClassPathResource(file);
        try {
            File resFile = res.getFile();
            List<String> fileContent = org.apache.commons.io.FileUtils.readLines(resFile);
            // 处理新内容
            List<String> newFileContent = handleInitContent(fileContent, size);
            org.apache.commons.io.FileUtils.writeLines(resFile, newFileContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> handleInitContent(List<String> fileContent, int size) {
        List<String> newFileContent = new ArrayList<>();
        for (String srcLine : fileContent) {
            String dstLine = srcLine;
            if (srcLine.startsWith("cons_parti.count")) {
                dstLine = "cons_parti.count=" + size;
            } else if (srcLine.startsWith("###############cons_parti_configs###############")) {
                List<String> parts = initParts(size);
                newFileContent.addAll(parts);
                dstLine = null;
            }
            if (dstLine != null) {
                newFileContent.add(dstLine);
            }
        }
        return newFileContent;
    }

    private List<String> initParts(int size) {
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            parts.add("");
            parts.add("#第" + i + "个参与方的名称");
            parts.add("cons_parti." + i + ".name=xx-" + i + ".com");
            parts.add("#第" + i + "个参与方的公钥文件路径");
            parts.add("cons_parti." + i + ".pubkey-path=");
            parts.add("#第" + i + "个参与方的公钥内容（由keygen工具生成），此参数优先于 pubkey-path 参数");
            parts.add("cons_parti." + i + ".pubkey=" + PUB_KEY[i]);
            parts.add("#第" + i + "个参与方的账本初始服务的主机");
            parts.add("cons_parti." + i + ".initializer.host=127.0.0.1");
            parts.add("#第" + i + "个参与方的账本初始服务的端口");
            int portVal = startInitPort + i * 15;
            parts.add("cons_parti." + i + ".initializer.port=" + portVal);
            parts.add("#第" + i + "个参与方的账本初始服务是否开启安全连接");
            parts.add("cons_parti." + i + ".initializer.secure=false");
            parts.add("");
        }
        return parts;
    }

    public void bftsmartConfigInit(int size) {
        String file = BFTSMART_DIR + "bftsmart-" + size + ".config";
        ClassPathResource res = new ClassPathResource(file);
        try {
            File resFile = res.getFile();
            List<String> fileContent = org.apache.commons.io.FileUtils.readLines(resFile);
            // 处理新内容
            List<String> newFileContent = handleContent(fileContent, size);
            org.apache.commons.io.FileUtils.writeLines(resFile, newFileContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> handleContent(List<String> fileContent, int size) {
        List<String> newFileContent = new ArrayList<>();
        for (String srcLine : fileContent) {
            String dstLine = srcLine;
            if (srcLine.startsWith("system.initial.view")) {
                dstLine = "system.initial.view = " + initViews(size);
            } else if (srcLine.startsWith("system.servers.num")) {
                dstLine = "system.servers.num = " + size;
            } else if (srcLine.startsWith("system.servers.f")) {
                dstLine = "system.servers.f = " + initFsize(size);
            } else if (srcLine.startsWith("###############system.server###############")) {
                List<String> servers = initServers(size);
                newFileContent.addAll(servers);
                dstLine = null;
            }
            if (dstLine != null) {
                newFileContent.add(dstLine);
            }
        }
        return newFileContent;
    }

    private List<String> initServers(int size) {
        List<String> servers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String pubKey = "system.server." + i + ".pubkey=" + PUB_KEY[i];
            String host = "system.server." + i + ".network.host=127.0.0.1";
            int portVal = startBftsmartPort + i * 10;
            String port = "system.server." + i + ".network.port=" + portVal;
            String secure = "system.server." + i+ ".network.secure=false";
            servers.add("############################################");
            servers.add("###### #Consensus Participant" + i + " ######");
            servers.add("############################################");
            servers.add("");
            servers.add(pubKey);
            servers.add(host);
            servers.add(port);
            servers.add(secure);
            servers.add("");
        }
        return servers;
    }

    private int initFsize(int size) {
        return (size - 1) / 3;
    }

    private String initViews(int size) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(i);
        }
        return stringBuilder.toString();
    }

    public static void load() {
        ClassPathResource res = new ClassPathResource(BFTSMART_DIR + "bftsmart-users.conf");
        try(InputStream in = res.getInputStream()){
            loadUsers(in);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static void loadUsers(InputStream in) {
        Properties props = FileUtils.readProperties(in, "UTF-8");
        for (int i = 0; i < PUB_KEY.length; i++) {
            String currUserPubKey = "user[" + i + "]pubKeyBase58";
            String currUserPrivKey = "user[" + i + "]privKeyBase58";
            PUB_KEY[i] = props.getProperty(currUserPubKey).trim();
            PRIV_KEY[i] = props.getProperty(currUserPrivKey).trim();
            System.out.printf("user[%s] pubKey=%s \r\n", i, PUB_KEY[i]);
            System.out.printf("user[%s] privKey=%s \r\n", i, PRIV_KEY[i]);
        }
    }
}