package com.jd.blockchain.crypto.utils;

import com.jd.blockchain.crypto.CryptoException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStrictStyle;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64;


import java.io.IOException;
import java.security.*;

/**
 * @author zhanglin33
 * @title: CSRBuilder
 * @description: A builder for certificate signing request, supporting rsa and sm2
 * @date 2019-05-10, 15:10
 */
public class CSRBuilder {

    private String BC = BouncyCastleProvider.PROVIDER_NAME;

    private PublicKey pubKey;
    private PrivateKey privKey;

    private String algoName;

    public void init() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        algoName = "SHA1withRSA";
        KeyPairGenerator generator;
        try {
            generator = KeyPairGenerator.getInstance("RSA", BC);
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            pubKey  = keyPair.getPublic();
            privKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    public void init(String algoName, int KeyLength) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        this.algoName = algoName;

        KeyPairGenerator generator;
        KeyPair keyPair;
        String[] hashAndSignature = algoName.split("with");

        try {
            switch (hashAndSignature[1]) {
                case "RSA": {
                    generator = KeyPairGenerator.getInstance("RSA", BC);
                    generator.initialize(KeyLength);
                    keyPair = generator.generateKeyPair();
                    pubKey = keyPair.getPublic();
                    privKey = keyPair.getPrivate();
                }

                case "SM2": {
                    generator = KeyPairGenerator.getInstance("EC", BC);
                    generator.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"));
                    keyPair = generator.generateKeyPair();
                    pubKey = keyPair.getPublic();
                    privKey = keyPair.getPrivate();
                }

                default: throw new CryptoException("Unsupported key algorithm[" + algoName + "] in CSR!");
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    public String buildRequest(String countryName, String stateName, String cityName,
                               String organizationName, String departmentName, String domainName,
                               String emailName) {

        String result = null;
        X500NameBuilder nameBuilder = new X500NameBuilder(BCStrictStyle.INSTANCE);

        nameBuilder.addRDN(BCStyle.C, countryName); // a country name, and China is short as CN
        nameBuilder.addRDN(BCStyle.ST, stateName); // a state or province name
        nameBuilder.addRDN(BCStyle.L, cityName); // a city name
        nameBuilder.addRDN(BCStyle.O, organizationName); // an organization or corporation name
        nameBuilder.addRDN(BCStyle.OU, departmentName); // a division of your organization name
        nameBuilder.addRDN(BCStyle.CN, domainName); // a fully qualified domain name
        nameBuilder.addRDN(BCStyle.E, emailName); // an email address

        try {
            X500Name x500Name = nameBuilder.build();

            PKCS10CertificationRequestBuilder requestBuilder
                    = new JcaPKCS10CertificationRequestBuilder(x500Name, pubKey);
            PKCS10CertificationRequest request
                    = requestBuilder.build(new JcaContentSignerBuilder(algoName).setProvider(BC).build(privKey));
            byte[] csrBytes = request.getEncoded();
            result = Base64.toBase64String(csrBytes);
        } catch (OperatorCreationException | IOException e) {
            throw new CryptoException(e.getMessage(), e);
        }

        return result;
    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public PrivateKey getPrivKey() {
        return privKey;
    }

    public byte[] getPubKeyBytes() {
        return pubKey.getEncoded();
    }

    public byte[] getPrivKeyBytes() {
        return privKey.getEncoded();
    }
}
