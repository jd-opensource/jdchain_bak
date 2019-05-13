package test.com.jd.blockchain.crypto.utils.classic;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;


import java.io.IOException;
import java.security.*;

/**
 * @author zhanglin33
 * @title: CSRTest
 * @description: TODO
 * @date 2019-05-10, 12:55
 */
public class CSRTest {
    public static String genCSR(String subject, String alg,String provider) {
        String signalg="";
        int alglength=0;
        String keyAlg="";
        if(alg.toUpperCase().equals("RSA1024")){
            signalg="SHA1WithRSA";
            alglength=1024;
            keyAlg="RSA";
        }else if(alg.toUpperCase().equals("RSA2048")){
            signalg="SHA1WithRSA";
            alglength=2048;
            keyAlg="RSA";
        }else if(alg.toUpperCase().equals("SM2")){
            signalg="SM3withSM2";
            alglength=256;
            keyAlg="SM2";
        }
        KeyPairGenerator keyGen;
        PKCS10CertificationRequestBuilder builder;

        try {
            keyGen = KeyPairGenerator.getInstance(keyAlg);
            keyGen.initialize(alglength);
            KeyPair kp = keyGen.generateKeyPair();


            builder = new PKCS10CertificationRequestBuilder(new X500Name(subject), SubjectPublicKeyInfo.getInstance(kp.getPublic().getEncoded()));
            JcaContentSignerBuilder jcaContentSignerBuilder = new JcaContentSignerBuilder(signalg);
            jcaContentSignerBuilder.setProvider(provider);
            ContentSigner contentSigner = jcaContentSignerBuilder.build(kp.getPrivate());
            builder.build(contentSigner);
            return builder.toString();
        } catch (OperatorCreationException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
