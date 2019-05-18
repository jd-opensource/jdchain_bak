package test.com.jd.blockchain.ledger;

import java.util.Random;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.BlockchainIdentityData;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.impl.TransactionStagedSnapshot;
import com.jd.blockchain.transaction.TransactionService;
import com.jd.blockchain.transaction.TxTemplate;

public class LedgerTestUtils {

	// private static ThreadLocalRandom rand = ThreadLocalRandom.current();

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	private static Random rand = new Random();

	public static TransactionRequest createTxRequest(HashDigest ledgerHash) {
		SignatureFunction signFunc = Crypto.getSignatureFunction("ED25519");
		return createTxRequest(ledgerHash, signFunc);
	}

	public static TransactionRequest createTxRequest(HashDigest ledgerHash, SignatureFunction signatureFunction) {
		TxHandle txHandle = new TxHandle();

		TxTemplate txTemp = new TxTemplate(ledgerHash, txHandle);

		AsymmetricKeypair cryptoKeyPair = signatureFunction.generateKeypair();
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

		AsymmetricKeypair cryptoKeyPair = signatureFunction.generateKeypair();
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
		return Crypto.getHashFunction("SHA256").hash(data);
	}

	public static CryptoSetting createDefaultCryptoSetting() {

		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig cryptoSetting = new CryptoConfig();
		cryptoSetting.setSupportedProviders(supportedProviders);
		cryptoSetting.setAutoVerifyHash(true);
		cryptoSetting.setHashAlgorithm(ClassicAlgorithm.SHA256);
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
