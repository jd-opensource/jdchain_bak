package com.jd.blockchain;

import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainKeypair;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;

public class SDKDemo_Constant {

    public static String GW_IPADDR = "localhost";
    public static int GW_PORT = 8080;
    public static String GW_PUB_KEY = "3snPdw7i7PisoLpqqtETdqzQeKVjQReP2Eid9wYK67q9z6trvByGZs";
    public static String GW_PRIV_KEY = "177gk2PbxhHeEdfAAqGfShJQyeV4XvGsJ9CvJFUbToBqwW1YJd5obicySE1St6SvPPaRrUP";
    public static String GW_PASSWORD = "8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG";

    public static PrivKey gwPrivkey0 = KeyGenUtils.decodePrivKey(GW_PRIV_KEY, GW_PASSWORD);
    public static PubKey gwPubKey0 = KeyGenUtils.decodePubKey(GW_PUB_KEY);
    public static BlockchainKeypair adminKey = new BlockchainKeypair(gwPubKey0, gwPrivkey0);

    public static final byte[] readChainCodes(String contractZip) {
        // 构建合约的字节数组;
        try {
            ClassPathResource contractPath = new ClassPathResource(contractZip);
//            File contractFile = new File(contractPath.getURI());

            InputStream in = contractPath.getInputStream();
            // 将文件写入至config目录下
            File directory = new File(".");
            String configPath = directory.getAbsolutePath() + File.separator + "contract.jar";
            File targetFile = new File(configPath);
            // 先将原来文件删除再Copy
            if (targetFile.exists()) {
                FileUtils.forceDelete(targetFile);
            }
            FileUtils.copyInputStreamToFile(in, targetFile);
            return FileUtils.readFileToByteArray(targetFile);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
