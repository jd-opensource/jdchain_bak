package com.jd.blockchain.ledger.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.jd.blockchain.utils.io.BytesEncoding;
import com.jd.blockchain.utils.io.BytesSerializable;

/**
 * 表示赋予角色的特权码；
 * 
 * @author huanghaiquan
 *
 */
public class RolePrivilege implements BytesSerializable {

	// 权限码的数量；目前有2种：账本权限 + 交易权限；
	private static final int SEGMENT_COUNT = 2;

	private LedgerPrivilege ledgerPrivilege;

	private TxPrivilege txPrivilege;

	public Privilege<TxPermission> getTxPrivilege() {
		return txPrivilege;
	}

	public Privilege<LedgerPermission> getLedgerPrivilege() {
		return ledgerPrivilege;
	}

	public RolePrivilege(byte[] priviledgeCodes) {
		byte[][] bytesSegments = decodeBytes(priviledgeCodes);
		ledgerPrivilege = new LedgerPrivilege(bytesSegments[0]);
		txPrivilege = new TxPrivilege(bytesSegments[1]);
	}

	private byte[] encodeBytes(byte[]... bytes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// write one byte;
		out.write(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			BytesEncoding.writeInTiny(bytes[i], out);
		}
		return out.toByteArray();
	}

	private byte[][] decodeBytes(byte[] bytes) {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		// read one byte;
		int len = in.read();
		if (len < 1 || len > SEGMENT_COUNT) {
			throw new IllegalStateException("Decoded illegal privilege bytes!");
		}
		byte[][] bytesSegments = new byte[len][];
		for (int i = 0; i < bytesSegments.length; i++) {
			bytesSegments[i] = BytesEncoding.readInTiny(in);
		}
		return bytesSegments;
	}

	@Override
	public byte[] toBytes() {
		// 保持和解码时一致的顺序；
		return encodeBytes(ledgerPrivilege.toBytes(), txPrivilege.toBytes());
	}

}
