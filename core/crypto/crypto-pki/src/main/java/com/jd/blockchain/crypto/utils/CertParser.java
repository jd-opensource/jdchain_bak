package com.jd.blockchain.crypto.utils;

import com.jd.blockchain.crypto.CryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemReader;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.cert.*;
import java.util.Date;

/**
 * @author zhanglin33
 * @title: CertParser
 * @description: A parser for standard certificate, along with validation process
 * @date 2019-05-10, 15:17
 */
public class CertParser {

    private PublicKey pubKey;
    private String sigAlgName;
    private String userName;
    private String issuerName;
    private int keyLength;

    private Date startTime;
    private Date endTime;

    public void parse(String userCertificate, String issuerCertificate) {

        X509Certificate issuerCert = parseWithoutValidationProcess(issuerCertificate);

        // ensure that the certificate is within the validity period
        try {
            issuerCert.checkValidity();
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            throw new CryptoException(e.getMessage(), e);
        }
        PublicKey issuerPubKey = issuerCert.getPublicKey();
        X500Principal issuerPrincipal = issuerCert.getSubjectX500Principal();

        X509Certificate userCert = parseWithoutValidationProcess(userCertificate);

        // check consistency between issuer's names in userCertificate and issuerCertificate
        if (!userCert.getIssuerX500Principal().equals(issuerPrincipal)) {
            throw new CryptoException("Issuer in the targeted certificate is not " +
                    "compliance with the parent certificate！");
        }

        try {
            userCert.checkValidity();
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
            throw new CryptoException(e.getMessage(), e);
        }

        // verify the signature in certificate with issuer's public key
        try {
            userCert.verify(issuerPubKey);
        } catch (CertificateException | NoSuchAlgorithmException
                | InvalidKeyException | NoSuchProviderException | SignatureException e) {
            throw new CryptoException(e.getMessage(), e);
        }

        startTime = userCert.getNotBefore();
        endTime = userCert.getNotAfter();

        pubKey = userCert.getPublicKey();
        sigAlgName = userCert.getSigAlgName();
        issuerName = userCert.getIssuerX500Principal().getName();
        userName   = userCert.getSubjectX500Principal().getName();

        switch (sigAlgName) {
            case "SM3WITHSM2": {
                keyLength = 256;
                break;
            }
            case "SHA1WITHRSA": {
                keyLength = (pubKey.getEncoded().length < 4096 / 8)? 2048: 4096;
                break;
            }
        }
    }

    // certificate string in Base64 format
    public X509Certificate parseWithoutValidationProcess(String certificate) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        byte[] certificateBytes;
        String BEGIN = "-----BEGIN CERTIFICATE-----";
        String END   = "-----END CERTIFICATE-----";
        if (!certificate.startsWith(BEGIN)) {
            certificate = certificate.replaceAll("\\n", "");
            certificate = certificate.replaceAll(END, "");
            certificateBytes = Base64.decode(certificate);
        } else {
            try {
                certificateBytes = new PemReader(new StringReader(certificate)).readPemObject().getContent();
            } catch (IOException e) {
                throw new CryptoException(e.getMessage(), e);
            }
        }

        ByteArrayInputStream bytesIn = new ByteArrayInputStream(certificateBytes);
        CertificateFactory factory;
        X509Certificate cert;
        try {
            factory = CertificateFactory.getInstance("X509", BouncyCastleProvider.PROVIDER_NAME);
            cert = (X509Certificate) factory.generateCertificate(bytesIn);
        } catch (CertificateException | NoSuchProviderException e) {
            throw new CryptoException(e.getMessage(), e);
        }
        return cert;
    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public String getSigAlgName() {
        return sigAlgName;
    }

    public String getUserName() {
        return userName;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
