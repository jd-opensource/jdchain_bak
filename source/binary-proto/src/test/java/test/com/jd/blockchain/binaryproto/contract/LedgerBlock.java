//package test.com.jd.blockchain.binaryproto.contract;
//
//import com.jd.blockchain.binaryproto.DataContract;
//import com.jd.blockchain.binaryproto.DataField;
//import com.jd.blockchain.binaryproto.ValueType;
//import com.jd.blockchain.crypto.hash.HashDigest;
//
///**
// * Created by zhangshuang3 on 2018/7/30.
// */
//@DataContract(code=0x12, name="LedgerBlock", description ="LedgerBlock")
//public interface LedgerBlock extends LedgerDataSnapshot{
//
//    @DataField(order=1, refHashDigest=true)
//    HashDigest getHash();
//
//    @DataField(order=2, refHashDigest=true)
//    HashDigest getPreviousHash();
//
//    @DataField(order=3, refHashDigest=true)
//    HashDigest getLedgerHash();
//
//    @DataField(order=4, primitiveType=ValueType.INT64)
//    long getHeight();
//
//    @DataField(order=5, refHashDigest=true)
//    HashDigest getTransactionSetHash();
//}