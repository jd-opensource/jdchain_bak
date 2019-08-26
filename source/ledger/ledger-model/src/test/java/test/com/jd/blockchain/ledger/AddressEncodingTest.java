package test.com.jd.blockchain.ledger;

import java.util.Random;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;

public class AddressEncodingTest {

	public static void main(String[] args) {
		BlockchainKeypair bkp = BlockchainKeyGenerator.getInstance().generate();
		PubKey pk = bkp.getPubKey();
		byte[] data =new byte[64];
		Random rand = new Random();
		rand.nextBytes(data);
		int round = 5;
		for (int r = 0; r < round; r++) {
			System.out.println("================== round[" + r + "] ===================");
			int count = 100000;
			long startTS = System.currentTimeMillis();
			for (int i = 0; i < count; i++) {
				AddressEncoding.generateAddress(pk);
			}
			long elapsedTS = System.currentTimeMillis() - startTS;
			System.out.println(String.format("Compute Count=%s; Total time=%s ms; TPS=%.2f", count, elapsedTS,
					((count * 1000.0D) / elapsedTS)));
		}
	}

	// @Test
	// public void testGenerateAddress() {
	// fail("Not yet implemented");
	// }

}
