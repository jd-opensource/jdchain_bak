package test.com.jd.blockchain.crypto.mpc;

import com.jd.blockchain.crypto.mpc.EqualVerify;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EqualVerifyTest {

    private byte[] sponsorEPubKeyBytes;
    private byte[] sponsorEPrivKeyBytes;

    private byte[] responderEPubKeyBytes;
    private byte[] responderEPrivKeyBytes;

    @Before
    public void init() {

        EqualVerify.generateParams();
        EqualVerify.generateSponsorKeyPair();
        EqualVerify.generateResponderKeyPair();

        sponsorEPubKeyBytes    = EqualVerify.getSponsorEPubKeyBytes();
        sponsorEPrivKeyBytes   = EqualVerify.getSponsorEPrivKeyBytes();

        responderEPubKeyBytes  = EqualVerify.getResponderEPubKeyBytes();
        responderEPrivKeyBytes = EqualVerify.getResponderEPrivKeyBytes();
    }


    @Test
    public void testIntCompare() {

        int sponsorInput;
        int responderInput;

        byte[] sponsorOutput;
        byte[] responderOutput;
        boolean isEqual;


        sponsorInput   = 666;
        responderInput = 666;

        sponsorOutput = EqualVerify.sponsor(sponsorInput,sponsorEPubKeyBytes);
        responderOutput = EqualVerify.responder(responderInput,sponsorOutput,
                 responderEPubKeyBytes,responderEPrivKeyBytes);

        isEqual = EqualVerify.sponsorCheck(sponsorInput,responderOutput,sponsorEPrivKeyBytes);

        assertTrue(isEqual);


        sponsorInput   = 666;
        responderInput = 667;

        sponsorOutput = EqualVerify.sponsor(sponsorInput,sponsorEPubKeyBytes);
        responderOutput = EqualVerify.responder(responderInput,sponsorOutput,
                    responderEPubKeyBytes,responderEPrivKeyBytes);

        isEqual = EqualVerify.sponsorCheck(sponsorInput,responderOutput,sponsorEPrivKeyBytes);

        assertTrue(!isEqual);

    }
}