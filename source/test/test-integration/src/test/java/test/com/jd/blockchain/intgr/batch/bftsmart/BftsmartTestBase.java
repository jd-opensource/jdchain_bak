/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: BftsmartTestBase
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/10 下午3:32
 * Description:
 */
package test.com.jd.blockchain.intgr.batch.bftsmart;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoUtils;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.security.ShaUtils;
import org.junit.Test;

import static com.jd.blockchain.tools.keygen.KeyGenCommand.encodePrivKey;
import static com.jd.blockchain.tools.keygen.KeyGenCommand.encodePubKey;

/**
 *
 * @author shaozhuguang
 * @create 2019/1/10
 * @since 1.0.0
 */

public class BftsmartTestBase {

    private final int userSize = 64;

    private final byte[] pwdBytes= ShaUtils.hash_256("abc".getBytes());

    private final String base58Pwd = Base58Utils.encode(pwdBytes);

    @Test
    public void newUsers() {
        for (int i = 0; i < userSize; i++) {
            CryptoKeyPair kp = CryptoUtils.sign(CryptoAlgorithm.ED25519).generateKeyPair();

            String base58PubKey = encodePubKey(kp.getPubKey());

            String base58PrivKey = encodePrivKey(kp.getPrivKey(), pwdBytes);

            System.out.printf("user[%s] privKeyBase58 = %s \r\n", i, base58PrivKey);
            System.out.printf("user[%s] pubKeyBase58 = %s \r\n", i, base58PubKey);
            System.out.println("------------------------------------------------------");
        }
        System.out.printf("pwdBase58 = %s \r\n", base58Pwd);
    }
}