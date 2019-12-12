/**
 * Copyright: Copyright 2016-2020 JD.COM All Right Reserved
 * FileName: BftsmartTestBase
 * Author: shaozhuguang
 * Department: 区块链研发部
 * Date: 2019/1/10 下午3:32
 * Description:
 */
package test.com.jd.blockchain.intgr.batch.bftsmart;

import static com.jd.blockchain.crypto.KeyGenUtils.encodePrivKey;
import static com.jd.blockchain.crypto.KeyGenUtils.encodePubKey;

import org.junit.Test;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.security.ShaUtils;

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
    	SignatureFunction signFunc = Crypto.getSignatureFunction("ED25519");
        for (int i = 0; i < userSize; i++) {
            AsymmetricKeypair kp = signFunc.generateKeypair();

            String base58PubKey = encodePubKey(kp.getPubKey());

            String base58PrivKey = encodePrivKey(kp.getPrivKey(), pwdBytes);

            System.out.printf("user[%s] privKeyBase58 = %s \r\n", i, base58PrivKey);
            System.out.printf("user[%s] pubKeyBase58 = %s \r\n", i, base58PubKey);
            System.out.println("------------------------------------------------------");
        }
        System.out.printf("pwdBase58 = %s \r\n", base58Pwd);
    }
}