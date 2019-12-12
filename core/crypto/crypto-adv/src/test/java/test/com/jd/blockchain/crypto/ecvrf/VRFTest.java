package test.com.jd.blockchain.crypto.ecvrf;

import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.ecvrf.VRF;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class VRFTest {

    @Test
    public void testVRF() {

        byte[] msg = BytesUtils.toBytes("你好");

        //初始化一个异常
        Exception actualEx = null;

        try{
            VRF.getLib();
            long skGenStartTime = System.currentTimeMillis();
            byte[] sk = VRF.genSecretKey();
            long skGenTime = System.currentTimeMillis() - skGenStartTime;
            System.out.println(String.format("VRF sk generation time = %s ms", skGenTime));

            long pkGenStartTime = System.currentTimeMillis();
            byte[] pk = VRF.sk2pk(sk);
            long pkGenTime = System.currentTimeMillis() - pkGenStartTime;
            System.out.println(String.format("VRF pk generation time = %s ms", pkGenTime));

            long proofGenStartTime = System.currentTimeMillis();
            byte[] proof = VRF.prove(sk, msg);
            long proofGenTime = System.currentTimeMillis() - proofGenStartTime;
            System.out.println(String.format("VRF proof generation time = %s ms", proofGenTime));

            long pkValidationStartTime = System.currentTimeMillis();
            assertTrue(VRF.IsValidPk(pk));
            long pkValidationTime = System.currentTimeMillis() - pkValidationStartTime;
            System.out.println(String.format("VRF pk validation time = %s ms", pkValidationTime));

            assertNotNull(proof);

            long proof2hashStartTime = System.currentTimeMillis();
            byte[] output = VRF.proof2hash(proof);
            long proof2hashTime = System.currentTimeMillis() - proof2hashStartTime;
            System.out.println(String.format("VRF proof2hash time = %s ms", proof2hashTime));

            long verificationStartTime = System.currentTimeMillis();
            boolean isValid = VRF.verify(pk, proof, msg);
            long verificationTime = System.currentTimeMillis() - verificationStartTime;
            System.out.println(String.format("VRF verification time = %s ms", verificationTime));

            assertTrue(isValid);
            assertNotNull(output);

        }catch (Exception e){
            actualEx = e;
        }

        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.startsWith("mac")
                || osName.contains("linux")) {
            assertNull(actualEx);
        }
        else {
            assertNotNull(actualEx);
            Class<?> expectedException = CryptoException.class;
            assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
        }
    }
}
