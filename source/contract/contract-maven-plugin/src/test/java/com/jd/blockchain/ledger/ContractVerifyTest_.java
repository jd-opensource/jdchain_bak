//package com.jd.blockchain.ledger;
//
//import com.jd.blockchain.contract.maven.ContractVerifyMojo;
//import org.apache.maven.project.MavenProject;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.lang.reflect.Field;
//
//import static com.jd.blockchain.ledger.ContractTestBase.mavenProjectInit;
//
//public class ContractVerifyTest_ {
//
//    private MavenProject project;
//
//    private String finalName;
//
//    @Before
//    public void testInit() {
//        project = mavenProjectInit();
//        finalName = "complex";
//    }
//
//    @Test
//    public void test() throws Exception {
//        ContractVerifyMojo contractVerifyMojo = contractVerifyMojoConf();
//        contractVerifyMojo.execute();
//    }
//
//    private ContractVerifyMojo contractVerifyMojoConf() throws Exception {
//        ContractVerifyMojo contractVerifyMojo = new ContractVerifyMojo();
//        // 为不影响其内部结构，通过反射进行私有变量赋值
//        Class<?> clazz = contractVerifyMojo.getClass();
//        Field projectField = clazz.getDeclaredField("project");
//        Field finalNameField = clazz.getDeclaredField("finalName");
//
//        // 更新权限
//        projectField.setAccessible(true);
//        finalNameField.setAccessible(true);
//
//        // 设置具体值
//        projectField.set(contractVerifyMojo, project);
//        finalNameField.set(contractVerifyMojo, finalName);
//
//        return contractVerifyMojo;
//    }
//}
