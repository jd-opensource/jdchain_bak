package test.my.utils;

import com.jd.blockchain.utils.decompiler.utils.DecompilerUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DecompilerUtilsTest {

    private String testJarName = "contract-JDChain-Contract.jar";

    private String testJarMainClass = "com.jd.jr.contract.service.impl.Test031111621550409227472896ContractImpl";

    @Test
    public void testMainClassFromJarFile() throws Exception {
        // 获取需要测试的jar包
        String path = DecompilerUtilsTest.class.getResource("/").toURI().getPath() + testJarName;

        String mainClass = DecompilerUtils.readMainClassFromJarFile(path);

        System.out.println("find main class = " + mainClass);

        assertEquals(mainClass, testJarMainClass);
    }
}
