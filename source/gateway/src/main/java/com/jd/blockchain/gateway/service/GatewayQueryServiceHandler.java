package com.jd.blockchain.gateway.service;

import com.jd.blockchain.consensus.ConsensusProvider;
import com.jd.blockchain.consensus.ConsensusProviders;
import com.jd.blockchain.consensus.ConsensusSettings;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.gateway.PeerService;
import com.jd.blockchain.ledger.ContractInfo;
import com.jd.blockchain.ledger.LedgerAdminInfo;
import com.jd.blockchain.ledger.LedgerMetadata;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.sdk.ContractSettings;
import com.jd.blockchain.sdk.LedgerBaseSettings;
import com.jd.blockchain.utils.QueryUtil;
import com.jd.blockchain.utils.codec.HexUtils;
import com.jd.blockchain.utils.decompiler.utils.DecompilerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;


/**
 * @Author zhaogw
 * @Date 2019/2/22 10:39
 */
@Component
public class GatewayQueryServiceHandler implements GatewayQueryService {

    @Autowired
    private PeerService peerService;

    @Override
    public HashDigest[] getLedgersHash(int fromIndex, int count) {
        HashDigest[] ledgersHashs = peerService.getQueryService().getLedgerHashs();
        int[] indexAndCount = QueryUtil.calFromIndexAndCount(fromIndex, count, ledgersHashs.length);
        return Arrays.copyOfRange(ledgersHashs, indexAndCount[0], indexAndCount[0] + indexAndCount[1]);
    }

    @Override
    public ParticipantNode[] getConsensusParticipants(HashDigest ledgerHash, int fromIndex, int count) {
        ParticipantNode[] participantNodes = peerService.getQueryService().getConsensusParticipants(ledgerHash);
        int[] indexAndCount = QueryUtil.calFromIndexAndCount(fromIndex, count, participantNodes.length);
        ParticipantNode[] participantNodesNews = Arrays.copyOfRange(participantNodes, indexAndCount[0],
                indexAndCount[0] + indexAndCount[1]);
        return participantNodesNews;
    }

    @Override
    public LedgerBaseSettings getLedgerBaseSettings(HashDigest ledgerHash) {

        LedgerAdminInfo ledgerAdminInfo = peerService.getQueryService().getLedgerAdminInfo(ledgerHash);

        return initLedgerBaseSettings(ledgerAdminInfo);
    }

    @Override
    public ContractSettings getContractSettings(HashDigest ledgerHash, String address) {
        ContractInfo contractInfo = peerService.getQueryService().getContract(ledgerHash, address);
        return contractSettings(contractInfo);
    }

    private ContractSettings contractSettings(ContractInfo contractInfo) {
        ContractSettings contractSettings = new ContractSettings(contractInfo.getAddress(), contractInfo.getPubKey(), contractInfo.getRootHash());
        byte[] chainCodeBytes = contractInfo.getChainCode();
        // 将反编译chainCode
        String mainClassJava = DecompilerUtils.decompileMainClassFromBytes(chainCodeBytes);
        contractSettings.setChainCode(mainClassJava);
        return contractSettings;
    }

    /**
     * 初始化账本的基本配置
     *
     * @param ledgerAdminInfo
     *     账本信息
     *
     * @return
     */
    private LedgerBaseSettings initLedgerBaseSettings(LedgerAdminInfo ledgerAdminInfo) {

        LedgerMetadata ledgerMetadata = ledgerAdminInfo.getMetadata();

        LedgerBaseSettings ledgerBaseSettings = new LedgerBaseSettings();

        // 设置参与方
        ledgerBaseSettings.setParticipantNodes(ledgerAdminInfo.getParticipants());

        // 设置共识设置
        ledgerBaseSettings.setConsensusSettings(initConsensusSettings(ledgerAdminInfo));

        // 设置参与方根Hash
        ledgerBaseSettings.setParticipantsHash(ledgerMetadata.getParticipantsHash());

        // 设置算法配置
        ledgerBaseSettings.setCryptoSetting(ledgerAdminInfo.getSettings().getCryptoSetting());

        // 设置种子
        ledgerBaseSettings.setSeed(initSeed(ledgerMetadata.getSeed()));

        // 设置共识协议
        ledgerBaseSettings.setConsensusProtocol(ledgerAdminInfo.getSettings().getConsensusProvider());

        return ledgerBaseSettings;
    }

    /**
     * 初始化账本种子信息
     *
     * @param seedBytes
     *     种子的字节数组显示
     * @return
     *     种子以十六进制方式显示，为方便阅读，每隔八个字符中间以"-"分割
     */
    private String initSeed(byte[] seedBytes) {
        String seedString = HexUtils.encode(seedBytes);
        // 每隔八个字符中加入一个一个横线
        StringBuffer seed = new StringBuffer();

        for( int i = 0; i < seedString.length(); i++) {
            char c = seedString.charAt(i);
            if (i != 0 && i % 8 == 0) {
                seed.append("-");
            }
            seed.append(c);
        }

        return seed.toString();
    }

    /**
     * 初始化共识配置
     *
     * @param ledgerAdminInfo
     *     账本元数据
     * @return
     */
    private ConsensusSettings initConsensusSettings(LedgerAdminInfo ledgerAdminInfo) {
        String consensusProvider = ledgerAdminInfo.getSettings().getConsensusProvider();
        ConsensusProvider provider = ConsensusProviders.getProvider(consensusProvider);
        byte[] consensusSettingsBytes = ledgerAdminInfo.getSettings().getConsensusSetting().toBytes();
        return provider.getSettingsFactory().getConsensusSettingsEncoder().decode(consensusSettingsBytes);
    }
}
