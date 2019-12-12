package test.com.jd.blockchain.crypto.utils.classic;

import com.jd.blockchain.crypto.utils.classic.AESUtils;
import com.jd.blockchain.utils.io.BytesUtils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author zhanglin33
 * @title: AESUtilsTest
 * @description: Tests for methods in AESUtils
 * @date 2019-04-22, 16:06
 */
public class AESUtilsTest {

    @Test
    public void generateKeyTest(){
        byte[] key = AESUtils.generateKey();
        assertEquals(16,key.length);
        key = AESUtils.generateKey(BytesUtils.toBytes("abc"));
        assertArrayEquals(
                Hex.decode("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad".substring(0,32)),key);
    }


    @Test
    public void encryptTest(){
        String plaintext            = "abc";
        String key                  = "1234567890123456";
        String iv                   = "1234567890123456";
        String expectedCiphertextIn2ndBlock = "f479efae2d41d23227f61e675fced95c";

        byte[] ciphertext = AESUtils.encrypt(BytesUtils.toBytes(plaintext),
                BytesUtils.toBytes(key), BytesUtils.toBytes(iv));
        byte[] expectedCiphertext = BytesUtils.concat(BytesUtils.toBytes(iv),Hex.decode(expectedCiphertextIn2ndBlock));
        assertArrayEquals(expectedCiphertext,ciphertext);
    }


    @Test
    public void decryptTest(){

        Random random = new Random();
        byte[] data = new byte[1024];
        random.nextBytes(data);

        byte[] key = AESUtils.generateKey();

        byte[] ciphertext = AESUtils.encrypt(data,key);
        byte[] plaintext = AESUtils.decrypt(ciphertext,key);

        assertArrayEquals(data,plaintext);
    }


    public void encryptingPerformance() {

    byte[] data = new byte[1000];
    Random random = new Random();
    random.nextBytes(data);

    byte[] aesCiphertext = null;

    int count = 100000;


    byte[] aesKey = AESUtils.generateKey();

        System.out.println("=================== do AES encrypt test ===================");
        for (int r = 0; r < 5; r++) {
        System.out.println("------------- round[" + r + "] --------------");
        long startTS = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            aesCiphertext = AESUtils.encrypt(data, aesKey);
        }
        long elapsedTS = System.currentTimeMillis() - startTS;
        System.out.println(String.format("AES Encrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
                (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do AES decrypt test ===================");
        for (int r = 0; r < 5; r++) {
        System.out.println("------------- round[" + r + "] --------------");
        long startTS = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            AESUtils.decrypt(aesCiphertext, aesKey);
        }
        long elapsedTS = System.currentTimeMillis() - startTS;
        System.out.println(String.format("AES Decrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
                (count * 1000.00D) / elapsedTS));
        }
    }
}
