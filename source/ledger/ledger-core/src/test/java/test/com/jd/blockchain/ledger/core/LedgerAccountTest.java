package test.com.jd.blockchain.ledger.core;

import java.util.Random;

import org.junit.Before;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.UserInfo;

/**
 * Created by zhangshuang3 on 2018/9/3.
 */
public class LedgerAccountTest {
	byte[] seed = null;
	byte[] settingValue = null;
	byte[] rawDigestBytes = null;

	@Before
	public void initCfg() throws Exception {
		Random rand = new Random();
		seed = new byte[8];
		settingValue = new byte[8];
		rawDigestBytes = new byte[8];
		rand.nextBytes(seed);
		rand.nextBytes(settingValue);
		rand.nextBytes(rawDigestBytes);
		DataContractRegistry.register(BlockchainIdentity.class);
		DataContractRegistry.register(UserInfo.class);
	}

//	@Test
//	public void testSerialize_AccountHeader() {
//		String address = "xxxxxxxxxxxx";
//		PubKey pubKey = new PubKey(SMAlgorithm.SM2, rawDigestBytes);
//		HashDigest hashDigest = new HashDigest(ClassicAlgorithm.SHA256, rawDigestBytes);
//		MerkleAccountSet.AccountHeaderData accountHeaderData = new MerkleAccountSet.AccountHeaderData(Bytes.fromString(address),
//				pubKey, hashDigest);
//
//		// encode and decode
//		byte[] encodeBytes = BinaryProtocol.encode(accountHeaderData, AccountHeader.class);
//		AccountHeader deAccountHeaderData = BinaryProtocol.decode(encodeBytes);
//
//		// verify start
//		assertEquals(accountHeaderData.getAddress(), deAccountHeaderData.getAddress());
//		assertEquals(accountHeaderData.getPubKey(), deAccountHeaderData.getPubKey());
//		assertEquals(accountHeaderData.getRootHash(), deAccountHeaderData.getRootHash());

//	}

}
