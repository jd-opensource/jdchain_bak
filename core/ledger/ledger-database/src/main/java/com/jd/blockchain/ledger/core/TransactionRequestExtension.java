package com.jd.blockchain.ledger.core;

import java.util.Collection;
import java.util.Set;

import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainIdentityData;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.utils.Bytes;

/**
 * 交易请求上下文；
 * 
 * @author huanghaiquan
 *
 */
public interface TransactionRequestExtension extends TransactionRequest {

//	/**
//	 * 交易请求；
//	 * 
//	 * @return
//	 */
//	TransactionRequest getRequest();

	/**
	 * 签名发起请求的终端用户的地址列表；
	 * 
	 * @return
	 */
	Set<Bytes> getEndpointAddresses();

	/**
	 * 签名发起请求的终端用户列表；
	 * 
	 * @return
	 */
	Collection<Credential> getEndpoints();

	/**
	 * 签名发起请求的节点的地址列表；
	 * 
	 * @return
	 */
	Set<Bytes> getNodeAddresses();

	/**
	 * 签名发起请求的节点列表；
	 * 
	 * @return
	 */
	Collection<Credential> getNodes();

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

	public static class Credential {

		private final BlockchainIdentity identity;

		private final DigitalSignature signature;

		Credential(DigitalSignature signature) {
			this.identity = new BlockchainIdentityData(signature.getPubKey());
			this.signature = signature;
		}

		public Bytes getAddress() {
			return identity.getAddress();
		}
		
		public PubKey getPubKey() {
			return identity.getPubKey();
		}

		public BlockchainIdentity getIdentity() {
			return identity;
		}

		public DigitalSignature getSignature() {
			return signature;
		}
	}
}
