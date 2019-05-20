package com.jd.blockchain.crypto.utils;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;

import java.io.IOException;
import java.security.*;

import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: CSRBuilderTest
 * @description: TODO
 * @date 2019-05-10, 17:22
 */
public class CSRBuilderTest {

    @Test
    public void defaultCSRTest(){

        String countryName = "CN";
        String stateName = "Beijing";
        String cityName = "Beijing";
        String organizationName = "JD.com";
        String departmentName = "Blockchain Department";
        String domainName = "ledger.jd.com";
        String emailName = "zhanglin33@jd.com";

        CSRBuilder builder = new CSRBuilder();

        builder.init();
        String csr = builder.buildRequest(countryName,stateName,cityName,
                                          organizationName,departmentName,domainName,
                                          emailName);

        PublicKey pubKey = builder.getPubKey();
        PrivateKey privKey = builder.getPrivKey();

        byte[] crsBytes = Base64.decode(csr);
        PKCS10CertificationRequest request = null;
        try {
            request = new PKCS10CertificationRequest(crsBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(request);
        assertEquals("1.2.840.113549.1.1.5",request.getSignatureAlgorithm().getAlgorithm().getId());
        byte[] pubKeyBytes = new byte[0];
        try {
            pubKeyBytes = request.getSubjectPublicKeyInfo().getEncoded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertArrayEquals(pubKeyBytes,pubKey.getEncoded());

        RDN[] rdns = request.getSubject().getRDNs();
        assertEquals(BCStyle.C,  rdns[0].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.ST, rdns[1].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.L,  rdns[2].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.O,  rdns[3].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.OU, rdns[4].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.CN, rdns[5].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.E,  rdns[6].getFirst().getType().toASN1Primitive());

        assertEquals("CN",  rdns[0].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Beijing", rdns[1].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Beijing",  rdns[2].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("JD.com",  rdns[3].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Blockchain Department", rdns[4].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("ledger.jd.com", rdns[5].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("zhanglin33@jd.com",  rdns[6].getFirst().getValue().toASN1Primitive().toString());

        byte[] signature = request.getSignature();

        CertificationRequestInfo requestInfo = new CertificationRequestInfo(request.getSubject(),request.getSubjectPublicKeyInfo(),new DERSet());
        byte[] message = new byte[0];
        try {
            message = requestInfo.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Signature signer;
        byte[] result = new byte[0];
        try {
            signer = Signature.getInstance("SHA1withRSA");
            signer.initSign(privKey);
            signer.update(message);
            result = signer.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        assertArrayEquals(result,signature);

        Signature verifier;
        boolean isValid = false;
        try {
            verifier = Signature.getInstance("SHA1withRSA");
            verifier.initVerify(pubKey);
            verifier.update(message);
            isValid = verifier.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        assertTrue(isValid);
    }

    @Test
    public void SHA1withRSA2048CSRTest(){

        String countryName = "CN";
        String stateName = "Beijing";
        String cityName = "Beijing";
        String organizationName = "JD.com";
        String departmentName = "Blockchain Department";
        String domainName = "ledger.jd.com";
        String emailName = "zhanglin33@jd.com";

        CSRBuilder builder = new CSRBuilder();

        builder.init("SHA1withRSA",2048);
        String csr = builder.buildRequest(countryName,stateName,cityName,
                organizationName,departmentName,domainName,
                emailName);

        PublicKey pubKey = builder.getPubKey();
        PrivateKey privKey = builder.getPrivKey();

        byte[] crsBytes = Base64.decode(csr);
        PKCS10CertificationRequest request = null;
        try {
            request = new PKCS10CertificationRequest(crsBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(request);
        assertEquals("1.2.840.113549.1.1.5",request.getSignatureAlgorithm().getAlgorithm().getId());
        byte[] pubKeyBytes = new byte[0];
        try {
            pubKeyBytes = request.getSubjectPublicKeyInfo().getEncoded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertArrayEquals(pubKeyBytes,pubKey.getEncoded());

        RDN[] rdns = request.getSubject().getRDNs();
        assertEquals(BCStyle.C,  rdns[0].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.ST, rdns[1].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.L,  rdns[2].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.O,  rdns[3].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.OU, rdns[4].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.CN, rdns[5].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.E,  rdns[6].getFirst().getType().toASN1Primitive());

        assertEquals("CN",  rdns[0].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Beijing", rdns[1].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Beijing",  rdns[2].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("JD.com",  rdns[3].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Blockchain Department", rdns[4].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("ledger.jd.com", rdns[5].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("zhanglin33@jd.com",  rdns[6].getFirst().getValue().toASN1Primitive().toString());

        byte[] signature = request.getSignature();

        CertificationRequestInfo requestInfo = new CertificationRequestInfo(request.getSubject(),request.getSubjectPublicKeyInfo(),new DERSet());
        byte[] message = new byte[0];
        try {
            message = requestInfo.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Signature signer;
        byte[] result = new byte[0];
        try {
            signer = Signature.getInstance("SHA1withRSA");
            signer.initSign(privKey);
            signer.update(message);
            result = signer.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        assertArrayEquals(result,signature);

        Signature verifier;
        boolean isValid = false;
        try {
            verifier = Signature.getInstance("SHA1withRSA");
            verifier.initVerify(pubKey);
            verifier.update(message);
            isValid = verifier.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        assertTrue(isValid);
    }


//    @Test
    public void SHA1withRSA4096CSRTest(){

        String countryName = "CN";
        String stateName = "Beijing";
        String cityName = "Beijing";
        String organizationName = "JD.com";
        String departmentName = "Blockchain Department";
        String domainName = "ledger.jd.com";
        String emailName = "zhanglin33@jd.com";

        CSRBuilder builder = new CSRBuilder();

        builder.init("SHA1withRSA",4096);
        String csr = builder.buildRequest(countryName,stateName,cityName,
                organizationName,departmentName,domainName,
                emailName);

        PublicKey pubKey = builder.getPubKey();
        PrivateKey privKey = builder.getPrivKey();

        byte[] crsBytes = Base64.decode(csr);
        PKCS10CertificationRequest request = null;
        try {
            request = new PKCS10CertificationRequest(crsBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(request);
        assertEquals("1.2.840.113549.1.1.5",request.getSignatureAlgorithm().getAlgorithm().getId());
        byte[] pubKeyBytes = new byte[0];
        try {
            pubKeyBytes = request.getSubjectPublicKeyInfo().getEncoded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertArrayEquals(pubKeyBytes,pubKey.getEncoded());

        RDN[] rdns = request.getSubject().getRDNs();
        assertEquals(BCStyle.C,  rdns[0].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.ST, rdns[1].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.L,  rdns[2].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.O,  rdns[3].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.OU, rdns[4].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.CN, rdns[5].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.E,  rdns[6].getFirst().getType().toASN1Primitive());

        assertEquals("CN",  rdns[0].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Beijing", rdns[1].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Beijing",  rdns[2].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("JD.com",  rdns[3].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Blockchain Department", rdns[4].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("ledger.jd.com", rdns[5].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("zhanglin33@jd.com",  rdns[6].getFirst().getValue().toASN1Primitive().toString());

        byte[] signature = request.getSignature();

        CertificationRequestInfo requestInfo = new CertificationRequestInfo(request.getSubject(),request.getSubjectPublicKeyInfo(),new DERSet());
        byte[] message = new byte[0];
        try {
            message = requestInfo.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Signature signer;
        byte[] result = new byte[0];
        try {
            signer = Signature.getInstance("SHA1withRSA");
            signer.initSign(privKey);
            signer.update(message);
            result = signer.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        assertArrayEquals(result,signature);

        Signature verifier;
        boolean isValid = false;
        try {
            verifier = Signature.getInstance("SHA1withRSA");
            verifier.initVerify(pubKey);
            verifier.update(message);
            isValid = verifier.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        assertTrue(isValid);
    }


    @Test
    public void SM3withSM2CSRTest(){

        String countryName = "CN";
        String stateName = "Beijing";
        String cityName = "Beijing";
        String organizationName = "JD.com";
        String departmentName = "Blockchain Department";
        String domainName = "ledger.jd.com";
        String emailName = "zhanglin33@jd.com";

        CSRBuilder builder = new CSRBuilder();

        builder.init("SM3withSM2",256);
        String csr = builder.buildRequest(countryName,stateName,cityName,
                organizationName,departmentName,domainName,
                emailName);

        System.out.println(csr);
        PublicKey pubKey = builder.getPubKey();
        PrivateKey privKey = builder.getPrivKey();

        byte[] crsBytes = Base64.decode(csr);
        PKCS10CertificationRequest request = null;
        try {
            request = new PKCS10CertificationRequest(crsBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(request);
        assertEquals("1.2.156.10197.1.501",request.getSignatureAlgorithm().getAlgorithm().getId());
        byte[] pubKeyBytes = new byte[0];
        try {
            pubKeyBytes = request.getSubjectPublicKeyInfo().getEncoded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertArrayEquals(pubKeyBytes,pubKey.getEncoded());

        RDN[] rdns = request.getSubject().getRDNs();
        assertEquals(BCStyle.C,  rdns[0].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.ST, rdns[1].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.L,  rdns[2].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.O,  rdns[3].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.OU, rdns[4].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.CN, rdns[5].getFirst().getType().toASN1Primitive());
        assertEquals(BCStyle.E,  rdns[6].getFirst().getType().toASN1Primitive());

        assertEquals("CN",  rdns[0].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Beijing", rdns[1].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Beijing",  rdns[2].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("JD.com",  rdns[3].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("Blockchain Department", rdns[4].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("ledger.jd.com", rdns[5].getFirst().getValue().toASN1Primitive().toString());
        assertEquals("zhanglin33@jd.com",  rdns[6].getFirst().getValue().toASN1Primitive().toString());

        byte[] signature = request.getSignature();

        CertificationRequestInfo requestInfo = new CertificationRequestInfo(request.getSubject(),request.getSubjectPublicKeyInfo(),new DERSet());
        byte[] message = new byte[0];
        try {
            message = requestInfo.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Signature signer;
        byte[] result = new byte[0];
        try {
            signer = Signature.getInstance("SM3withSM2");
            signer.initSign(privKey);
            signer.update(message);
            result = signer.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }

        Signature verifier;
        boolean isValid = false;
        try {
            verifier = Signature.getInstance("SM3withSM2");
            verifier.initVerify(pubKey);
            verifier.update(message);
            isValid = verifier.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        assertTrue(isValid);

        try {
            verifier = Signature.getInstance("SM3withSM2");
            verifier.initVerify(pubKey);
            verifier.update(message);
            isValid = verifier.verify(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        assertTrue(isValid);
    }
}
