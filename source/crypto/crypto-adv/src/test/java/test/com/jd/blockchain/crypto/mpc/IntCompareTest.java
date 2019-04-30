package test.com.jd.blockchain.crypto.mpc;

import com.jd.blockchain.crypto.mpc.IntCompare;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntCompareTest {

    private byte[] pubKeyBytes;
    private byte[] privKeyBytes;

    @Before
    public void init() {
        IntCompare.generateKeyPair();
        pubKeyBytes  = IntCompare.getPubKeyBytes();
        privKeyBytes = IntCompare.getPrivKeyBytes();
    }

    @Test
    public void testIntCompare() {

        byte[][] cipherArray;
        byte[][] aggregatedCipherArray;
        int output;

        int sponsorInput = 10000;
        int responderInput = 9999;

        cipherArray = IntCompare.sponsor(sponsorInput, pubKeyBytes);
        aggregatedCipherArray = IntCompare.responder(responderInput, cipherArray, pubKeyBytes);
        output = IntCompare.sponsorOutput(aggregatedCipherArray, privKeyBytes);
        assertEquals(1, output);

        sponsorInput = 10000;
        responderInput = 19999;

        cipherArray = IntCompare.sponsor(sponsorInput, pubKeyBytes);
        aggregatedCipherArray = IntCompare.responder(responderInput, cipherArray, pubKeyBytes);
        output = IntCompare.sponsorOutput(aggregatedCipherArray, privKeyBytes);
        assertEquals(0, output);
    }
}