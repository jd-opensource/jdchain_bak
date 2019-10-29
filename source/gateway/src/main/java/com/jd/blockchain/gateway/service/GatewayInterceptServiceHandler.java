package com.jd.blockchain.gateway.service;

import com.jd.blockchain.contract.ContractJarUtils;
import com.jd.blockchain.gateway.PeerService;
import com.jd.blockchain.ledger.ContractCodeDeployOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.TransactionRequest;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.net.URL;

@Service
public class GatewayInterceptServiceHandler implements GatewayInterceptService {

    private static String contractsPath;

    @Autowired
    private PeerService peerService;

    static {
        contractsPath = jarRootDir();
    }

    @Override
    public void intercept(TransactionRequest txRequest) {
        // 当前仅处理合约发布的请求
        Operation[] operations = txRequest.getTransactionContent().getOperations();
        if (operations != null && operations.length > 0) {
            for (Operation op : operations) {
                if (ContractCodeDeployOperation.class.isAssignableFrom(op.getClass())) {
                    // 发布合约请求
                    contractCheck((ContractCodeDeployOperation)op);
                }
            }
        }
    }

    private void contractCheck(final ContractCodeDeployOperation contractOP) {

        // 校验chainCode
        ContractJarUtils.verify(contractsPath, contractOP.getChainCode());
    }

    private static String jarRootDir() {

        try {
            URL url = GatewayInterceptServiceHandler.class.getProtectionDomain().getCodeSource().getLocation();
            String currPath = java.net.URLDecoder.decode(url.getPath(), "UTF-8");
            if (currPath.contains("!/")) {
                currPath = currPath.substring(5, currPath.indexOf("!/"));
            }
            if (currPath.endsWith(".jar")) {
                currPath = currPath.substring(0, currPath.lastIndexOf("/") + 1);
            }
            File file = new File(currPath);

            String homeDir = file.getParent();

            String jarRootPath = homeDir + File.separator + "contracts";

            FileUtils.forceMkdir(new File(jarRootPath));

            return jarRootPath;

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
