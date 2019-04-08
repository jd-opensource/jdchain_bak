package test.com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoServiceProviders;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.asymmetric.CryptoKeyPair;
import com.jd.blockchain.crypto.asymmetric.SignatureDigest;
import com.jd.blockchain.crypto.asymmetric.SignatureFunction;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.util.Random;

import static com.jd.blockchain.crypto.CryptoKeyType.PRIVATE;
import static com.jd.blockchain.crypto.CryptoKeyType.PUBLIC;
import static org.junit.Assert.*;

/**
 * @author zhanglin33
 * @title: ED25519SignatureFunctionTest
 * @description: JunitTest for ED25519SignatureFunction in SPI mode
 * @date 2019-04-01, 14:01
 */
public class ED25519SignatureFunctionTest {
    @Test
    public void getAlgorithmTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);


        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("Ed25519");
        assertNotNull(algorithm);

        assertEquals(signatureFunction.getAlgorithm().name(), algorithm.name());
        assertEquals(signatureFunction.getAlgorithm().code(), algorithm.code());

        algorithm = CryptoServiceProviders.getAlgorithm("eddsa");
        assertNull(algorithm);
    }

    @Test
    public void generateKeyPairTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);


        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();

        assertEquals(PUBLIC.CODE,pubKey.getKeyType().CODE);
        assertEquals(32, pubKey.getRawKeyBytes().length);
        assertEquals(PRIVATE.CODE,privKey.getKeyType().CODE);
        assertEquals(32, privKey.getRawKeyBytes().length);

        assertEquals(algorithm.name(),pubKey.getAlgorithm().name());
        assertEquals(algorithm.toString(),pubKey.getAlgorithm().toString());
        assertEquals(algorithm.name(),privKey.getAlgorithm().name());
        assertEquals(algorithm.toString(),privKey.getAlgorithm().toString());

        assertEquals(2 + 1 + 32, pubKey.toBytes().length);
        assertEquals(2 + 1 + 32, privKey.toBytes().length);

        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
        byte[] privKeyTypeBytes = new byte[] {PRIVATE.CODE};
        byte[] rawPubKeyBytes = pubKey.getRawKeyBytes();
        byte[] rawPrivKeyBytes = privKey.getRawKeyBytes();
        assertArrayEquals(BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawPubKeyBytes),pubKey.toBytes());
        assertArrayEquals(BytesUtils.concat(algoBytes,privKeyTypeBytes,rawPrivKeyBytes),privKey.toBytes());



    }



    @Test
    public void signTest(){

        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();
        SignatureDigest signatureDigest = signatureFunction.sign(privKey,data);

        byte[] signatureBytes = signatureDigest.toBytes();

        assertEquals(2 + 64, signatureBytes.length);
        CryptoAlgorithm signatureAlgo = signatureDigest.getAlgorithm();
        assertEquals(algorithm.name(),signatureDigest.getAlgorithm().name());
        assertEquals(algorithm.toString(),signatureDigest.getAlgorithm().toString());

        byte[] algoBytes = CryptoAlgorithm.toBytes(signatureAlgo);
        byte[] rawSinatureBytes = signatureDigest.getRawDigest();
        assertArrayEquals(BytesUtils.concat(algoBytes,rawSinatureBytes),signatureBytes);
    }

    @Test
    public void verifyTest(){
        byte[] data = new byte[1024];
        Random random = new Random();
        random.nextBytes(data);

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);

        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PubKey pubKey = keyPair.getPubKey();
        PrivKey privKey = keyPair.getPrivKey();
        SignatureDigest signatureDigest = signatureFunction.sign(privKey,data);

        assertTrue(signatureFunction.verify(signatureDigest,pubKey,data));
    }


    @Test
    public void supportPrivKeyTest(){

        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("ed25519");
        assertNotNull(algorithm);

        SignatureFunction signatureFunction = CryptoServiceProviders.getSignatureFunction(algorithm);


        CryptoKeyPair keyPair = signatureFunction.generateKeyPair();

        PrivKey privKey = keyPair.getPrivKey();
        byte[] privKeyBytes = privKey.toBytes();

        assertTrue(signatureFunction.supportPrivKey(privKeyBytes));

        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
        assertNotNull(algorithm);
        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
        byte[] rawKeyBytes = privKey.getRawKeyBytes();
        byte[] ripemd160PubKeyBytes = BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawKeyBytes);

        assertFalse(signatureFunction.supportPrivKey(ripemd160PubKeyBytes));
    }

//    @Test
//    public void resolveSymmetricKeyTest(){
//        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
//        assertNotNull(algorithm);
//
//        SymmetricEncryptionFunction symmetricEncryptionFunction =
//                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);
//
//        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();
//        byte[] symmetricKeyBytes = symmetricKey.toBytes();
//
//        SymmetricKey resolvedKey = symmetricEncryptionFunction.resolveSymmetricKey(symmetricKeyBytes);
//
//        assertEquals(SYMMETRIC.CODE,resolvedKey.getKeyType().CODE);
//        assertEquals(128 / 8, resolvedKey.getRawKeyBytes().length);
//
//        assertEquals(algorithm.name(),resolvedKey.getAlgorithm().name());
//        assertEquals(algorithm.toString(),resolvedKey.getAlgorithm().toString());
//
//
//        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
//        assertNotNull(algorithm);
//        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
//        byte[] pubKeyTypeBytes = new byte[] {PUBLIC.CODE};
//        byte[] rawKeyBytes = symmetricKey.getRawKeyBytes();
//        byte[] ripemd160KeyBytes = BytesUtils.concat(algoBytes,pubKeyTypeBytes,rawKeyBytes);
//
//
//        Class<?> expectedException = CryptoException.class;
//        Exception actualEx = null;
//        try {
//            symmetricEncryptionFunction.resolveSymmetricKey(ripemd160KeyBytes);
//        } catch (Exception e) {
//            actualEx = e;
//        }
//        assertNotNull(actualEx);
//        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//    }
//
//    @Test
//    public void supportCiphertextTest(){
//
//        byte[] data = new byte[1024];
//        Random random = new Random();
//        random.nextBytes(data);
//
//        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
//        assertNotNull(algorithm);
//
//        SymmetricEncryptionFunction symmetricEncryptionFunction =
//                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);
//
//        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();
//
//        Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey,data);
//
//        byte[] ciphertextBytes = ciphertext.toBytes();
//        assertTrue(symmetricEncryptionFunction.supportCiphertext(ciphertextBytes));
//
//        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
//        assertNotNull(algorithm);
//        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
//        byte[] rawCiphertextBytes = ciphertext.toBytes();
//        byte[] ripemd160CiphertextBytes = BytesUtils.concat(algoBytes,rawCiphertextBytes);
//
//        assertFalse(symmetricEncryptionFunction.supportCiphertext(ripemd160CiphertextBytes));
//    }
//
//    @Test
//    public void resolveCiphertextTest(){
//
//        byte[] data = new byte[1024];
//        Random random = new Random();
//        random.nextBytes(data);
//
//        CryptoAlgorithm algorithm = CryptoServiceProviders.getAlgorithm("aes");
//        assertNotNull(algorithm);
//
//        SymmetricEncryptionFunction symmetricEncryptionFunction =
//                CryptoServiceProviders.getSymmetricEncryptionFunction(algorithm);
//
//        SymmetricKey symmetricKey = (SymmetricKey) symmetricEncryptionFunction.generateSymmetricKey();
//
//        Ciphertext ciphertext = symmetricEncryptionFunction.encrypt(symmetricKey,data);
//
//        byte[] ciphertextBytes = ciphertext.toBytes();
//        assertTrue(symmetricEncryptionFunction.supportCiphertext(ciphertextBytes));
//
//        algorithm = CryptoServiceProviders.getAlgorithm("ripemd160");
//        assertNotNull(algorithm);
//        byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
//        byte[] rawCiphertextBytes =  ciphertext.toBytes();
//        byte[] ripemd160CiphertextBytes = BytesUtils.concat(algoBytes,rawCiphertextBytes);
//
//        Class<?> expectedException = CryptoException.class;
//        Exception actualEx = null;
//        try {
//            symmetricEncryptionFunction.resolveSymmetricKey(ripemd160CiphertextBytes);
//        } catch (Exception e) {
//            actualEx = e;
//        }
//        assertNotNull(actualEx);
//        assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
//    }
}
