package test.com.jd.blockchain.crypto.performance;

import com.jd.blockchain.crypto.Ciphertext;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.PrivKey;
import com.jd.blockchain.crypto.asymmetric.PubKey;
import com.jd.blockchain.crypto.impl.sm.asymmetric.SM2CryptoFunction;
import org.bouncycastle.util.encoders.Hex;

public class MyAsymmetricEncryptionTest {

    public static void main(String[] args) {

        String string1K = "0123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba98765432100123456789abcdeffedcba9876543210";
        String string1M = "";
        for (int i = 0; i < 1024 ; i++)
        {
            string1M = string1M + string1K;
        }

        byte[] data1K = Hex.decode(string1K);
        byte[] data1M = Hex.decode(string1M);
        int count = 10000;

        SM2CryptoFunction sm2 = new SM2CryptoFunction();
        CryptoKeyPair keyPairSM2 = sm2.generateKeyPair();
        PrivKey privKeySM2 = keyPairSM2.getPrivKey();
        PubKey pubKeySM2 = keyPairSM2.getPubKey();

        System.out.println("=================== do SM2 encrypt test ===================");
        Ciphertext ciphertextSM2 = null;
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                ciphertextSM2 = sm2.encrypt(pubKeySM2,data1K);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("SM2 Encrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }

        System.out.println("=================== do SM2 decrypt test ===================");
        for (int r = 0; r < 5; r++) {
            System.out.println("------------- round[" + r + "] --------------");
            long startTS = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
                sm2.decrypt(privKeySM2,ciphertextSM2);
            }
            long elapsedTS = System.currentTimeMillis() - startTS;
            System.out.println(String.format("SM2 Decrypting Count=%s; Elapsed Times=%s; KBPS=%.2f", count, elapsedTS,
                    (count * 1000.00D) / elapsedTS));
        }
    }
}
