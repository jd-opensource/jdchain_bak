package test.com.jd.blockchain.tools.initializer;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * Created by zhangshuang3 on 2018/9/11.
 */
public class ConsensusSettingTest {

    @Test
    public void systemFileToBytes() throws Exception {

        ClassPathResource systemConfigResource = new ClassPathResource("bftsmart.config");
        InputStream fis = systemConfigResource.getInputStream();
        ByteArrayOutputStream bos = null;
        FileOutputStream fos = null;
        byte[] buffer = null;

        try {
            bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            //file to bytes
            buffer = bos.toByteArray();
            //bytes to file
            File file = new File("bftsmart.config");
            fos = new FileOutputStream(file);
            fos.write(buffer);
        } catch (FileNotFoundException e) {
            throw new Exception(e.getMessage(), e);
        } catch (IOException e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return;
    }
}