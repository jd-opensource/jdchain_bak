package com.jd.blockchain.contract.engine;

import com.jd.blockchain.utils.Bytes;

/**
 * 合约引擎；
 * 
 * @author huanghaiquan
 *
 */
public interface ContractEngine {

	/**
	 * 返回指定地址的合约代码；<br>
	 * 
	 * 如果不存在，则返回 null；
	 * 
	 * @param address
	 * @return
	 */
	ContractCode getContract(Bytes address, long version);

	/**
	 * 装入合约代码；<br>
	 * 
	 * 如果已经存在，则直接返回已有实例；
	 * 
	 * @param address
	 * @param codeBytes
	 * @return
	 */
	ContractCode setupContract(Bytes address, long version, byte[] codeBytes);

}
