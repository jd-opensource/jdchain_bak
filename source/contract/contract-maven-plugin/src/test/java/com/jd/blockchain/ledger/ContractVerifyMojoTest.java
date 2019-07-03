//package com.jd.blockchain.ledger;
//
//import com.jd.blockchain.contract.maven.ContractVerifyMojo;
//import org.apache.maven.plugin.testing.AbstractMojoTestCase;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//
///**
// * @Author zhaogw
// * @Date 2019/3/1 21:27
// */
//public class ContractVerifyMojoTest extends AbstractMojoTestCase {
//    Logger logger = LoggerFactory.getLogger(ContractVerifyMojoTest.class);
//
//    @Test
//    public void test1() throws Exception {
//        File pom = getTestFile( "src/test/resources/project-to-test/pom.xml" );
//        assertNotNull( pom );
//        assertTrue( pom.exists() );
//
//        ContractVerifyMojo myMojo = (ContractVerifyMojo) lookupMojo( "contractVerify", pom );
//        assertNotNull( myMojo );
//        myMojo.execute();
//    }
//}
