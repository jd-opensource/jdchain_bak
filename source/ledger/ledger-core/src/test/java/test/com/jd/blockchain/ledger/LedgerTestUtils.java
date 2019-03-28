package test.com.jd.blockchain.ledger;

import java.util.Random;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
import com.jd.blockchain.crypto.hash.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.impl.TransactionStagedSnapshot;
import com.jd.blockchain.ledger.data.TransactionService;
import com.jd.blockchain.ledger.data.TxTemplate;

public class LedgerTestUtils {

	// private static ThreadLocalRandom rand = ThreadLocalRandom.current();

	private static Random rand = new Random();
	
	
	public static TransactionRequest createTxRequest(HashDigest ledgerHash) {
		return createTxRequest(ledgerHash, CryptoUtils.sign(CryptoAlgorithm.ED25519));
	}
	
	public static TransactionRequest createTxRequest(HashDigest ledgerHash, SignatureFunction signatureFunction) {
		TxHandle txHandle = new TxHandle();

		TxTemplate txTemp = new TxTemplate(ledgerHash, txHandle);

		CryptoKeyPair cryptoKeyPair = signatureFunction.generateKeyPair();
		PubKey pubKey = cryptoKeyPair.getPubKey();
		txTemp.users().register(new BlockchainIdentityData(pubKey));
		PreparedTransaction ptx = txTemp.prepare();
		ptx.sign(cryptoKeyPair);
		ptx.commit();
		return txHandle.txRequest;
	}

	public static TransactionRequest createContractEventTxRequest(HashDigest ledgerHash,
			SignatureFunction signatureFunction, String contractAddress, String event, byte[] args) {
		TxHandle txHandle = new TxHandle();

		TxTemplate txTemp = new TxTemplate(ledgerHash, txHandle);

		txTemp.contractEvents().send(contractAddress, event, args);

		CryptoKeyPair cryptoKeyPair = signatureFunction.generateKeyPair();
		PubKey pubKey = cryptoKeyPair.getPubKey();
		txTemp.users().register(new BlockchainIdentityData(pubKey));
		PreparedTransaction ptx = txTemp.prepare();
		ptx.sign(cryptoKeyPair);
		ptx.commit();
		return txHandle.txRequest;
	}

	public static TransactionStagedSnapshot generateRandomSnapshot() {
		TransactionStagedSnapshot txDataSnapshot = new TransactionStagedSnapshot();
		txDataSnapshot.setAdminAccountHash(generateRandomHash());
		txDataSnapshot.setContractAccountSetHash(generateRandomHash());
		txDataSnapshot.setDataAccountSetHash(generateRandomHash());
		txDataSnapshot.setUserAccountSetHash(generateRandomHash());
		return txDataSnapshot;
	}
	
	public static HashDigest generateRandomHash() {
		byte[] data = new byte[64];
		rand.nextBytes(data);
		return CryptoUtils.hash(CryptoAlgorithm.SHA256).hash(data);
	}
	
	
	public static CryptoSetting createDefaultCryptoSetting() {
		CryptoConfig cryptoSetting = new CryptoConfig();
		cryptoSetting.setAutoVerifyHash(true);
		cryptoSetting.setHashAlgorithm(CryptoAlgorithm.SHA256);
		return cryptoSetting;
	}
	

	private static class TxHandle implements TransactionService {

		private TransactionRequest txRequest;

		@Override
		public TransactionResponse process(TransactionRequest txRequest) {
			this.txRequest = txRequest;
			return null;
		}

	}

}
