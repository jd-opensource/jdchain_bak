package com.jd.blockchain.sdk.client;

import java.io.Serializable;

import com.jd.blockchain.utils.net.NetworkAddress;

/**
 * 网关接入信息；
 * 
 * @author huanghaiquan
 *
 */
public class GatewayIncomingInfo implements Serializable {

	private static final long serialVersionUID = 981081410237759756L;

	private String sessionId;

	private NetworkAddress queryServiceAddress;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public NetworkAddress getQueryServiceAddress() {
		return queryServiceAddress;
	}

	public void setQueryServiceAddress(NetworkAddress queryServiceAddress) {
		this.queryServiceAddress = queryServiceAddress;
	}

}
