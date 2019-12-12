package test.com.jd.blockchain.ledger;

import java.util.Random;

import org.junit.Test;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.utils.security.Ed25519Utils;

public class ED25519SignatureTest {

//	@Test
	public void perfomanceTest() {
		Random rand = new Random();
		byte[] data = new byte[64];
		rand.nextBytes(data);

		SignatureFunction signFunc = Crypto.getSignatureFunction("ED25519");
		AsymmetricKeypair key = signFunc.generateKeypair();
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
