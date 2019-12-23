//package com.jd.blockchain.ledger;
//
//import org.apache.maven.model.Build;
//import org.apache.maven.project.MavenProject;
//import org.junit.Test;
//import org.springframework.core.io.ClassPathResource;
//
//import java.io.File;
//
//public class ContractTestBase {
//
//    public static MavenProject mavenProjectInit() {
//        MavenProject mavenProject = new MavenProject();
//        mavenProject.setBuild(buildInit());
//        mavenProject.setFile(file());
//        return mavenProject;
//    }
//
//    public static File file() {
//        String resDir = resourceDir();
//        File file = new File(resDir);
//        String path = file.getParentFile().getParentFile().getPath();
//        return new File(path + File.separator + "src");
//    }
//
//    public static Build buildInit() {
//        Build build = new Build();
//        build.setDirectory(resourceDir());
//        return build;
//    }
//
//    public static String resourceDir() {
//        try {
//            ClassPathResource classPathResource = new ClassPathResource("complex.jar");
//            return classPathResource.getFile().getParentFile().getPath();
//        } catch (Exception e) {
//            throw new IllegalStateException(e);
//        }
//    }
//
//    @Test
//    public void testResourceDir() {
//        System.out.println(resourceDir());
//    }
//
//    @Test
//    public void testFile() {
//        System.out.println(file().getPath());
//    }
//}
