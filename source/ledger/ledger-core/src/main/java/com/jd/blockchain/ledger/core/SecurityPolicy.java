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
	 * 检查签署交易的终端用户(来自{@link TransactionRequest#getEndpointSignatures()})是否被授权了参数指定的权限；<br>
	 * 
	 * @param permission 要检查的权限；
	 * @param midPolicy  针对多个签名用户的权限策略；
	 * @return 返回 true 表示获得授权； 返回 false 表示未获得授权；
	 */
	boolean isEnableToEndpoints(LedgerPermission permission, MultiIdsPolicy midPolicy);

	/**
	 * 检查签署交易的终端用户(来自{@link TransactionRequest#getEndpointSignatures()})是否被授权了参数指定的权限；<br>
	 * 
	 * @param permission 要检查的权限；
	 * @param midPolicy  针对多个签名用户的权限策略；
	 * @return 返回 true 表示获得授权； 返回 false 表示未获得授权；
	 */
	boolean isEnableToEndpoints(TransactionPermission permission, MultiIdsPolicy midPolicy);

	/**
	 * 检查签署交易的节点参与方(来自{@link TransactionRequest#getNodeSignatures()})是否被授权了参数指定的权限；<br>
	 * 
	 * @param permission 要检查的权限；
	 * @param midPolicy  针对多个签名用户的权限策略；
	 * @return 返回 true 表示获得授权； 返回 false 表示未获得授权；
	 */
	boolean isEnableToNodes(LedgerPermission permission, MultiIdsPolicy midPolicy);

	/**
	 * 检查签署交易的节点参与方(来自{@link TransactionRequest#getNodeSignatures()})是否被授权了参数指定的权限；<br>
	 * 
	 * @param permission 要检查的权限；
	 * @param midPolicy  针对多个签名用户的权限策略；
	 * @return 返回 true 表示获得授权； 返回 false 表示未获得授权；
	 */
	boolean isEnableToNodes(TransactionPermission permission, MultiIdsPolicy midPolicy);

	/**
	 * 检查签署交易的终端用户(来自{@link TransactionRequest#getEndpointSignatures()})是否被授权了参数指定的权限；<br>
	 * 如果未获授权，方法将引发 {@link LedgerSecurityException} 异常；
	 * 
	 * @param permission 要检查的权限；
	 * @param midPolicy  针对多个签名用户的权限策略；
	 * @throws LedgerSecurityException
	 */
	void checkEndpoints(LedgerPermission permission, MultiIdsPolicy midPolicy) throws LedgerSecurityException;

	/**
	 * 检查签署交易的终端用户(来自{@link TransactionRequest#getEndpointSignatures()})是否被授权了参数指定的权限；<br>
	 * 如果未获授权，方法将引发 {@link LedgerSecurityException} 异常；
	 * 
	 * @param permission
	 * @param midPolicy
	 * @throws LedgerSecurityException
	 */
	void checkEndpoints(TransactionPermission permission, MultiIdsPolicy midPolicy) throws LedgerSecurityException;

	/**
	 * 检查签署交易的节点参与方(来自{@link TransactionRequest#getNodeSignatures()})是否被授权了参数指定的权限；<br>
	 * 如果未获授权，方法将引发 {@link LedgerSecurityException} 异常；
	 * 
	 * @param permission
	 * @param midPolicy
	 * @throws LedgerSecurityException
	 */
	void checkNodes(LedgerPermission permission, MultiIdsPolicy midPolicy) throws LedgerSecurityException;

	/**
	 * 检查签署交易的节点参与方(来自{@link TransactionRequest#getNodeSignatures()})是否被授权了参数指定的权限；<br>
	 * 如果未获授权，方法将引发 {@link LedgerSecurityException} 异常；
	 * 
	 * @param permission
	 * @param midPolicy
	 * @throws LedgerSecurityException
	 */
	void checkNodes(TransactionPermission permission, MultiIdsPolicy midPolicy) throws LedgerSecurityException;

}
