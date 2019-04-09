package test.com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.utils.classic.ECDSAUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.math.BigInteger;
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
