//package test.com.jd.blockchain.binaryproto.contract;
//
//import com.jd.blockchain.binaryproto.DataContract;
//import com.jd.blockchain.binaryproto.DataField;
//import com.jd.blockchain.binaryproto.ValueType;
//
///**
// * Created by zhangshuang3 on 2018/7/30.
// */
//@DataContract(code=0x0d, name="CryptoSetting", description = "Crypto setting")
//public interface CryptoSetting {
//
//    /**
//     * 系统中使用的 Hash 算法； <br>
//     *
//     * 对于历史数据，如果它未发生更改，则总是按照该数据产生时采用的算法进行校验，即使当时指定的Hash算法和当前的不同；<br>
//     *
//     * 如果对数据进行了更新，则采用新的 Hash 算法来计算生成完整性证明；
//     *
//     * @return
//     */
//    @DataField(order=1, refEnum=true)
//    public HashAlgorithm getHashAlgorithm();
//
//    @DataField(order=2, refEnum=true)
//    public CryptoAlgorithm getHashAlgorithm1();
//
//    /**
//     * 当有完整性证明的数据被从持久化介质中加载时，是否对其进行完整性校验（重新计算 hash 比对是否一致）； <br>
//     *
//     * 如果为 true ，则自动进行校验，如果校验失败，会引发异常； <br>
//     *
//     * 注意：开启此选项将对性能会产生负面影响，因此使用者需要在性能和数据安全性之间做出权衡；
//     *
//     * @return
//     */
//    @DataField(order=3, primitiveType= ValueType.BOOLEAN)
//    public boolean getAutoVerifyHash();//func name is getxxxxx type
//
//}
//
