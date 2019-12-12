package test.com.jd.blockchain.crypto.paillier;

import com.jd.blockchain.crypto.paillier.PaillierPrivateKeyParameters;
import com.jd.blockchain.crypto.paillier.PaillierPublicKeyParameters;
import com.jd.blockchain.crypto.paillier.PaillierUtils;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;

/**
 * @author zhanglin33
 * @title: PaillierUtilsTest
 * @description: Tests on PaillierUtils
 * @date 2019-04-30, 14:54
 */
public class PaillierUtilsTest {
    @Test
    public void test() {

        AsymmetricCipherKeyPair keyPair = PaillierUtils.generateKeyPair();
        PaillierPublicKeyParameters pubKeyParams = (PaillierPublicKeyParameters) keyPair.getPublic();
        PaillierPrivateKeyParameters privKeyParams = (PaillierPrivateKeyParameters) keyPair.getPrivate();

        BigInteger n = pubKeyParams.getModulus();
        BigInteger nSquared = pubKeyParams.getModulusSquared();
        BigInteger g = pubKeyParams.getGenerator();

        BigInteger nConverted = new BigInteger(1, bigIntegerToBytes(n,256));
        BigInteger nSquaredConverted = new BigInteger(1, bigIntegerToBytes(nSquared,512));
        BigInteger gConverted = new BigInteger(1, bigIntegerToBytes(g,256));
        assertEquals(nConverted, n);
        assertEquals(nSquaredConverted, nSquared);
        assertEquals(gConverted, g);

        BigInteger p = privKeyParams.getP();
        BigInteger pSquared = privKeyParams.getPSquared();
        BigInteger q = privKeyParams.getQ();
        BigInteger qSquared = privKeyParams.getQSquared();
        BigInteger pInverse = privKeyParams.getPInverse();
        BigInteger muP = privKeyParams.getMuP();
        BigInteger muQ = privKeyParams.getMuQ();

        BigInteger pConverted = new BigInteger(1, bigIntegerToBytes(p,128));
        BigInteger pSquaredConverted = new BigInteger(1, bigIntegerToBytes(pSquared,256));
        BigInteger qConverted = new BigInteger(1, bigIntegerToBytes(q,128));
        BigInteger qSquaredConverted = new BigInteger(1, bigIntegerToBytes(qSquared,256));
        BigInteger pInverseConverted = new BigInteger(1, bigIntegerToBytes(pInverse,128));
        BigInteger muPConverted = new BigInteger(1, bigIntegerToBytes(muP,128));
        BigInteger muQConverted = new BigInteger(1, bigIntegerToBytes(muQ,128));
        assertEquals(pConverted, p);
        assertEquals(pSquaredConverted, pSquared);
        assertEquals(qConverted, q);
        assertEquals(qSquaredConverted, qSquared);
        assertEquals(pInverseConverted, pInverse);
        assertEquals(muPConverted, muP);
        assertEquals(muQConverted, muQ);

        byte[] pubKeyBytes = PaillierUtils.pubKey2Bytes(pubKeyParams);

        SecureRandom random = new SecureRandom();
        byte[] data = new byte[256];
        random.nextBytes(data);

        byte[] ciphertextFromParams = PaillierUtils.encrypt(data,pubKeyParams);
        byte[] ciphertextFromBytes = PaillierUtils.encrypt(data,pubKeyBytes);

        assertEquals(512,ciphertextFromParams.length);
        assertEquals(512,ciphertextFromBytes.length);


        byte[] privKeyBytes = PaillierUtils.privKey2Bytes(privKeyParams);

        int input = 666;
        byte[] inputBytes = intToByteArray(input);

        ciphertextFromParams = PaillierUtils.encrypt(inputBytes,pubKeyParams);
        ciphertextFromBytes  = PaillierUtils.encrypt(inputBytes,pubKeyBytes);

        byte[] plaintextFromParams  = PaillierUtils.decrypt(ciphertextFromBytes,privKeyParams);
        byte[] plaintextFromBytes   = PaillierUtils.decrypt(ciphertextFromParams,privKeyBytes);

        int outputFromParams = byteArrayToInt(plaintextFromParams);
        int outputFromBytes = byteArrayToInt(plaintextFromBytes);

        assertEquals(input,outputFromParams);
        assertEquals(input,outputFromBytes);


        pubKeyBytes = PaillierUtils.pubKey2Bytes(pubKeyParams);

        int input1 = 600;
        int input2 = 60;
        int input3 = 6;

        int sum = 666;

        byte[] data1 = intToByteArray(input1);
        byte[] data2 = intToByteArray(input2);
        byte[] data3 = intToByteArray(input3);

        byte[] ciphertext1 = PaillierUtils.encrypt(data1,pubKeyParams);
        byte[] ciphertext2 = PaillierUtils.encrypt(data2,pubKeyParams);
        byte[] ciphertext3 = PaillierUtils.encrypt(data3,pubKeyParams);

        byte[] aggregatedCiphertext = PaillierUtils.add(pubKeyParams,ciphertext1,ciphertext2,ciphertext3);
        byte[] plaintext = PaillierUtils.decrypt(aggregatedCiphertext,privKeyParams);

        int output = byteArrayToInt(plaintext);
        assertEquals(sum,output);

        aggregatedCiphertext = PaillierUtils.add(pubKeyBytes,ciphertext1,ciphertext2,ciphertext3);
        plaintext = PaillierUtils.decrypt(aggregatedCiphertext,privKeyParams);

        output = byteArrayToInt(plaintext);
        assertEquals(sum,output);


        pubKeyBytes = PaillierUtils.pubKey2Bytes(pubKeyParams);

        input = 111;
        int scalar = 6;
        data = intToByteArray(input);

        byte[] ciphertext = PaillierUtils.encrypt(data,pubKeyParams);
        byte[] ciphertextPowered = PaillierUtils.scalarMultiply(pubKeyBytes,ciphertext,scalar);
        byte[] plaintextMultiplied = PaillierUtils.decrypt(ciphertextPowered,privKeyParams);

        output = byteArrayToInt(plaintextMultiplied);
        assertEquals(input * scalar, output);
    }

    private static byte[] intToByteArray(int input) {
        byte[] result = new byte[4];
        result[0] = (byte) ((input >> 24) & 0xFF);
        result[1] = (byte) ((input >> 16) & 0xFF);
        result[2] = (byte) ((input >> 8 ) & 0xFF);
        result[3] = (byte) ((input      ) & 0xFF);
        return result;
    }

    private static int byteArrayToInt(byte[] input) {
        int result;
        int length = input.length;
        byte[] buffer = new byte[4];
        if (length <= buffer.length){
            System.arraycopy(input,0,buffer,buffer.length - length,length);
        } else {
            System.arraycopy(input,length - buffer.length,buffer,0, buffer.length);
        }
        result =  buffer[3] & 0xFF |
                (buffer[2] & 0xFF) << 8 |
                (buffer[1] & 0xFF) << 16 |
                (buffer[0] & 0xFF) << 24;
        return result;
    }

    // To convert BigInteger to byte array in specified size
    private static byte[] bigIntegerToBytes(BigInteger b, int bytesSize){
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[bytesSize];
        if (tmp.length > result.length) {
            System.arraycopy(tmp, tmp.length - result.length, result, 0, result.length);
        }
        else {
            System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
        }
        return result;
    }
}
