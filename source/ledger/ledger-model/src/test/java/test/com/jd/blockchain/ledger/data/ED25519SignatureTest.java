package test.com.jd.blockchain.ledger.data;

import java.util.Random;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.utils.security.Ed25519Utils;

public class ED25519SignatureTest {

	public static void main(String[] args) {
		Random rand = new Random();
		byte[] data = new byte[64];
		rand.nextBytes(data);

		BlockchainKeyPair key = BlockchainKeyGenerator.getInstance().generate(CryptoAlgorithm.ED25519);

		byte[] pubKey = key.getPubKey().getRawKeyBytes();
		byte[] privKey = key.getPrivKey().getRawKeyBytes();

		int count = 10000;
		
		System.out.println("=================== do sign test ===================");
		byte[] sign = null;
		for (int r = 0; r < 5; r++) {
			System.out.println("------------- round[" + r + "] --------------");
			long startTS = System.currentTimeMillis();
			for (int i = 0; i < count; i++) {
				sign = Ed25519Utils.sign_512(data, privKey);
			}
			long elapsedTS = System.currentTimeMillis() - startTS;
			System.out.println(String.format("Siging Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
					(count * 1000.00D) / elapsedTS));
		}
		
		System.out.println("=================== do verify test ===================");
		for (int r = 0; r < 5; r++) {
			System.out.println("------------- round[" + r + "] --------------");
			long startTS = System.currentTimeMillis();
			for (int i = 0; i < count; i++) {
				Ed25519Utils.verify(data, pubKey, sign);
			}
			long elapsedTS = System.currentTimeMillis() - startTS;
			System.out.println(String.format("Verifying Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
					(count * 1000.00D) / elapsedTS));
		}

	}

}
