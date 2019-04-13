package com.jd.blockchain.gateway.service;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.ParticipantNode;

/**
 * queryService only for gateway;
 * @Author zhaogw
 * @Date 2019/2/22 10:37
 */
public interface GatewayQueryService {
    /**
     * get all ledgers hashs;
     * @param fromIndex
     * @param count
     */
    HashDigest[] getLedgersHash(int fromIndex, int count);

    /**
     * get the participants by range;
     * @param ledgerHash
     * @param fromIndex
     * @param count
     * @return
     */
    ParticipantNode[] getConsensusParticipants(HashDigest ledgerHash, int fromIndex, int count);
}
