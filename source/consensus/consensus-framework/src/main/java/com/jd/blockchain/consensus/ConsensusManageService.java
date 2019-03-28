package com.jd.blockchain.consensus;

/**
 * 共识节点的管理服务；
 * 
 * @author huanghaiquan
 *
 */
public interface ConsensusManageService {

	/**
	 * 对客户端的接入进行认证；
	 * 
	 * @param authId
	 *            客户端的身份信息；
	 * @return 如果通过认证，则返回接入参数；如果认证失败，则返回 null；
	 */
	ClientIncomingSettings authClientIncoming(ClientIdentification authId) throws ConsensusSecurityException;

}
