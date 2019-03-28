//package test.com.jd.blockchain.binaryproto.contract;
//
//import com.jd.blockchain.binaryproto.DataContract;
//import com.jd.blockchain.binaryproto.DataField;
//import com.jd.blockchain.crypto.hash.HashDigest;
//
///**
// * Created by zhangshuang3 on 2018/7/30.
// */
//@DataContract(code=0x11, name="LedgerDataSnapshot", description ="LedgerDataSnapshot")
//public interface LedgerDataSnapshot {
//
//    @DataField(order=1, refHashDigest=true)
//    HashDigest getAdminAccountHash();
//
//    @DataField(order=2, refHashDigest=true)
//    HashDigest getUserAccountSetHash();
//
//    @DataField(order=3, refHashDigest=true)
//    HashDigest getUserPrivilegeHash();
//
//    @DataField(order=4, refHashDigest=true)
//    HashDigest getDataAccountSetHash();
//
//    @DataField(order=5, refHashDigest=true)
//    HashDigest getDataPrivilegeHash();
//
//    @DataField(order=6, refHashDigest=true)
//    HashDigest getContractAccountSetHash();
//
//    @DataField(order=7, refHashDigest=true)
//    HashDigest getContractPrivilegeHash();
//
//}
