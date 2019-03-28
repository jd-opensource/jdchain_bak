//package test.com.jd.blockchain.binaryproto.contract;
//
//import com.jd.blockchain.binaryproto.DataContract;
//import com.jd.blockchain.binaryproto.DataField;
//import com.jd.blockchain.binaryproto.ValueType;
//
///**
// * Created by zhangshuang3 on 2018/7/30.
// */
//@DataContract(code=0x0b, name="LedgerMetadata", description = "Ledger meta data")
//public interface LedgerMetadata {
//
//    @DataField(order=1, primitiveType= ValueType.INT8, list=true)
//    byte[] getSeed();
//
//    @DataField(order = 2, refContract=true)
//    LedgerSetting getSetting();
//
//    @DataField(order=3, primitiveType=ValueType.INT8, list=true)
//    byte[] getPrivilegesHash();
//
//    @DataField(order=4, primitiveType=ValueType.INT8, list=true)
//    byte[] getParticipantsHash();
//
//}