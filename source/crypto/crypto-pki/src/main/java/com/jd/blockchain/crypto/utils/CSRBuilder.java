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
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author zhanglin33
 * @title: CSRBuilder
 * @description: A builder for certificate signing request, supporting rsa and sm2
 * @date 2019-05-10, 15:10
 */
public class CSRBuilder {

    private final String BC = BouncyCastleProvider.PROVIDER_NAME;

    private PublicKey pubKey;
    private PrivateKey privKey;

    private String algoName;
    private int keyLength;

    public void init() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        algoName = "SHA1withRSA";
        keyLength = 2048;
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

    public void init(String algoName, int keyLength) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        this.algoName  = algoName;
        this.keyLength = keyLength;

        KeyPairGenerator generator;
        KeyPair keyPair;
        String[] hashAndSignature = algoName.split("with");

        try {
            switch (hashAndSignature[1]) {
                case "RSA": {
                    generator = KeyPairGenerator.getInstance("RSA", BC);
                    generator.initialize(keyLength);
                    break;
                }

                case "SM2": {
                    generator = KeyPairGenerator.getInstance("EC", BC);
                    if (keyLength != 256) {
                        throw new CryptoException("SM3withSM2 with unsupported key length [" +
                                keyLength +"] in CSR!");
                    }
                    generator.initialize(new ECNamedCurveGenParameterSpec("sm2p256v1"));
                    break;
                }

                default: throw new CryptoException("Unsupported algorithm [" + algoName + "] with key length [" +
                        keyLength +"] in CSR!");
            }
            keyPair = generator.generateKeyPair();
            pubKey = keyPair.getPublic();
            privKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    public void init(String algoName, byte[] pubKeyBytes, byte[] privKeyBytes) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        this.algoName = algoName;
        String[] hashAndSignature = algoName.split("with");

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privKeyBytes);

        KeyFactory keyFactory;

        try {
            switch (hashAndSignature[1]) {
                case "RSA": {
                    keyFactory = KeyFactory.getInstance("RSA");
                    privKey = keyFactory.generatePrivate(privKeySpec);
                    pubKey  = keyFactory.generatePublic(pubKeySpec);
                    keyLength = (pubKey.getEncoded().length < 4096 / 8)? 2048: 4096;
                    break;
                }

                case "SM2": {
                    keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
                    privKey = keyFactory.generatePrivate(privKeySpec);
                    pubKey  = keyFactory.generatePublic(pubKeySpec);
                    keyLength = 256;
                    break;
                }

                default: throw new CryptoException("Unsupported algorithm [" + algoName + "] with the given key pair!");
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CryptoException(e.getMessage(), e);
        }


    }

    public String buildRequest(String countryName, String stateName, String cityName,
                               String organizationName, String departmentName, String domainName,
                               String emailName) {

        String result;
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

    public String getAlgoName() {
        return algoName;
    }

    public int getKeyLength() {
        return keyLength;
    }
}
