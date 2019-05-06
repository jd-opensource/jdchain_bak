package com.jd.blockchain.transaction;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.MagicNumber;
import com.jd.blockchain.ledger.NodeRequest;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.utils.Bytes;

/**
 * TxRequestMessage 交易消息；
 * <p>
 * 
 * TxRequestMessage 表示参与者提交的交易请求，由3部分组成：交易内容、参与者签名、网关节点签名；<br>
 * 
 * <pre>
 * 字节位如下：
 * [第1字节]：标识数据类型为交易请求的魔数常量 ({@link MagicNumber#TX_REQUEST})；
 * 
 * [第2字节] - [第N字节]: 交易内容；
 * 
 * [第N+1字节]: 交易参与者数量(有效值范围 0 - 255)；
 *         注：在单个交易中，参与者的数量总是有限的，对于那些需要更多参与者且数量不确定的场景，可以通过合约来实现把参与者分为多次 TX 提交，最终组合完成一次完整的业务进程；
 * [第N+2字节] - [第X字节]: 参与者的签名列表；
 * 
 * [第X+1字节]：对交易请求的哈希算法的代码；
 * [第X+2字节] - [第Y字节]：对交易请求的哈希值；针对交易请求中此段之前的全部内容进行哈希计算，包括：交易请求魔数、交易内容、签名者列表、哈希算法代码；
 * 
 * [第Y+1字节] - 结束: 网关节点针对交易请求的签名；
 * </pre>
 * 
 * @author huanghaiquan
 *
 */
public class TxRequestMessage implements TransactionRequest {// , Externalizable {

	/**
	 * 交易参与者的个数的最大值；
	 */
	public static final int MAX_TX_PARTICIPANT_COUNT = 0xFF;

	private HashDigest hash;

	private TransactionContent transactionContent;

	private Map<Bytes, DigitalSignature> endpointSignatureMap = new LinkedHashMap<>();

	private Map<Bytes, DigitalSignature> nodeSignatureMap = new LinkedHashMap<>();

	// private CryptoAlgorithm defaultHashAlgorithm = CryptoAlgorithm.SHA_256;

	// public TxRequestMessage() {
	// }

	static {
		DataContractRegistry.register(NodeRequest.class);
	}

	public TxRequestMessage(TransactionContent txContent) {
		// if (!(txContent instanceof BytesWriter)) {
		// throw new IllegalArgumentException("The tx content must be instance of
		// BytesWriter!");
		// }
		this.transactionContent = txContent;
	}

	public TxRequestMessage(TransactionRequest txRequest) {
		this.transactionContent = txRequest.getTransactionContent();
		setHash(txRequest.getHash());
		setEndpointSignatures(txRequest.getEndpointSignatures());
		setNodeSignatures(txRequest.getNodeSignatures());
	}

	@Override
	public TransactionContent getTransactionContent() {
		return this.transactionContent;
	}

	@Override
	public DigitalSignature[] getEndpointSignatures() {
		return endpointSignatureMap.values().toArray(new DigitalSignature[endpointSignatureMap.size()]);
	}

	@Override
	public DigitalSignature[] getNodeSignatures() {
		return nodeSignatureMap.values().toArray(new DigitalSignature[nodeSignatureMap.size()]);
	}

	public void setEndpointSignatures(Object[] endpointSignatures) {
		if (endpointSignatures != null) {
			for (Object object : endpointSignatures) {
				DigitalSignature endpointSignature = (DigitalSignature) object;
				addEndpointSignatures(endpointSignature);
			}
		}
		return;
	}

	public void setNodeSignatures(Object[] nodeSignatures) {
		if (nodeSignatures != null) {
			for (Object object : nodeSignatures) {
				DigitalSignature nodeSignature = (DigitalSignature) object;
				addNodeSignatures(nodeSignature);
			}
		}
		return;
	}

	private void doAddEndpointSignature(DigitalSignature signature) {
		Bytes address = AddressEncoding.generateAddress(signature.getPubKey());
		if (endpointSignatureMap.containsKey(address)) {
			throw new IllegalArgumentException(
					String.format("Participant signature of Address[%s] already exist!", address));
		}
		endpointSignatureMap.put(address, signature);
	}

	/**
	 * 从参与者签名列表中检查是否包含指定的参与者；
	 * 
	 * @param userBid
	 *            参与者的身份；
	 * @return
	 */
	public boolean containsEndpointSignature(BlockchainIdentity userBid) {
		return endpointSignatureMap.containsKey(userBid.getAddress());
	}

	public boolean containsEndpointSignature(Bytes userAddress) {
		return endpointSignatureMap.containsKey(userAddress);
	}

	public void addEndpointSignatures(DigitalSignature... signature) {
		for (DigitalSignature sign : signature) {
			doAddEndpointSignature(sign);
		}
	}

	public void addEndpointSignatures(List<DigitalSignature> signature) {
		for (DigitalSignature sign : signature) {
			doAddEndpointSignature(sign);
		}
	}

	/**
	 * 从节点签名列表中检查是否包含指定的节点；
	 * 
	 * @param nodeBid
	 *            节点的身份；
	 * @return
	 */
	public boolean containsNodeSignature(BlockchainIdentity nodeBid) {
		return nodeSignatureMap.containsKey(nodeBid.getAddress());
	}

	public boolean containsNodeSignature(Bytes nodeAddress) {
		return nodeSignatureMap.containsKey(nodeAddress);
	}

	private void doAddNodeSignatures(DigitalSignature signature) {
		Bytes address = AddressEncoding.generateAddress(signature.getPubKey());
		if (nodeSignatureMap.containsKey(address)) {
			throw new IllegalArgumentException(String.format("Node signature of Address[%s] already exist!", address));
		}
		nodeSignatureMap.put(address, signature);
	}

	public void addNodeSignatures(DigitalSignature... signature) {
		for (DigitalSignature sign : signature) {
			doAddNodeSignatures(sign);
		}
	}

	public void addNodeSignatures(List<DigitalSignature> signature) {
		for (DigitalSignature sign : signature) {
			doAddNodeSignatures(sign);
		}
	}

	@Override
	public HashDigest getHash() {
		return hash;
	}

	public void setHash(HashDigest hash) {
		this.hash = hash;
	}

	// public HashDigest updateHash() {
	// return computeHash(this.defaultHashAlgorithm);
	// }

	// public HashDigest updateHash(CryptoAlgorithm hashAlgorithm) {
	// return computeHash(hashAlgorithm);
	// }
	//
	// private HashDigest computeHash(CryptoAlgorithm hashAlgorithm) {
	// byte[] reqBody = getRequestBody();
	// this.hash = CryptoUtils.hash(hashAlgorithm).hash(reqBody);
	// return this.hash;
	// }

	// @Override
	// public void resolvFrom(InputStream in) throws IOException {
	// // 解析校验交易请求魔数；
	// byte[] buff = new byte[1];
	// int len = in.read(buff, 0, 1);
	// if (len < 1) {
	// throw new IllegalArgumentException("No bytes was read for the magic number
	// [TX_REQUEST]!");
	// }
	// if (MagicNumber.TX_REQUEST != buff[0]) {
	// throw new IllegalArgumentException("Magic number [TX_REQUEST] dismatch!");
	// }
	//
	// // 解析交易内容；
	// TxContentBlob txContentBlob = new TxContentBlob();
	// txContentBlob.resolvFrom(in);
	//
	// // 解析参与者签名列表；
	// int participantCount = NumberMask.TINY.resolveMaskedNumber(in);
	// List<DigitalSignature> partiSignList = new ArrayList<>();
	// for (int i = 0; i < participantCount; i++) {
	// DigitalSignatureBlob signature = new DigitalSignatureBlob();
	// signature.resolvFrom(in);
	//
	// partiSignList.add(signature);
	// }
	//
	// // 解析节点签名列表；
	// int nodeCount = NumberMask.TINY.resolveMaskedNumber(in);
	// List<DigitalSignature> nodeSignList = new ArrayList<>();
	// for (int i = 0; i < nodeCount; i++) {
	// DigitalSignatureBlob nodeSign = new DigitalSignatureBlob();
	// nodeSign.resolvFrom(in);
	// nodeSignList.add(nodeSign);
	// }
	//
	// // 解析哈希算法标识符；
	// HashAlgorithm hashAlgorithm = HashAlgorithm.valueOf((byte) in.read());
	//
	// // 解析原始的哈希；
	// ByteArray hash = HashEncoding.read(in);
	//
	// this.txContent = txContentBlob;
	// addParticipantSignatures(partiSignList);
	// addNodeSignatures(nodeSignList);
	// this.hash = hash;
	//
	// // 校验原始哈希；
	// byte[] bodyBytes = getRequestBody();
	// ByteArray rHash = HashEncoding.computeHash(bodyBytes, hashAlgorithm);
	// if (!rHash.equals(hash)) {
	// throw new IllegalArgumentException("The hash is not match with request
	// content!");
	// }
	// }
	//
	// /**
	// * 输出交易请求消息；
	// *
	// * 注：此方法不会自动重新计算hash；如果消息的内容发生改变后，需要调用主动调用 {@link #updateHash()} 方法重新计算 hash；
	// */
	// @Override
	// public void writeTo(OutputStream out) throws IOException {
	// if (this.hash == null) {
	// updateHash();
	// }
	//
	// buildRequestBody(out);
	//
	// // 写入 hash 值；
	// HashEncoding.write(hash, out);
	// }

	// /**
	// * 生成请求体，包括：交易请求魔数、交易内容、参与者签名者列表、哈希算法代号；
	// *
	// * @param out
	// * @throws IOException
	// */
	// private void buildRequestBody(OutputStream out) throws IOException {
	//
	// buildParticipantRequest(out);
	//
	// // 写入节点签名列表；
	// NumberMask.TINY.writeMask(nodeSignatureMap.size(), out);
	// for (DigitalSignature nodeSignatureBlob : nodeSignatureMap.values()) {
	// nodeSignatureBlob.writeTo(out);
	// }
	//
	// // 写入 hash 算法代号；
	// out.write(hashAlgorithm.getAlgorithm());
	// }

	// /**
	// * 生成参与者的请求数据；
	// *
	// * <br>
	// * 参与者的请求数据仅包含“交易请求模数({@link MagicNumber#TX_REQUEST })”
	// * “交易内容({@link #getTransactionContent()})”
	// * 和“参与者签名列表({@link #getParticipantSignatures()})”三项属性；
	// *
	// * @param out
	// */
	// public void buildParticipantRequest(OutputStream out) {
	// try {
	// // 写入魔数；
	// out.write(MagicNumber.TX_REQUEST);
	//
	// // 写入交易内容；
	// txContent.writeTo(out);
	//
	// // 写入 1 个字节的参与者签名数量；
	// if (participantSignatureMap.size() > MAX_TX_PARTICIPANT_COUNT) {
	// throw new IllegalArgumentException("The number of participant signatures is
	// out of the max count["
	// + MAX_TX_PARTICIPANT_COUNT + "]!");
	// }
	//
	// NumberMask.TINY.writeMask(participantSignatureMap.size(), out);
	// // 写入参与者签名列表；
	// for (DigitalSignature digitalSignatureBlob :
	// participantSignatureMap.values()) {
	// digitalSignatureBlob.writeTo(out);
	// }
	//
	// } catch (IOException e) {
	// throw new RuntimeIOException(e.getMessage(), e);
	// }
	// }
	//
	// @Override
	// public void writeExternal(ObjectOutput out) throws IOException {
	// ByteArrayOutputStream os = new ByteArrayOutputStream();
	// writeTo(os);
	// byte[] bts = os.toByteArray();
	// out.writeInt(bts.length);
	// out.write(bts);
	// }
	//
	// @Override
	// public void readExternal(ObjectInput in) throws IOException,
	// ClassNotFoundException {
	// int len = in.readInt();
	// byte[] bts = new byte[len];
	// in.readFully(bts);
	// this.resolvFrom(new ByteArrayInputStream(bts));
	// }

	// @Override
	// public byte[] toBytes() {
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	// try {
	// writeTo(out);
	// } catch (IOException e) {
	// throw new RuntimeIOException(e.getMessage(), e);
	// }
	// return out.toByteArray();
	// }

	// @Override
	// public ByteArray getHashData() {
	// return ByteArray.wrap(getRequestBody());
	// }

	// private byte[] getRequestBody() {
	// try {
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	// buildRequestBody(out);
	//
	// return out.toByteArray();
	// } catch (IOException e) {
	// throw new RuntimeIOException(e.getMessage(), e);
	// }
	// }
}
