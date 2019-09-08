package com.jd.blockchain.ledger.core;

import java.util.Set;

import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.LedgerSecurityException;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.utils.Bytes;

/**
 * 针对特定交易请求的账本安全策略；
 * 
 * @author huanghaiquan
 *
 */
public interface SecurityPolicy {

	/**
	 * 签署交易的终端用户的地址列表；(来自{@link TransactionRequest#getEndpointSignatures()})
	 * 
	 * @return
	 */
	Set<Bytes> getEndpoints();

	/**
	 * 签署交易的节点参与方的地址列表(来自{@link TransactionRequest#getNodeSignatures()})
	 * 
	 * @return
	 */
	Set<Bytes> getNodes();

	/**
	 * 终端身份是否合法；
	 * 
	 * @param midPolicy
	 * @return
	 */
	boolean isEndpointValid(MultiIDsPolicy midPolicy);

	/**
	 * 节点身份是否合法；
	 * 
	 * @param midPolicy
	 * @return
	 */
	boolean isNodeValid(MultiIDsPolicy midPolicy);

	/**
	 * 检查签署交易的终端用户(来自{@link TransactionRequest#getEndpointSignatures()})是否被授权了参数指定的权限；<br>
	 * 
	 * @param permission 要检查的权限；
	 * @param midPolicy  针对多个签名用户的权限策略；
	 * @return 返回 true 表示获得授权； 返回 false 表示未获得授权；
	 */
	boolean isEndpointEnable(LedgerPermission permission, MultiIDsPolicy midPolicy);

	/**
	 * 检查签署交易的终端用户(来自{@link TransactionRequest#getEndpointSignatures()})是否被授权了参数指定的权限；<br>
	 * 
	 * @param permission 要检查的权限；
	 * @param midPolicy  针对多个签名用户的权限策略；
	 * @return 返回 true 表示获得授权； 返回 false 表示未获得授权；
	 */
	boolean isEndpointEnable(TransactionPermission permission, MultiIDsPolicy midPolicy);

	/**
	 * 检查签署交易的节点参与方(来自{@link TransactionRequest#getNodeSignatures()})是否被授权了参数指定的权限；<br>
	 * 
	 * @param permission 要检查的权限；
	 * @param midPolicy  针对多个签名用户的权限策略；
	 * @return 返回 true 表示获得授权； 返回 false 表示未获得授权；
	 */
	boolean isNodeEnable(LedgerPermission permission, MultiIDsPolicy midPolicy);

	/**
	 * 检查签署交易的节点参与方(来自{@link TransactionRequest#getNodeSignatures()})是否被授权了参数指定的权限；<br>
	 * 
	 * @param permission 要检查的权限；
	 * @param midPolicy  针对多个签名用户的权限策略；
	 * @return 返回 true 表示获得授权； 返回 false 表示未获得授权；
	 */
	boolean isNodeEnable(TransactionPermission permission, MultiIDsPolicy midPolicy);

	/**
	 * 检查终端身份的合法性；
	 * 
	 * @param midPolicy
	 * @throws LedgerSecurityException
	 */
	void checkEndpointValidity(MultiIDsPolicy midPolicy) throws LedgerSecurityException;
	
	/**
	 * 检查节点身份的合法性；
	 * 
	 * @param midPolicy
	 * @throws LedgerSecurityException
	 */
	void checkNodeValidity(MultiIDsPolicy midPolicy) throws LedgerSecurityException;

	/**
	 * 检查签署交易的终端用户(来自{@link TransactionRequest#getEndpointSignatures()})是否被授权了参数指定的权限；<br>
	 * 如果未获授权，方法将引发 {@link LedgerSecurityException} 异常；
	 * 
	 * @param permission 要检查的权限；
	 * @param midPolicy  针对多个签名用户的权限策略；
	 * @throws LedgerSecurityException
	 */
	void checkEndpointPermission(LedgerPermission permission, MultiIDsPolicy midPolicy) throws LedgerSecurityException;

	/**
	 * 检查签署交易的终端用户(来自{@link TransactionRequest#getEndpointSignatures()})是否被授权了参数指定的权限；<br>
	 * 如果未获授权，方法将引发 {@link LedgerSecurityException} 异常；
	 * 
	 * @param permission
	 * @param midPolicy
	 * @throws LedgerSecurityException
	 */
	void checkEndpointPermission(TransactionPermission permission, MultiIDsPolicy midPolicy)
			throws LedgerSecurityException;

	/**
	 * 检查签署交易的节点参与方(来自{@link TransactionRequest#getNodeSignatures()})是否被授权了参数指定的权限；<br>
	 * 如果未获授权，方法将引发 {@link LedgerSecurityException} 异常；
	 * 
	 * @param permission
	 * @param midPolicy
	 * @throws LedgerSecurityException
	 */
	void checkNodePermission(LedgerPermission permission, MultiIDsPolicy midPolicy) throws LedgerSecurityException;

	/**
	 * 检查签署交易的节点参与方(来自{@link TransactionRequest#getNodeSignatures()})是否被授权了参数指定的权限；<br>
	 * 如果未获授权，方法将引发 {@link LedgerSecurityException} 异常；
	 * 
	 * @param permission
	 * @param midPolicy
	 * @throws LedgerSecurityException
	 */
	void checkNodePermission(TransactionPermission permission, MultiIDsPolicy midPolicy) throws LedgerSecurityException;

}
