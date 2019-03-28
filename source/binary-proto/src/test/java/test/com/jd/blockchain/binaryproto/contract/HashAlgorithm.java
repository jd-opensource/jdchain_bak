//package test.com.jd.blockchain.binaryproto.contract;
//
//import com.jd.blockchain.binaryproto.EnumContract;
//import com.jd.blockchain.binaryproto.EnumField;
//import com.jd.blockchain.binaryproto.ValueType;
//
///**
// * Created by zhangshuang3 on 2018/7/30.
// */
//@EnumContract(code=0x0101)
//public enum HashAlgorithm {
//
//    RIPE160((byte) 1),
//
//    SHA256((byte) 2),
//
//    SM3((byte) 4);
//
//    @EnumField(type = ValueType.INT8)
//    public final byte CODE;
//
//    private HashAlgorithm(byte algorithm) {
//        CODE = algorithm;
//    }
//
//    public byte getAlgorithm() {
//        return CODE;
//    }
//
//    public static HashAlgorithm valueOf(byte algorithm) {
//        for (HashAlgorithm hashAlgorithm : HashAlgorithm.values()) {
//            if (hashAlgorithm.CODE == algorithm) {
//                return hashAlgorithm;
//            }
//        }
//        throw new IllegalArgumentException("Unsupported hash algorithm [" + algorithm + "]!");
//    }
//
//    public static void checkHashAlgorithm(HashAlgorithm algorithm) {
//        switch (algorithm) {
//            case RIPE160:
//                break;
//            case SHA256:
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported hash algorithm [" + algorithm + "]!");
//        }
//    }
//}
//
