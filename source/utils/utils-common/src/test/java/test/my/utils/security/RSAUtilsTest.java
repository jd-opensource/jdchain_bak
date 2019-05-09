package test.my.utils.security;

import static com.jd.blockchain.utils.security.RSAUtils.ALG_RSA;
import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.jd.blockchain.utils.codec.HexUtils;
import com.jd.blockchain.utils.security.AESUtils;
import com.jd.blockchain.utils.security.RSAKeyPair;
import com.jd.blockchain.utils.security.RSAUtils;

public class RSAUtilsTest {

	@Test
	public void testEncription() throws UnsupportedEncodingException {
		// 测试：公钥加密，私钥解密；
		RSAKeyPair keyPair = RSAUtils.generateKey512();
		System.out.println("New-PUB-KEY=" + keyPair.getPublicKey_Base58());
		System.out.println("New-PRIV-KEY=" + keyPair.getPrivateKey_Base58());

		String content = UUID.randomUUID().toString();
		byte[] contentBytes = content.getBytes("UTF-8");
		
		System.out.println("要加密的数据长度：" + contentBytes.length);
		byte[] enContentBytes = RSAUtils.encryptByPublicKey_Base58(contentBytes, keyPair.getPublicKey_Base58());

		byte[] deContentBytes = RSAUtils.decryptByPrivateKey_Base58(enContentBytes, keyPair.getPrivateKey_Base58());
		String deContent = new String(deContentBytes, "UTF-8");

		assertEquals(content, deContent);
	}

	public static final String PUB_KEY = "rbanryFCC6yJnCRCQgVWyYXvqkSBz4k8KgpbVavFUopm6EcXY7217hqU7sa5iEvG2RbYUqcswYvdVikJqWADXNh6XDs1AB6MWHdhNrh4ha7rAVfDM6H9ho3KxHWXKSAc";

	public static final String PRIV_KEY = "2YxGPrDW2hwdAfrXymm5pJb1ftXzKmPqYBqYdgQWAcBRzMk8nDM7sGeDf3cJZEQaremdVbfQqUrhRtz2MV31TX8qABm3ChTsszGUmoxvybJPaVd513adZwLyo54NS1GuyLxPyD2N32uqYnKtKojTrxfWZmoLpACmoqhMzt68Sn69ULd84j24tt8VBpSWEH1Xsr9hCtBB65HT3f6FH5X5HYMU6ueaPrx8Sh4zFiEW5eLM4xJEPLjf3iaWatN9YV9QPeyvavicNYCtmJWQ6sv13xKR9DscWVJ61EjSxTQQMQe1sA9Anewjmk1bRUbnoKebM2kaGK9Q3Gg7qx8QwoAvRfPP9A6pyUPPaE33WuKDKZ2QPzYQ531TXJA8hYwnCvfcixp3cJqGtFNEkhBu7H4DSqchnWfVJ62HJFBB2L96Fxkiudyqh8ejjsweWVKPw1aYoNzreEnRpMHsLoxAN6eRFA3NV";

	public static final String DATA_HEX = "f61610cba4c92c268a2bd73e963a270b";

	public static final String CHIPER_DATA = "b127ffbbf6d570cea43aba7494bf04e1d1263d130b922cde32d558de3fdd88f2c205ba8151fdfcae7bb5c075fad10524cfde89b6b3be4ad8b2fb0db38901ea9c";

	@Test
	public void testEncryptAndDecrypt() {
		byte[] contentBytes = HexUtils.decode(DATA_HEX);
		System.out.println("要加密的数据长度：" + contentBytes.length);
		
		byte[] enContentBytes = RSAUtils.encryptByPublicKey_Base58(contentBytes, PUB_KEY);
		System.out.println("CHIPER-DATA=[" + HexUtils.encode(enContentBytes) + "]");

		byte[] deContentBytes = RSAUtils.decryptByPrivateKey_Base58(enContentBytes, PRIV_KEY);
		String deContent = HexUtils.encode(deContentBytes);
		System.out.println("DECRYPT-DATA=[" + HexUtils.encode(enContentBytes) + "]");
		
		assertEquals(DATA_HEX, deContent);

	}

	@Test
	public void generateKeyPairTest() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(2048, new SecureRandom());
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		byte[] pubKey = publicKey.getEncoded();
		byte[] privKey = privateKey.getEncoded();

		System.out.println(Base64.toBase64String(pubKey));
		System.out.println(Base64.toBase64String(privKey));

		System.out.println(Hex.toHexString(pubKey));
		System.out.println(Hex.toHexString(privKey));

	}

}
