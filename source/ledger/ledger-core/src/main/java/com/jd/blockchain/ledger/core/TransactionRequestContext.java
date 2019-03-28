package com.jd.blockchain.ledger.core;

import java.util.Set;

import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.utils.Bytes;

/**
 * 交易请求上下文；
 * 
 * @author huanghaiquan
 *
 */
public interface TransactionRequestContext {

	/**
	 * 交易请求；
	 * 
	 * @return
	 */
	TransactionRequest getRequest();

	/**
	 * 签名发起请求的终端用户的地址列表；
	 * 
	 * @return
	 */
	Set<Bytes> getEndpoints();

	/**
	 * 签名发起请求的节点的地址列表；
	 * 
	 * @return
	 */
	Set<Bytes> getNodes();
	
	/**
	 * 请求的终端发起人列表中是否包含指定地址的终端用户；
	 * 
	 * @param address
	 * @return
	 */
	boolean containsEndpoint(Bytes address);
	
	/**
	 * 请求的经手节点列表中是否包含指定地址的节点；
	 * 
	 * @param address
	 * @return
	 */
	boolean containsNode(Bytes address);

	/**
	 * 获取交易请求中指定地址的终端的签名；
	 * 
	 * @param address
	 * @return
	 */
	DigitalSignature getEndpointSignature(Bytes address);

	/**
	 * 获取交易请求中指定地址的节点的签名；
	 * 
	 * @param address
	 * @return
	 */
	DigitalSignature getNodeSignature(Bytes address);

}
