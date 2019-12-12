package test.com.jd.blockchain.sdk.proxy;

//public class TxMessageTest {
//
//	@Test
//	public void testSerialize() throws IOException {
//		BlockchainKeyPair id = BlockchainKeyGenerator.getInstance().generate(CryptoKeyType.ED25519);
//		BlockchainKeyPair id1 = BlockchainKeyGenerator.getInstance().generate(CryptoKeyType.ED25519);
//		BlockchainKeyPair id2 = BlockchainKeyGenerator.getInstance().generate(CryptoKeyType.ED25519);
//
//		TxContentBlob contentBlob = new TxContentBlob();
//
//		contentBlob.setSubjectAccount(id.getAddress());
//		contentBlob.setSequenceNumber(1);
//
//		OpBlob op1 = new OpBlob();
//		op1.setOperation(OperationType.REGISTER_USER.CODE, ByteArray.parseString("AAA", "UTF-8"));
//		contentBlob.addOperation(op1);
//
//		TxRequestMessage txMsg = new TxRequestMessage(contentBlob);
//
//		ByteArray mockedDigest = ByteArray.wrap(RandomUtils.generateRandomBytes(32));
//		DigitalSignatureBlob signature = new DigitalSignatureBlob(id.getPubKey(), mockedDigest);
//		txMsg.addEndpointSignatures(signature);
//
//		ByteArray mockedDigest1 = ByteArray.wrap(RandomUtils.generateRandomBytes(32));
//		DigitalSignatureBlob signature1 = new DigitalSignatureBlob(id1.getPubKey(), mockedDigest1);
//		txMsg.addEndpointSignatures(signature1);
//
//		ByteArray mockedDigest2 = ByteArray.wrap(RandomUtils.generateRandomBytes(32));
//		DigitalSignatureBlob signature2 = new DigitalSignatureBlob(id2.getPubKey(), mockedDigest2);
//		txMsg.addNodeSignatures(signature2);
//
//		// 输出；
//		byte[] msgBytes = BinaryEncodingUtils.encode(txMsg, TransactionRequest.class);
//
//		assertEquals(MagicNumber.TX_REQUEST, msgBytes[0]);
//
////		TxRequestMessage resolvedTxMsg = new TxRequestMessage();
////		resolvedTxMsg.resolvFrom(ByteArray.wrap(msgBytes).asInputStream());
//		TxRequestMessage resolvedTxMsg = BinaryEncodingUtils.decode(msgBytes, null, TxRequestMessage.class);
//
//		assertEquals(txMsg.getTransactionContent().getSubjectAccount(),
//				resolvedTxMsg.getTransactionContent().getSubjectAccount());
//		assertEquals(txMsg.getTransactionContent().getHash(), resolvedTxMsg.getTransactionContent().getHash());
//		assertEquals(txMsg.getHash(), resolvedTxMsg.getHash());
//
//		DigitalSignature[] pSignatures = txMsg.getEndpointSignatures();
//		DigitalSignature[] resolvedPSignatures = txMsg.getEndpointSignatures();
//		assertEquals(pSignatures.length, resolvedPSignatures.length);
//		for (int i = 0; i < resolvedPSignatures.length; i++) {
//			assertEquals(pSignatures[i].getPubKey().getType(), resolvedPSignatures[i].getPubKey().getType());
//			assertEquals(pSignatures[i].getPubKey().getValue(), resolvedPSignatures[i].getPubKey().getValue());
//			assertEquals(pSignatures[i].getDigest(), resolvedPSignatures[i].getDigest());
//		}
//
//		assertEquals(txMsg.getNodeSignatures()[0].getPubKey().getType(),
//				resolvedTxMsg.getNodeSignatures()[0].getPubKey().getType());
//		assertEquals(txMsg.getNodeSignatures()[0].getPubKey().getValue(),
//				resolvedTxMsg.getNodeSignatures()[0].getPubKey().getValue());
//		assertEquals(txMsg.getNodeSignatures()[0].getDigest(), resolvedTxMsg.getNodeSignatures()[0].getDigest());
//	}
//
//}
