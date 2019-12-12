package com.jd.blockchain.transaction;
//package com.jd.blockchain.ledger.data;
//
//import com.jd.blockchain.ledger.OperationType;
//import my.utils.io.ByteArray;
//
//public class NewLedgerOpBlob {
//
//    private ByteArray genesisKey;
//
//    public NewLedgerOpBlob() {
//    }
//
//    public NewLedgerOpBlob(ByteArray genesisKey) {
//        this.genesisKey = genesisKey;
//    }
//
//    public void resolvFrom(OpBlob opBlob) {
////        if (OperationType.NEW_LEDGER.CODE != opBlob.getCode()) {
////            throw new IllegalArgumentException(
////                    "Could not resolve operation info due to NEW_LEDGER operation code mismatch!");
////        }
////
////        genesisKey = opBlob.getArg(0);
//    }
//
//    public OpBlob toBlob() {
//        // 写入操作码；
//        OpBlob opBlob = new OpBlob();
//        opBlob.setOperation(OperationType.NEW_LEDGER.CODE, genesisKey);
//
//        return opBlob;
//    }
//
//    public ByteArray getGenesisKey() {
//        return genesisKey;
//    }
//}
