package test.com.jd.blockchain.ledger.data;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static com.jd.blockchain.contract.ContractJarUtils.*;
import static org.junit.Assert.fail;

public class ContractJarUtilsTest {

    private String jarName = "complex";

    @Test
    public void test() {

        byte[] chainCode = null;
        try {
            ClassPathResource classPathResource = new ClassPathResource(jarName + ".jar");
            String classPath = classPathResource.getFile().getParentFile().getPath();

            // 首先将Jar包转换为指定的格式
            String srcJarPath = classPath +
                    File.separator + jarName + ".jar";

            String dstJarPath = classPath +
                    File.separator + jarName + "-temp-" + System.currentTimeMillis() + ".jar";

            File srcJar = new File(srcJarPath), dstJar = new File(dstJarPath);

            // 首先进行Copy处理
            copy(srcJar, dstJar);

            byte[] txtBytes = contractMF(FileUtils.readFileToByteArray(dstJar)).getBytes(StandardCharsets.UTF_8);

            String finalJarPath = classPath +
                    File.separator + jarName + "-jdchain.jar";

            File finalJar = new File(finalJarPath);

            copy(dstJar, finalJar, contractMFJarEntry(), txtBytes, null);

            // 删除临时文件
            FileUtils.forceDelete(dstJar);

            // 读取finalJar中的内容
            chainCode = FileUtils.readFileToByteArray(finalJar);

            FileUtils.forceDelete(finalJar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            verify(chainCode);
            System.out.println("Verify Success ！！！");
        } catch (Exception e) {
            fail("Verify Fail !!");
        }
    }

    @Test
    public void testSign() {
        byte[] test = "zhangsan".getBytes(StandardCharsets.UTF_8);
        System.out.println(contractMF(test));
    }
}
