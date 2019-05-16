//package com.jd.blockchain.gateway.service;
//
//import com.jd.blockchain.crypto.HashDigest;
//import com.jd.blockchain.gateway.PeerService;
//import com.jd.blockchain.ledger.ParticipantNode;
//import com.jd.blockchain.utils.QueryUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import java.util.Arrays;
//
//import static com.jd.blockchain.utils.BaseConstant.QUERY_LIST_MAX;
//
///**
// * @Author zhaogw
// * @Date 2019/2/22 10:39
// */
//@Component
//public class GatewayQueryServiceImpl implements GatewayQueryService {
//    @Autowired
//    private PeerService peerService;
//
//    @Override
//    public HashDigest[] getLedgersHash(int fromIndex, int count) {
//        HashDigest ledgersHash[] = peerService.getQueryService().getLedgerHashs();
//        int indexAndCount[] = QueryUtil.calFromIndexAndCount(fromIndex,count,ledgersHash.length);
//        HashDigest ledgersHashNew[] = Arrays.copyOfRange(ledgersHash,indexAndCount[0],indexAndCount[0]+indexAndCount[1]);
//        return ledgersHashNew;
//    }
//
//    @Override
//    public ParticipantNode[] getConsensusParticipants(HashDigest ledgerHash, int fromIndex, int count) {
//        ParticipantNode participantNode[] = peerService.getQueryService().getConsensusParticipants(ledgerHash);
//        int indexAndCount[] = QueryUtil.calFromIndexAndCount(fromIndex,count,participantNode.length);
//        ParticipantNode participantNodesNew[] = Arrays.copyOfRange(participantNode,indexAndCount[0],indexAndCount[0]+indexAndCount[1]);
//        return participantNodesNew;
//    }
//}
