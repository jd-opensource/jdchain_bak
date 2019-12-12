package com.jd.blockchain.transaction;


import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.MagicNumber;

/**
 * 数字签名的字节块；
 * 
 * <pre>
 * 字节位如下：
 * [第1字节]：标识数据类型为数字签名的魔数常量 ({@link MagicNumber#SIGNATURE})；
 * 
 * [第2字节] - [第N字节]: 公钥；
 * 		注：公钥的值是包含了公钥算法标识和公钥内容的字节码编码;
 * 
 * [第N+1字节] - 结束: 摘要；
 * 
 * </pre>
 * 
 * @author huanghaiquan
 *
 */
public class DigitalSignatureBlob implements DigitalSignature { 

	private PubKey pubKey;

	private SignatureDigest digest;

	@Override
	public PubKey getPubKey() {
		return pubKey;
	}

	@Override
	public SignatureDigest getDigest() {
		return digest;
	}
	
	public DigitalSignatureBlob() {
	}
	
	public DigitalSignatureBlob(PubKey pubKey, SignatureDigest digest) {
		this.pubKey = pubKey;
		this.digest = digest;
	}
	

//	@Override
//	public void resolvFrom(InputStream in) {
//		try {
//			byte[] buff = new byte[1];
//			int len = in.read(buff);
//			if (len < 1) {
//				throw new IllegalArgumentException("No enough bytes was read for the magic number [SIGNATURE]!");
//			}
//			if (buff[0] != MagicNumber.SIGNATURE) {
//				throw new IllegalArgumentException("Magic number [SIGNATURE] dismatch!");
//			}
//			PubKey pk = CryptoKeyEncoding.readPubKey(in);
//			ByteArray dg = BytesEncoding.readAsByteArray(NumberMask.SHORT, in);
//			this.pubKey = pk;
//			this.digest = dg;
//		} catch (IOException e) {
//			throw new RuntimeIOException(e.getMessage(), e);
//		}
//	}
//
//	@Override
//	public void writeTo(OutputStream out) {
//		try {
//			out.write(MagicNumber.SIGNATURE);
//			CryptoKeyEncoding.writeKey(pubKey, out);
//			BytesEncoding.write(digest, NumberMask.SHORT, out);
//		} catch (IOException e) {
//			throw new RuntimeIOException(e.getMessage(), e);
//		}
//	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (!(o instanceof DigitalSignatureBlob)) return false;
//		DigitalSignatureBlob that = (DigitalSignatureBlob) o;
//		return Objects.equals(getPubKey(), that.getPubKey()) &&
//				Objects.equals(getDigest(), that.getDigest());
//	}
//
//	public byte[] toBytes() {
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		writeTo(out);
//		return out.toByteArray();
//	}
//
//	@Override
//	public String toString() {
//		return toBytes().toString();
//	}
//
//	@Override
//	public void writeExternal(ObjectOutput out) throws IOException {
//		byte[] bts = toBytes();
//		out.writeInt(bts.length);
//		out.write(bts, 0, bts.length);
//	}
//
//	@Override
//	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//		int size = in.readInt();
//		byte[] bs = new byte[size];
//		in.readFully(bs, 0, size);
//		resolvFrom(new ByteArrayInputStream(bs));
//	}
}
