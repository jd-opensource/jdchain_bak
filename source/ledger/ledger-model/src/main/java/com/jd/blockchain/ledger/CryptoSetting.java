package com.jd.blockchain.ledger;

import com.jd.blockchain.base.data.TypeCodes;
import com.jd.blockchain.binaryproto.DataContract;
import com.jd.blockchain.binaryproto.DataField;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.utils.ValueType;

/**
 * 默克尔树算法相关的配置；
 * 
 * @author huanghaiquan
 *
 */
@DataContract(code= TypeCodes.METADATA_CRYPTO_SETTING)
public interface CryptoSetting {

	/**
	 * 系统中使用的 Hash 算法； <br>
	 * 
	 * 对于历史数据，如果它未发生更改，则总是按照该数据产生时采用的算法进行校验，即使当时指定的Hash算法和当前的不同；<br>
	 * 
	 * 如果对数据进行了更新，则采用新的 Hash 算法来计算生成完整性证明；
	 * 
	 * @return
	 */
	@DataField(order=1, refEnum=true)
	public CryptoAlgorithm getHashAlgorithm();
	
	/**
	 * 当有完整性证明的数据被从持久化介质中加载时，是否对其进行完整性校验（重新计算 hash 比对是否一致）； <br>
	 * 
	 * 如果为 true ，则自动进行校验，如果校验失败，会引发异常； <br>
	 * 
	 * 注意：开启此选项将对性能会产生负面影响，因此使用者需要在性能和数据安全性之间做出权衡；
	 * 
	 * @return
	 */
	@DataField(order=2, primitiveType= ValueType.BOOLEAN)
	public boolean getAutoVerifyHash();

}
