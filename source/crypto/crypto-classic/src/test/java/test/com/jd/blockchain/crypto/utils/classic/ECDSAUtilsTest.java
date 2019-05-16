package test.com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.utils.classic.ECDSAUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.test.FixedSecureRandom;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: ECDSAUtilsTest
 * @description: Tests for methods in ECDSAUtils
 * @date 2019-04-09, 14:58
 */
public class ECDSAUtilsTest {

    @Test
    public void generateKeyPairTest(){

        AsymmetricCipherKeyPair keyPair = ECDSAUtils.generateKeyPair();
        ECPrivateKeyParameters privKeyParams = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters pubKeyParams = (ECPublicKeyParameters) keyPair.getPublic();

        byte[] privKeyBytes = BigIntegerTo32Bytes(privKeyParams.getD());
        byte[] pubKeyBytes = pubKeyParams.getQ().getEncoded(false);

        assertEquals(32,privKeyBytes.length);
        assertEquals(65,pubKeyBytes.length);
    }

    @Test
    public void retrievePublicKeyTest(){

        AsymmetricCipherKeyPair keyPair = ECDSAUtils.generateKeyPair();
        ECPrivateKeyParameters privKeyParams = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters pubKeyParams = (ECPublicKeyParameters) keyPair.getPublic();

        byte[] privKeyBytes = BigIntegerTo32Bytes(privKeyParams.getD());
        byte[] pubKeyBytes = pubKeyParams.getQ().getEncoded(false);

        byte[] retrievedPubKeyBytes = ECDSAUtils.retrievePublicKey(privKeyBytes);

        assertArrayEquals(pubKeyBytes,retrievedPubKeyBytes);
    }

    @Test
    public void signTest(){

        AsymmetricCipherKeyPair keyPair = ECDSAUtils.generateKeyPair();
        ECPrivateKeyParameters privKeyParams = (ECPrivateKeyParameters) keyPair.getPrivate();

        byte[] privKeyBytes = BigIntegerTo32Bytes(privKeyParams.getD());

        Random random = new Random();
        byte[] data = new byte[1024];
        random.nextBytes(data);

        byte[] signatureDigestFromBytes = ECDSAUtils.sign(data,privKeyBytes);
        byte[] signatureDigestFromParams = ECDSAUtils.sign(data,privKeyBytes);

        assertEquals(64,signatureDigestFromParams.length);
        assertEquals(64,signatureDigestFromBytes.length);
    }

    @Test
    public void verifyTest(){

        AsymmetricCipherKeyPair keyPair = ECDSAUtils.generateKeyPair();
        ECPrivateKeyParameters privKeyParams = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters pubKeyParams = (ECPublicKeyParameters) keyPair.getPublic();

        byte[] pubKeyBytes = pubKeyParams.getQ().getEncoded(false);

        Random random = new Random();
        byte[] data = new byte[1024];
        random.nextBytes(data);

        byte[] signatureDigest = ECDSAUtils.sign(data,privKeyParams);

        assertTrue(ECDSAUtils.verify(data,pubKeyParams,signatureDigest));
        assertTrue(ECDSAUtils.verify(data,pubKeyBytes,signatureDigest));
    }

    @Test
    public void checkParams(){
        // https://crypto.stackexchange.com/questions/784/are-there-any-secp256k1-ecdsa-test-examples-available
        ECDomainParameters params = ECDSAUtils.getDomainParams();
        ECPoint ECDSA_G = params.getG();

        BigInteger scalar = new BigInteger("AA5E28D6A97A2479A65527F7290311A3624D4CC0FA1578598EE3C2613BF99522",16);

        String xCoord = "34F9460F0E4F08393D192B3C5133A6BA099AA0AD9FD54EBCCFACDFA239FF49C6";
        String yCoord = "0B71EA9BD730FD8923F6D25A7A91E7DD7728A960686CB5A901BB419E0F2CA232";


        ECMultiplier createBasePointMultiplier = new FixedPointCombMultiplier();
        ECPoint point = createBasePointMultiplier.multiply(ECDSA_G,scalar).normalize();
        byte[] result = point.getEncoded(false);

        assertEquals("04" + xCoord + yCoord,Hex.toHexString(result).toUpperCase());
    }

    @Test
    public void checkDeterministicValues(){
        // https://crypto.stackexchange.com/questions/41316/complete-set-of-test-vectors-for-ecdsa-secp256k1
        ECDomainParameters domainParams = ECDSAUtils.getDomainParams();
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(
                new BigInteger("ebb2c082fd7727890a28ac82f6bdf97bad8de9f5d7c9028692de1a255cad3e0f", 16),
                domainParams);
        ECPublicKeyParameters pubKey = new ECPublicKeyParameters(
                domainParams.getCurve().decodePoint(Hex.decode("04" +
                        "779dd197a5df977ed2cf6cb31d82d43328b790dc6b3b7d4437a427bd5847dfcd" +
                        "e94b724a555b6d017bb7607c3e3281daf5b1699d6ef4124975c9237b917d426f")),
                domainParams);

        byte[] privKeyBytes = BigIntegerTo32Bytes(privKey.getD());
        byte[] pubKeyBytes = ECDSAUtils.retrievePublicKey(privKeyBytes);

        assertArrayEquals(pubKeyBytes,pubKey.getQ().getEncoded(false));

        SecureRandom k = new FixedSecureRandom(Hex.decode("49a0d7b786ec9cde0d0721d72804befd06571c974b191efb42ecf322ba9ddd9a"));
        CipherParameters params = new ParametersWithRandom(privKey,k);

        byte[] hashedMsg = Hex.decode("4b688df40bcedbe641ddb16ff0a1842d9c67ea1c3bf63f3e0471baa664531d1a");

        byte[] signature = ECDSAUtils.sign(params,hashedMsg);

        String r = "241097efbf8b63bf145c8961dbdf10c310efbb3b2676bbc0f8b08505c9e2f795";
        String s = "021006b7838609339e8b415a7f9acb1b661828131aef1ecbc7955dfb01f3ca0e";
        assertEquals(Hex.toHexString(signature),r + s);

        assertTrue(ECDSAUtils.verify(pubKey,signature,hashedMsg));
    }

//    @Test
    public void performanceTest(){

        int count = 10000;
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        AsymmetricCipherKeyPair keyPair = ECDSAUtils.generateKeyPair();
        ECPrivateKeyParameters privKeyParams = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters pubKeyParams = (ECPublicKeyParameters) keyPair.getPublic();

        byte[] signatureDigest = ECDSAUtils.sign(data,privKeyParams);

        assertTrue(ECDSAUtils.verify(data,pubKeyParams,signatureDigest));

        System.out.println("=================== do ECDSA sign test ===================");

        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                ECDSAUtils.sign(data,privKeyParams);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("ECDSA Signing Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do ECDSA verify test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                ECDSAUtils.verify(data,pubKeyParams,signatureDigest);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("ECDSA Verifying Count=%s; Elapsed Times=%s; TPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }
    }

    // To convert BigInteger to byte[] whose length is 32
    private static byte[] BigIntegerTo32Bytes(BigInteger b){
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[32];
        if (tmp.length > result.length) {
            System.arraycopy(tmp, tmp.length - result.length, result, 0, result.length);
        }
        else {
            System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
        }
        return result;
    }
}
