package com.jd.blockchain;

import com.jd.blockchain.contract.model.ContractDeployExeUtil;
import com.jd.blockchain.crypto.asymmetric.PrivKey;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.tools.keygen.KeyGenCommand;
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
 * @phase compile
 * @author zhaogw
 * date 2018/10/18 10:12
 */

@Mojo(name = "contractDeploy")
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
        String ledger = prop.getProperty("ledgerHash");
        String ownerPubPath = prop.getProperty("ownerPubPath");
        String ownerPrvPath = prop.getProperty("ownerPrvPath");
        String ownerPassword = FileUtils.readText(prop.getProperty("ownerPassword"));
        String chainCodePath = prop.getProperty("chainCodePath");

        if(StringUtils.isEmpty(host)){
            logger.info("host can not be empty");
            return;
        }

        if(StringUtils.isEmpty(ledger)){
            logger.info("ledger can not be empty.");
            return;
        }
        if(StringUtils.isEmpty(ownerPubPath)){
            logger.info("pubKey can not be empty.");
            return;
        }
        if(StringUtils.isEmpty(ownerPrvPath)){
            logger.info("prvKey can not be empty.");
            return;
        }
        if(StringUtils.isEmpty(chainCodePath)){
            logger.info("contractPath can not be empty.");
            return;
        }

       File contract = new File(chainCodePath);
        if (!contract.isFile()){
            logger.info("file:"+chainCodePath+" is not exist");
            return;
        }
        byte[] contractBytes = FileUtils.readBytes(chainCodePath);


//        PrivKey prv = KeyGenCommand.decodePrivKeyWithRawPassword(prvKey, password);
//        PubKey pub = KeyGenCommand.decodePubKey(pubKey);
//        BlockchainKeyPair blockchainKeyPair = new BlockchainKeyPair(pub, prv);
        BlockchainKeyPair ownerKey = ContractDeployExeUtil.instance.getKeyPair(ownerPubPath, ownerPrvPath, ownerPassword);
        HashDigest ledgerHash = new HashDigest(Base58Utils.decode(ledger));

        StringBuffer sb = new StringBuffer();
        sb.append("host:"+ host).append(",port:"+port).append(",ledgerHash:"+ledgerHash.toBase58()).
                append(",pubKey:"+ownerKey.getPubKey()).append(",prvKey:"+ownerKey.getPrivKey()).append(",contractPath:"+chainCodePath);
        logger.info(sb.toString());
        if(ContractDeployExeUtil.instance.deploy(host,port,ledgerHash, ownerKey, contractBytes)){
            logger.info("deploy is OK.");
        }
    }
}


