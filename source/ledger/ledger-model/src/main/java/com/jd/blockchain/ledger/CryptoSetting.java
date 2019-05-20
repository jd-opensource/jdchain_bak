package com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.binaryproto.PrimitiveType;
import com.jd.blockchain.consts.DataCodes;
import com.jd.blockchain.crypto.CryptoProvider;

/**
 * 默克尔树算法相关的配置；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code = DataCodes.METADATA_CRYPTO_SETTING)
public interface CryptoSetting {

	/**
	 * 系统支持的密码服务提供者；
	 * 
	 * @return
	 */
	@DataField(order = 0, refContract = true, list = true)
	public CryptoProvider[] getSupportedProviders();
	
	

	/**
	 * 系统中使用的 Hash 算法； <br>
	 * 
	 * 对于历史数据，如果它未发生更改，则总是按照该数据产生时采用的算法进行校验，即使当时指定的Hash算法和当前的不同；<br>
	 * 
	 * 如果对数据进行了更新，则采用新的 Hash 算法来计算生成完整性证明；
	 * 
	 * @return
	 */
	@DataField(order = 1, primitiveType = PrimitiveType.INT16)
	public short getHashAlgorithm();

	/**
	 * 当有加载附带哈希摘要的数据时，是否重新计算哈希摘要进行完整性校验； <br>
	 * 
	 * 如果为 true ，则自动进行校验，如果校验失败，会引发异常； <br>
	 * 
	 * 注意：开启此选项将对性能会产生负面影响，因此使用者需要在性能和数据安全性之间做出权衡；
	 * 
	 * @return
	 */
	@DataField(order = 2, primitiveType = PrimitiveType.BOOLEAN)
	public boolean getAutoVerifyHash();
	

}
