package test.com.jd.blockchain.crypto.smutils;

import com.jd.blockchain.crypto.smutils.symmetric.SM4Utils;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import static org.junit.Assert.*;

public class SM4UtilsTest {

    private static final int KEY_SIZE = 16;
    private static final int BLOCK_SIZE = 16;

    @Test
    public void testGenerateKey() {
        byte[] key = SM4Utils.generateKey();
        assertEquals(KEY_SIZE,key.length);
    }

    @Test
    public void testEncrypt() {

        String plaintext            = "0123456789abcdeffedcba9876543210";
        String key                  = "0123456789abcdeffedcba9876543210";
        String iv                   = "00000000000000000000000000000000";
        String expectedCiphertextIn2ndBlock = "681edf34d206965e86b3e94f536e4246";

        byte[] plaintextBytes = Hex.decode(plaintext);
        byte[] keyBytes = Hex.decode(key);
        byte[] ivBytes = Hex.decode(iv);
        byte[] expectedCiphertextIn2ndBlockBytes = Hex.decode(expectedCiphertextIn2ndBlock);


            byte[] ciphertextbytes = SM4Utils.encrypt(plaintextBytes,keyBytes,ivBytes);

            assertEquals(BLOCK_SIZE*3,ciphertextbytes.length);

            byte[] ciphertextIn1stBlockBytes = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertextbytes,0,ciphertextIn1stBlockBytes,0,BLOCK_SIZE);
            assertArrayEquals(ivBytes,ciphertextIn1stBlockBytes);

            byte[] ciphertextIn2ndBlockBytes = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertextbytes,BLOCK_SIZE,ciphertextIn2ndBlockBytes,0,BLOCK_SIZE);
            assertArrayEquals(expectedCiphertextIn2ndBlockBytes,ciphertextIn2ndBlockBytes);


    }

    @Test
    public void testDecrypt() {

        String plaintext            = "0123456789abcdeffedcba987654321000112233445566778899";
        String key                  = "0123456789abcdeffedcba9876543210";
        String iv                   = "0123456789abcdeffedcba9876543210";

        byte[] plaintextBytes = Hex.decode(plaintext);
        byte[] keyBytes = Hex.decode(key);
        byte[] ivBytes = Hex.decode(iv);


            byte[] ciphertext = SM4Utils.encrypt(plaintextBytes,keyBytes,ivBytes);
            byte[] decryptedData = SM4Utils.decrypt(ciphertext,keyBytes);
            assertArrayEquals(plaintextBytes,decryptedData);

    }
}