package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitProperties;
import com.jd.blockchain.ledger.LedgerInitProperties.ParticipantProperties;
import com.jd.blockchain.ledger.LedgerPermission;
import com.jd.blockchain.ledger.RoleInitData;
import com.jd.blockchain.ledger.RolesPolicy;
import com.jd.blockchain.ledger.TransactionPermission;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.utils.codec.HexUtils;

public class LedgerInitPropertiesTest {
	

	static {
		DataContractRegistry.register(LedgerInitOperation.class);
		DataContractRegistry.register(UserRegisterOperation.class);
	}

	public static final String PASSWORD = "abc";

	public static final String[] PUB_KEYS = { "3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9",
			"3snPdw7i7PajLB35tEau1kmixc6ZrjLXgxwKbkv5bHhP7nT5dhD9eX",
			"3snPdw7i7PZi6TStiyc6mzjprnNhgs2atSGNS8wPYzhbKaUWGFJt7x",
			"3snPdw7i7PifPuRX7fu3jBjsb3rJRfDe9GtbDfvFJaJ4V4hHXQfhwk" };

	public static final String[] PRIV_KEYS = {
			"177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x",
			"177gju9p5zrNdHJVEQnEEKF4ZjDDYmAXyfG84V5RPGVc5xFfmtwnHA7j51nyNLUFffzz5UT",
			"177gjtwLgmSx5v1hFb46ijh7L9kdbKUpJYqdKVf9afiEmAuLgo8Rck9yu5UuUcHknWJuWaF",
			"177gk1pudweTq5zgJTh8y3ENCTwtSFsKyX7YnpuKPo7rKgCkCBXVXh5z2syaTCPEMbuWRns" };


	private static String expectedCreatedTimeStr = "2019-08-01 14:26:58.069+0800";

	private static String expectedCreatedTimeStr1 = "2019-08-01 13:26:58.069+0700";

	@Test
	public void testTimeFormat() throws ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat(LedgerInitProperties.CREATED_TIME_FORMAT);
		timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
		// 或者设置全局的默认时区；
		// TimeZone.setDefault(TimeZone.getTimeZone("GMT+08:00"));

		Date time = timeFormat.parse(expectedCreatedTimeStr);
		String actualTimeStr = timeFormat.format(time);
		assertEquals(expectedCreatedTimeStr, actualTimeStr);

		Date time1 = timeFormat.parse(expectedCreatedTimeStr1);
		String actualTimeStr1 = timeFormat.format(time1);
		assertEquals(expectedCreatedTimeStr, actualTimeStr1);
	}

	@Test
	public void testProperties() throws IOException, ParseException {
		// 加载用于测试的账本初始化配置；
		ClassPathResource ledgerInitSettingResource = new ClassPathResource("ledger.init");
		InputStream in = ledgerInitSettingResource.getInputStream();
		try {
			LedgerInitProperties initProps = LedgerInitProperties.resolve(in);

			// 验证账本信息；
			String expectedLedgerSeed = "932dfe23-fe23232f-283f32fa-dd32aa76-8322ca2f-56236cda-7136b322-cb323ffe"
					.replace("-", "");
			String actualLedgerSeed = HexUtils.encode(initProps.getLedgerSeed());
			assertEquals(expectedLedgerSeed, actualLedgerSeed);

			SimpleDateFormat timeFormat = new SimpleDateFormat(LedgerInitProperties.CREATED_TIME_FORMAT);
			timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			long expectedTs = timeFormat.parse(expectedCreatedTimeStr).getTime();
			assertEquals(expectedTs, initProps.getCreatedTime());

			String createdTimeStr = timeFormat.format(new Date(initProps.getCreatedTime()));
			assertEquals(expectedCreatedTimeStr, createdTimeStr);

			// 验证角色配置；
			RoleInitData[] roles = initProps.getRoles();
			assertEquals(4, roles.length);
			Map<String, RoleInitData> rolesInitDatas = new HashMap<String, RoleInitData>();
			for (RoleInitData r : roles) {
				rolesInitDatas.put(r.getRoleName(), r);
			}
			// 初始化配置的角色最终也是有序排列的，按照角色名称的自然顺序；
			String[] expectedRolesNames = { "DEFAULT", "ADMIN", "MANAGER", "GUEST" };
			Arrays.sort(expectedRolesNames);
			assertEquals(expectedRolesNames[0], roles[0].getRoleName());
			assertEquals(expectedRolesNames[1], roles[1].getRoleName());
			assertEquals(expectedRolesNames[2], roles[2].getRoleName());
			assertEquals(expectedRolesNames[3], roles[3].getRoleName());

			RoleInitData roleDefault = rolesInitDatas.get("DEFAULT");
			assertArrayEquals(
					new LedgerPermission[] { LedgerPermission.REGISTER_USER, LedgerPermission.REGISTER_DATA_ACCOUNT },
					roleDefault.getLedgerPermissions());
			assertArrayEquals(new TransactionPermission[] { TransactionPermission.DIRECT_OPERATION,
					TransactionPermission.CONTRACT_OPERATION }, roleDefault.getTransactionPermissions());

			RoleInitData roleAdmin = rolesInitDatas.get("ADMIN");
			assertArrayEquals(
					new LedgerPermission[] { LedgerPermission.CONFIGURE_ROLES, LedgerPermission.AUTHORIZE_USER_ROLES,
							LedgerPermission.SET_CONSENSUS, LedgerPermission.SET_CRYPTO,
							LedgerPermission.REGISTER_PARTICIPANT, LedgerPermission.REGISTER_USER },
					roleAdmin.getLedgerPermissions());
			assertArrayEquals(new TransactionPermission[] { TransactionPermission.DIRECT_OPERATION },
					roleAdmin.getTransactionPermissions());

			RoleInitData roleManager = rolesInitDatas.get("MANAGER");
			assertArrayEquals(
					new LedgerPermission[] { LedgerPermission.CONFIGURE_ROLES, LedgerPermission.AUTHORIZE_USER_ROLES,
							LedgerPermission.REGISTER_USER, LedgerPermission.REGISTER_DATA_ACCOUNT,
							LedgerPermission.REGISTER_CONTRACT, LedgerPermission.UPGRADE_CONTRACT,
							LedgerPermission.SET_USER_ATTRIBUTES, LedgerPermission.WRITE_DATA_ACCOUNT },
					roleManager.getLedgerPermissions());
			assertArrayEquals(new TransactionPermission[] { TransactionPermission.DIRECT_OPERATION,
					TransactionPermission.CONTRACT_OPERATION }, roleManager.getTransactionPermissions());

			RoleInitData roleGuest = rolesInitDatas.get("GUEST");
			assertTrue(roleGuest.getLedgerPermissions() == null || roleGuest.getLedgerPermissions().length == 0);
			assertArrayEquals(new TransactionPermission[] { TransactionPermission.CONTRACT_OPERATION },
					roleGuest.getTransactionPermissions());

			// 验证共识和密码配置；
			assertEquals("com.jd.blockchain.consensus.bftsmart.BftsmartConsensusProvider",
					initProps.getConsensusProvider());

			String[] cryptoProviders = initProps.getCryptoProperties().getProviders();
			assertEquals(2, cryptoProviders.length);
			assertEquals("com.jd.blockchain.crypto.service.classic.ClassicCryptoService", cryptoProviders[0]);
			assertEquals("com.jd.blockchain.crypto.service.sm.SMCryptoService", cryptoProviders[1]);

			// 验证参与方信息；
			assertEquals(4, initProps.getConsensusParticipantCount());

			ParticipantProperties part0 = initProps.getConsensusParticipant(0);
			assertEquals("jd.com", part0.getName());
			PubKey pubKey0 = KeyGenUtils.decodePubKey("3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9");
			assertEquals(pubKey0, part0.getPubKey());
			assertEquals("127.0.0.1", part0.getInitializerAddress().getHost());
			assertEquals(8800, part0.getInitializerAddress().getPort());
			assertEquals(true, part0.getInitializerAddress().isSecure());
			assertArrayEquals(new String[] { "ADMIN", "MANAGER" }, part0.getRoles());
			assertEquals(RolesPolicy.UNION, part0.getRolesPolicy());

			ParticipantProperties part1 = initProps.getConsensusParticipant(1);
			assertEquals(false, part1.getInitializerAddress().isSecure());
			PubKey pubKey1 = KeyGenUtils.decodePubKey("3snPdw7i7PajLB35tEau1kmixc6ZrjLXgxwKbkv5bHhP7nT5dhD9eX");
			assertEquals(pubKey1, part1.getPubKey());
			assertArrayEquals(new String[] { "MANAGER" }, part1.getRoles());
			assertEquals(RolesPolicy.UNION, part1.getRolesPolicy());

			ParticipantProperties part2 = initProps.getConsensusParticipant(2);
			assertEquals("7VeRAr3dSbi1xatq11ZcF7sEPkaMmtZhV9shonGJWk9T4pLe", part2.getPubKey().toBase58());
			assertArrayEquals(new String[] { "MANAGER" }, part2.getRoles());
			assertEquals(RolesPolicy.UNION, part2.getRolesPolicy());

			ParticipantProperties part3 = initProps.getConsensusParticipant(3);
			PubKey pubKey3 = KeyGenUtils.decodePubKey("3snPdw7i7PifPuRX7fu3jBjsb3rJRfDe9GtbDfvFJaJ4V4hHXQfhwk");
			assertEquals(pubKey3, part3.getPubKey());
			assertArrayEquals(new String[] { "GUEST" }, part3.getRoles());
			assertEquals(RolesPolicy.INTERSECT, part3.getRolesPolicy());

		} finally {
			in.close();
		}
	}

	@Test
	public void testPubKeyAddress() {
		String[] pubKeys = PUB_KEYS;
		int index = 0;
		for (String pubKeyStr : pubKeys) {
			System.out.println("[" + index + "][配置] = " + pubKeyStr);
			PubKey pubKey = KeyGenUtils.decodePubKey(pubKeyStr);
			System.out.println("[" + index + "][公钥Base58] = " + pubKey.toBase58());
			System.out.println("[" + index + "][地址] = " + AddressEncoding.generateAddress(pubKey).toBase58());
			System.out.println("--------------------------------------------------------------------");
			index++;
		}
	}

}
