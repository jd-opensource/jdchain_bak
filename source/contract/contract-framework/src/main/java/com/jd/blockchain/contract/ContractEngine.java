package com.jd.blockchain.contract;

/**
 * contract engine.
 * 
 * @author huanghaiquan
 *
 */
public interface ContractEngine {

	/**
	 * Returns the contract code for the specified address;<br>
	 * 
	 * If not, return null；
	 * 
	 * @param address
	 * @return
	 */
	ContractCode getContract(String address, long version);

	/**
	 * Load contract code;<br>
	 * 
	 * If it already exists, it returns the existing instance directly.
	 * 
	 * @param address
	 * @param code
	 * @return
	 */
	ContractCode setupContract(String address, long version, byte[] code);

}
