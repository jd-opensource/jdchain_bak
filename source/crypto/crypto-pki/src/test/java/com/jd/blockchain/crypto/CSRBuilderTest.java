package com.jd.blockchain.crypto;

import com.jd.blockchain.crypto.utils.classic.RSAUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStrictStyle;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;
import sun.security.rsa.RSAPublicKeyImpl;

import java.io.IOException;
import java.security.*;

import static org.junit.Assert.fail;

/**
 * @author zhanglin33
 * @title: CSRBuilderTest
 * @description: TODO
 * @date 2019-05-10, 17:22
 */
public class CSRBuilderTest {
    public static String genCSR(String subject, String alg, String provider) {
        String signalg = "";
        int alglength = 0;
        String keyAlg = "";
        if (alg.toUpperCase().equals("RSA1024")) {
            signalg = "SHA1WithRSA";
            alglength = 1024;
            keyAlg = "RSA";
        } else if (alg.toUpperCase().equals("RSA2048")) {
            signalg = "SHA1WithRSA";
            alglength = 2048;
            keyAlg = "RSA";
        } else if (alg.toUpperCase().equals("SM2")) {
            signalg = "SM3withSM2";
            alglength = 256;
            keyAlg = "SM2";
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

    @Test
    public void csrTest() throws Exception{

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());


        String keyName = "RSA";
        String sigName = "SHA1withRSA";

        String BC = BouncyCastleProvider.PROVIDER_NAME;
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(keyName, BC);

        kpg.initialize(2048);

        KeyPair kp = kpg.generateKeyPair();
//        AsymmetricCipherKeyPair keyPair = RSAUtils.generateKeyPair();


        X500NameBuilder builder = new X500NameBuilder(BCStrictStyle.INSTANCE);

        builder.addRDN(BCStyle.C,"CN"); // a country name, and China is short as CN
        builder.addRDN(BCStyle.ST, "Beijing"); // a state or province name
        builder.addRDN(BCStyle.L, "Beijing"); // a city name
        builder.addRDN(BCStyle.O, "JD.com"); // an organization or corporation name
        builder.addRDN(BCStyle.OU, "Blockchain Department"); // a division of your organization name
        builder.addRDN(BCStyle.CN, "ledger.jd.com"); // a fully qualified domain name
        builder.addRDN(BCStyle.EmailAddress, "zhanglin33@jd.com"); // an email address

        X500Name x500Name = builder.build();

        PKCS10CertificationRequestBuilder requestBuilder = new JcaPKCS10CertificationRequestBuilder(x500Name, kp.getPublic());

//        PKCS10CertificationRequest req = requestBuilder.build(new JcaContentSignerBuilder(sigName).setProvider(sigName).build(kp.getPrivate()));
        PKCS10CertificationRequest req = requestBuilder.build(new JcaContentSignerBuilder(sigName).setProvider(BC).build(kp.getPrivate()));


        byte[] csrBytes = req.getEncoded();
        String result = Base64.toBase64String(csrBytes);
        System.out.println(result);

    }

    @Test
    public void csr2Test() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        generationTest(512, "RSA", "SHA1withRSA", "BC");
    }

    private void generationTest(int keySize, String keyName, String sigName, String provider)
            throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(keyName, "BC");

        kpg.initialize(keySize);

        KeyPair kp = kpg.generateKeyPair();


        X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);

        x500NameBld.addRDN(BCStyle.C, "AU");
        x500NameBld.addRDN(BCStyle.O, "The Legion of the Bouncy Castle");
        x500NameBld.addRDN(BCStyle.L, "Melbourne");
        x500NameBld.addRDN(BCStyle.ST, "Victoria");

        x500NameBld.addRDN(BCStyle.EmailAddress, "feedback-crypto@bouncycastle.org");

        X500Name subject = x500NameBld.build();

        PKCS10CertificationRequestBuilder requestBuilder = new JcaPKCS10CertificationRequestBuilder(subject, kp.getPublic());
        BCRSAPublicKey pubKey = (BCRSAPublicKey) kp.getPublic();
        System.out.println(pubKey.getModulus().bitLength());

        PKCS10CertificationRequest req1 = requestBuilder.build(new JcaContentSignerBuilder(sigName).setProvider(provider).build(kp.getPrivate()));

        JcaPKCS10CertificationRequest req2 = new JcaPKCS10CertificationRequest(req1.getEncoded()).setProvider(provider);

        if (!req2.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(provider).build(kp.getPublic()))) {
            fail(sigName + ": Failed verify check.");
        }

        if (!Arrays.areEqual(req2.getPublicKey().getEncoded(), req1.getSubjectPublicKeyInfo().getEncoded())) {
            fail(keyName + ": Failed public key check.");
        }
    }

}
