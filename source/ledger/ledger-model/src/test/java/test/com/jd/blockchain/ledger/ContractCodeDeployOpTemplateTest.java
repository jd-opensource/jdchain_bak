/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: test.com.jd.blockchain.ledger.data.ContractCodeDeployOpTemplateTest
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2018/8/30 上午10:53
 * Description:
 */
package test.com.jd.blockchain.ledger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainIdentityData;
import com.jd.blockchain.ledger.ContractCodeDeployOperation;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.transaction.ContractCodeDeployOpTemplate;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 *
 * @author shaozhuguang
 * @create 2018/8/30
 * @since 1.0.0
 */

public class ContractCodeDeployOpTemplateTest {

	private ContractCodeDeployOpTemplate data;

	@Before
	public void initContractCodeDeployOpTemplate() {
		DataContractRegistry.register(ContractCodeDeployOperation.class);
		DataContractRegistry.register(Operation.class);
		SignatureFunction signFunc = Crypto.getSignatureFunction("ED25519");
		PubKey pubKey = signFunc.generateKeypair().getPubKey();
		BlockchainIdentity contractID = new BlockchainIdentityData(pubKey);
		byte[] chainCode = "jd-test".getBytes();
		data = new ContractCodeDeployOpTemplate(contractID, chainCode);
	}

	@Test
	public void testSerialize_ContractCodeDeployOperation() throws Exception {
		byte[] serialBytes = BinaryProtocol.encode(data, ContractCodeDeployOperation.class);
		ContractCodeDeployOperation resolvedData = BinaryProtocol.decode(serialBytes);
		System.out.println("------Assert start ------");
		assertArrayEquals(resolvedData.getChainCode(), data.getChainCode());
		assertEquals(resolvedData.getContractID().getAddress(), data.getContractID().getAddress());
		assertEquals(resolvedData.getContractID().getPubKey(), data.getContractID().getPubKey());
		System.out.println("------Assert OK ------");
	}

	@Test
	public void testSerialize_Operation() throws Exception {
		byte[] serialBytes = BinaryProtocol.encode(data, ContractCodeDeployOperation.class);
		ContractCodeDeployOperation resolvedData = BinaryProtocol.decode(serialBytes);
		BlockchainIdentity expCodeId = data.getContractID();
		BlockchainIdentity actualCodeId = resolvedData.getContractID();

		assertEquals(expCodeId.getAddress().toBase58(), actualCodeId.getAddress().toBase58());
		assertEquals(expCodeId.getPubKey().toBase58(), actualCodeId.getPubKey().toBase58());
		assertTrue(BytesUtils.equals(data.getChainCode(), resolvedData.getChainCode()));

		System.out.println("------Assert start ------");
		System.out.println("serialBytesLength=" + serialBytes.length);
		System.out.println(resolvedData);
		System.out.println("------Assert OK ------");
	}

	// test复杂场景;

}