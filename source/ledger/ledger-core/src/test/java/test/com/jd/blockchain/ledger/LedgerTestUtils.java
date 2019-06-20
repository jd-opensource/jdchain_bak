package test.com.jd.blockchain.ledger;

import java.util.Random;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoProvider;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.service.classic.ClassicCryptoService;
import com.jd.blockchain.crypto.service.sm.SMCryptoService;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.CryptoSetting;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.TransactionRequest;
import com.jd.blockchain.ledger.TransactionRequestBuilder;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.impl.TransactionStagedSnapshot;
import com.jd.blockchain.transaction.ConsensusParticipantData;
import com.jd.blockchain.transaction.LedgerInitSettingData;
import com.jd.blockchain.transaction.TransactionService;
import com.jd.blockchain.transaction.TxBuilder;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.net.NetworkAddress;

public class LedgerTestUtils {

	public static final SignatureFunction ED25519_SIGN_FUNC = Crypto.getSignatureFunction("ED25519");

	public static final CryptoAlgorithm ED25519 = ED25519_SIGN_FUNC.getAlgorithm();

	private static final String[] SUPPORTED_PROVIDERS = { ClassicCryptoService.class.getName(),
			SMCryptoService.class.getName() };

	private static Random rand = new Random();

	public static TransactionRequest createTxRequest_UserReg(HashDigest ledgerHash) {
		BlockchainKeypair key = BlockchainKeyGenerator.getInstance().generate(ED25519);
		return createTxRequest_UserReg(ledgerHash, key);
	}

	public static LedgerInitSetting createLedgerInitSetting() {
		BlockchainKeypair[] partiKeys = new BlockchainKeypair[2];
		partiKeys[0] = BlockchainKeyGenerator.getInstance().generate();
		partiKeys[1] = BlockchainKeyGenerator.getInstance().generate();
		return createLedgerInitSetting(partiKeys);
	}

	public static LedgerInitSetting createLedgerInitSetting(BlockchainKeypair[] partiKeys) {
		CryptoProvider[] supportedProviders = new CryptoProvider[SUPPORTED_PROVIDERS.length];
		for (int i = 0; i < SUPPORTED_PROVIDERS.length; i++) {
			supportedProviders[i] = Crypto.getProvider(SUPPORTED_PROVIDERS[i]);
		}

		CryptoConfig defCryptoSetting = new CryptoConfig();
		defCryptoSetting.setSupportedProviders(supportedProviders);
		defCryptoSetting.setAutoVerifyHash(true);
		defCryptoSetting.setHashAlgorithm(ClassicAlgorithm.SHA256);

		LedgerInitSettingData initSetting = new LedgerInitSettingData();

		initSetting.setLedgerSeed(BytesUtils.toBytes("A Test Ledger seed!", "UTF-8"));
		initSetting.setCryptoSetting(defCryptoSetting);
		ConsensusParticipantData[] parties = new ConsensusParticipantData[partiKeys.length];
		for (int i = 0; i < parties.length; i++) {
			parties[i] = new ConsensusParticipantData();
			parties[i].setId(0);
			parties[i].setName("Parti-" + i);
			parties[i].setPubKey(partiKeys[i].getPubKey());
			parties[i].setAddress(AddressEncoding.generateAddress(partiKeys[i].getPubKey()).toBase58());
			parties[i].setHostAddress(new NetworkAddress("192.168.1." + (10 + i), 9000));

		}

		initSetting.setConsensusParticipants(parties);

		return initSetting;
	}

	public static TransactionRequest createTxRequest_UserReg(HashDigest ledgerHash, BlockchainKeypair userKeypair) {
		return createTxRequest_UserReg(ledgerHash, userKeypair, null);
	}

	public static TransactionRequest createTxRequest_UserReg(HashDigest ledgerHash, BlockchainKeypair userKeypair,
			BlockchainKeypair gatewayKeypair) {
		TxBuilder txBuilder = new TxBuilder(ledgerHash);

		txBuilder.users().register(userKeypair.getIdentity());

		TransactionRequestBuilder txReqBuilder = txBuilder.prepareRequest();
		txReqBuilder.signAsEndpoint(userKeypair);
		if (gatewayKeypair != null) {
			txReqBuilder.signAsNode(gatewayKeypair);
		}
		
		return txReqBuilder.buildRequest();
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
