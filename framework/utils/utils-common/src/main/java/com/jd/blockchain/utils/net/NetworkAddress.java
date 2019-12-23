package com.jd.blockchain.utils.net;

import java.io.Serializable;
import java.util.Arrays;

import com.jd.blockchain.utils.io.BytesOutputBuffer;
import com.jd.blockchain.utils.io.BytesSerializable;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * 网络地址；
 * 
 * @author haiq
 *
 */
public class NetworkAddress implements BytesSerializable, Serializable {

	private static final long serialVersionUID = -4565279525154132393L;

	private String host;

	private int port;

	private boolean secure;

	public NetworkAddress() {
	}

	public NetworkAddress(String host, int port) {
		this(host, port, false);
	}

	public NetworkAddress(String host, int port, boolean secure) {
		this.host = host;
		this.port = port;
		this.secure = secure;
	}

	/**
	 * 用于二进制反序列化的构造器；
	 * 
	 * @param serializeBytes
	 */
	public NetworkAddress(byte[] serializeBytes) {
		secure = serializeBytes[0] == (byte) 1;
		port = BytesUtils.toInt(serializeBytes, 1);
		host = BytesUtils.toString(serializeBytes, 5);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	@Override
	public String toString() {
		return secure ? String.format("secure://%s:%s", host, port) : String.format("%s:%s", host, port);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] { host, port, secure });
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof NetworkAddress) {
			NetworkAddress other = (NetworkAddress) obj;
			return this.host.equals(other.host) && this.port == other.port && this.secure == other.secure;
		}
		return false;
	}

	@Override
	public byte[] toBytes() {
		BytesOutputBuffer buffer = new BytesOutputBuffer();
		byte[] bf1 = new byte[5];
		bf1[0] = secure ? (byte) 1 : (byte) 0;
		BytesUtils.toBytes(port, bf1, 1);
		buffer.write(bf1);
		buffer.write(BytesUtils.toBytes(host));
		return buffer.toBytes();
	}

}
