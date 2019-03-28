package test.com.jd.blockchain.ledger;

import com.jd.blockchain.binaryproto.BinaryEncodingUtils;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeyPair;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.core.CryptoConfig;
import com.jd.blockchain.ledger.core.ParticipantCertData;
import com.jd.blockchain.ledger.data.ConsensusParticipantData;
import com.jd.blockchain.ledger.data.LedgerInitOpTemplate;
import com.jd.blockchain.ledger.data.LedgerInitSettingData;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.net.NetworkAddress;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class LedgerInitOperationTest {

    byte[] seed = null;
    byte[] csSysSettingBytes = null;
    LedgerInitSettingData ledgerInitSettingData = new LedgerInitSettingData();

    @Before
    public void initCfg() {

        DataContractRegistry.register(LedgerInitSetting.class);
        DataContractRegistry.register(LedgerInitOperation.class);

        Random rand = new Random();

        seed = new byte[8];
        rand.nextBytes(seed);
        csSysSettingBytes = new byte[64];
        rand.nextBytes(csSysSettingBytes);

        CryptoConfig cryptoConfig = new CryptoConfig();
        cryptoConfig.setAutoVerifyHash(true);
        cryptoConfig.setHashAlgorithm(CryptoAlgorithm.SHA256);


        ledgerInitSettingData.setConsensusSettings(new Bytes(csSysSettingBytes));
        ledgerInitSettingData.setConsensusProvider("cons-provider");

        ledgerInitSettingData.setLedgerSeed(seed);

        ledgerInitSettingData.setCryptoSetting(cryptoConfig);
    }

    @Test
    public void test_LedgerInitOperation_ConsensusParticipantData() {
        ConsensusParticipantData[] parties = new ConsensusParticipantData[4];
        BlockchainKeyPair[] keys = new BlockchainKeyPair[parties.length];
        for (int i = 0; i < parties.length; i++) {
            keys[i] = BlockchainKeyGenerator.getInstance().generate();
            parties[i] = new ConsensusParticipantData();
//            parties[i].setId(i);
            parties[i].setAddress(AddressEncoding.generateAddress(keys[i].getPubKey()).toBase58());
            parties[i].setHostAddress(new NetworkAddress("192.168.10." + (10 + i), 10010 + 10 * i));
            parties[i].setName("Participant[" + i + "]");
            parties[i].setPubKey(keys[i].getPubKey());
        }
        ConsensusParticipantData[] parties1 = Arrays.copyOf(parties, 4);

        ledgerInitSettingData.setConsensusParticipants(parties1);

        LedgerInitOpTemplate template = new LedgerInitOpTemplate(ledgerInitSettingData);

        byte[] encode = BinaryEncodingUtils.encode(template, LedgerInitOperation.class);
        LedgerInitOperation decode = BinaryEncodingUtils.decode(encode);

        for (int i = 0 ; i < template.getInitSetting().getConsensusParticipants().length; i++) {
            assertEquals(template.getInitSetting().getConsensusParticipants()[i].getAddress(), decode.getInitSetting().getConsensusParticipants()[i].getAddress());
            assertEquals(template.getInitSetting().getConsensusParticipants()[i].getName(), decode.getInitSetting().getConsensusParticipants()[i].getName());
            assertEquals(template.getInitSetting().getConsensusParticipants()[i].getPubKey(), decode.getInitSetting().getConsensusParticipants()[i].getPubKey());

        }
        assertArrayEquals(template.getInitSetting().getLedgerSeed(), decode.getInitSetting().getLedgerSeed());
        assertArrayEquals(template.getInitSetting().getConsensusSettings().toBytes(), decode.getInitSetting().getConsensusSettings().toBytes());
        assertEquals(template.getInitSetting().getCryptoSetting().getHashAlgorithm(), decode.getInitSetting().getCryptoSetting().getHashAlgorithm());
        assertEquals(template.getInitSetting().getCryptoSetting().getAutoVerifyHash(), decode.getInitSetting().getCryptoSetting().getAutoVerifyHash());
        assertEquals(template.getInitSetting().getConsensusProvider(), decode.getInitSetting().getConsensusProvider());

    }

    @Test
    public void test_LedgerInitOperation_ParticipantCertData() {
        ParticipantCertData[] parties = new ParticipantCertData[4];
        BlockchainKeyPair[] keys = new BlockchainKeyPair[parties.length];

        for (int i = 0; i < parties.length; i++) {
            keys[i] = BlockchainKeyGenerator.getInstance().generate();
            parties[i] = new ParticipantCertData(AddressEncoding.generateAddress(keys[i].getPubKey()).toBase58(), "Participant[" + i + "]", keys[i].getPubKey());
        }

        ParticipantCertData[] parties1 = Arrays.copyOf(parties, 4);

        ledgerInitSettingData.setConsensusParticipants(parties1);

        LedgerInitOpTemplate template = new LedgerInitOpTemplate(ledgerInitSettingData);

        byte[] encode = BinaryEncodingUtils.encode(template, LedgerInitOperation.class);
        LedgerInitOperation decode = BinaryEncodingUtils.decode(encode);

        for (int i = 0 ; i < template.getInitSetting().getConsensusParticipants().length; i++) {
            assertEquals(template.getInitSetting().getConsensusParticipants()[i].getAddress(), decode.getInitSetting().getConsensusParticipants()[i].getAddress());
            assertEquals(template.getInitSetting().getConsensusParticipants()[i].getName(), decode.getInitSetting().getConsensusParticipants()[i].getName());
            assertEquals(template.getInitSetting().getConsensusParticipants()[i].getPubKey(), decode.getInitSetting().getConsensusParticipants()[i].getPubKey());

        }
        assertArrayEquals(template.getInitSetting().getLedgerSeed(), decode.getInitSetting().getLedgerSeed());
        assertArrayEquals(template.getInitSetting().getConsensusSettings().toBytes(), decode.getInitSetting().getConsensusSettings().toBytes());
        assertEquals(template.getInitSetting().getCryptoSetting().getHashAlgorithm(), decode.getInitSetting().getCryptoSetting().getHashAlgorithm());
        assertEquals(template.getInitSetting().getCryptoSetting().getAutoVerifyHash(), decode.getInitSetting().getCryptoSetting().getAutoVerifyHash());
        assertEquals(template.getInitSetting().getConsensusProvider(), decode.getInitSetting().getConsensusProvider());
    }
}
