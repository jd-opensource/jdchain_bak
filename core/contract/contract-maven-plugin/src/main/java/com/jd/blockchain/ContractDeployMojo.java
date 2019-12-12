package com.jd.blockchain;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.utils.StringUtils;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * for contract remote deploy;
 * @goal contractDeploy
 * @phase process-sources
 * @Author zhaogw
 * @Date 2018/10/18 10:12
 */

@Mojo(name = "deploy")
public class ContractDeployMojo extends AbstractMojo {
    Logger logger = LoggerFactory.getLogger(ContractDeployMojo.class);

    @Parameter
    private File config;

    @Override
    public void execute()throws MojoFailureException {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(config);
            prop.load(input);

        } catch (IOException ex) {
            logger.error(ex.getMessage());
            throw new MojoFailureException("io error");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        int port;
        try {
            port = Integer.parseInt(prop.getProperty("port"));
        }catch (NumberFormatException e){
            logger.error(e.getMessage());
            throw new MojoFailureException("invalid port");
        }
        String host = prop.getProperty("host");
        String ledger = prop.getProperty("ledger");
        String pubKey = prop.getProperty("pubKey");
        String prvKey = prop.getProperty("prvKey");
        String password = prop.getProperty("password");
        String contractPath = prop.getProperty("contractPath");


        if(StringUtils.isEmpty(host)){
            logger.info("host不能为空");
            return;
        }

        if(StringUtils.isEmpty(ledger)){
            logger.info("ledger不能为空.");
            return;
        }
        if(StringUtils.isEmpty(pubKey)){
            logger.info("pubKey不能为空.");
            return;
        }
        if(StringUtils.isEmpty(prvKey)){
            logger.info("prvKey不能为空.");
            return;
        }
        if(StringUtils.isEmpty(contractPath)){
            logger.info("contractPath不能为空.");
            return;
        }

       File contract = new File(contractPath);
        if (!contract.isFile()){
            logger.info("文件"+contractPath+"不存在");
            return;
        }
        byte[] contractBytes = FileUtils.readBytes(contractPath);


        PrivKey prv = KeyGenUtils.decodePrivKeyWithRawPassword(prvKey, password);
        PubKey pub = KeyGenUtils.decodePubKey(pubKey);
        BlockchainKeypair blockchainKeyPair = new BlockchainKeypair(pub, prv);
        HashDigest ledgerHash = new HashDigest(Base58Utils.decode(ledger));

        StringBuffer sb = new StringBuffer();
        sb.append("host:"+ host).append(",port:"+port).append(",ledgerHash:"+ledgerHash.toBase58()).
                append(",pubKey:"+pubKey).append(",prvKey:"+prv).append(",contractPath:"+contractPath);
        logger.info(sb.toString());
        ContractDeployExeUtil.instance.deploy(host,port,ledgerHash, blockchainKeyPair, contractBytes);
    }

}


