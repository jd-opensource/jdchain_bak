package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.utils.Bytes;

@DataContract(code = DataCodes.METADATA_LEDGER_SETTING)
public interface LedgerSettings {

    @DataField(order=0, primitiveType=PrimitiveType.TEXT)
    String getConsensusProvider();

    @DataField(order=1, primitiveType=PrimitiveType.BYTES)
    Bytes getConsensusSetting();

    @DataField(order=2, refContract=true)
    CryptoSetting getCryptoSetting();

}