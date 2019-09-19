package com.jd.blockchain.contract.maven;

import com.jd.blockchain.contract.maven.rule.BlackList;
import com.jd.blockchain.contract.maven.rule.WhiteList;
import com.jd.blockchain.contract.maven.rule.DependencyExclude;
import com.jd.blockchain.contract.maven.verify.ResolveEngine;
import com.jd.blockchain.contract.maven.verify.VerifyEngine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.assembly.mojos.SingleAssemblyMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.jd.blockchain.contract.ContractJarUtils.BLACK_CONF;
import static com.jd.blockchain.contract.ContractJarUtils.WHITE_CONF;

@Mojo(name = "compile")
public class ContractCompileMojo extends SingleAssemblyMojo {

    public static final String JAR_DEPENDENCE = "jar-with-dependencies";

    public static final String SCOPE_PROVIDED = "provided";

    public static final String SCOPE_COMPILE = "compile";

    private DependencyExclude dependencyExclude = new DependencyExclude();

    private static BlackList black;

    private static WhiteList white;

    static {
        init();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // 首先对MainClass进行校验，要求必须有MainClass
        String mainClass = mainClassVerify();

        // 排除所有依赖，只打包当前代码
//        excludeAllArtifactExclude(super.getProject().getDependencyArtifacts());
//        handleArtifactCompile(super.getProject().getDependencyArtifacts());
        handleArtifactExclude(super.getProject().getDependencyArtifacts());

        // 此参数用于设置将所有第三方依赖的Jar包打散为.class，与主代码打包在一起，生成一个jar包
        super.setDescriptorRefs(new String[]{JAR_DEPENDENCE});

        // 执行打包命令
        // 该命令生成的是只含有当前项目的实际代码的Jar包，该Jar包仅用于校验MainClass
        super.execute();

        // 生成解析引擎
        ResolveEngine resolveEngine = new ResolveEngine(getLog(), mainClass);

        // 获取本次生成的Jar文件
        File defaultJarFile;
        try {
            defaultJarFile = rename(getProject(), getFinalName());
            // 校验当前MainClass是否满足需求
            resolveEngine.verifyCurrentProjectMainClass(defaultJarFile);
            // 校验完成后将该Jar删除
//            FileUtils.forceDelete(mainClassFile);
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoFailureException(e.getMessage());
        }

//        // 将JDChain本身之外的代码打包进Jar包，然后编译
//        handleArtifactExclude(super.getProject().getDependencyArtifacts());
//
//        // 此参数用于设置将所有第三方依赖的Jar包打散为.class，与主代码打包在一起，生成一个jar包
//        super.setDescriptorRefs(new String[]{JAR_DEPENDENCE});
//
//        // 生成Jar包（该Jar包中不包含JDChain内部的代码）
//        super.execute();
//
//        File defaultJarFile;
//        try {
//            defaultJarFile = rename(getProject(), getFinalName());
//        } catch (Exception e) {
//            getLog().error(e);
//            throw new MojoFailureException(e.getMessage());
//        }

        // 校验该Jar包
        verify(defaultJarFile, mainClass);

        File deployJarFile = resolveEngine.verify(defaultJarFile);

        // 删除中间产生的临时文件
        try {
            FileUtils.forceDelete(defaultJarFile);
        } catch (Exception e) {
            getLog().error(e);
        }

        getLog().info(String.format("JDChain's Contract compile success, path = %s !", deployJarFile.getPath()));



//        // 将JDChain本身代码之外的代码移除（不打包进整个Jar）
//        handleArtifactExclude(super.getProject().getDependencyArtifacts());
//
//        // 此参数用于设置将所有第三方依赖的Jar包打散为.class，与主代码打包在一起，生成一个jar包
//        super.setDescriptorRefs(new String[]{JAR_DEPENDENCE});
//
//        // 执行打包命令
//        super.execute();

//        // 将本次打包好的文件重新命名，以便于后续重新打包需要
//        // 把文件改名，然后重新再生成一个文件
//        File dstFile;
//        try {
//            dstFile = rename(getProject(), getFinalName());
//        } catch (IOException e) {
//            getLog().error(e);
//            throw new MojoFailureException(e.getMessage());
//        }
//
//        // dstFile理论上应该含有
//
//        // 首先校验该类的Jar包中是否包含不符合规范的命名，以及该类的代码中的部分解析
//
//        ResolveEngine resolveEngine = new ResolveEngine(getLog(), mainClass);
//
//        // 校验mainClass
//        resolveEngine.verifyCurrentProjectMainClass(dstFile);
//
//
//
//        File finalJarFile = resolveEngine.verify();
//
//        // 将所有的依赖的jar包全部打包进一个包中，以便于进行ASM检查
//        handleArtifactCompile(super.getProject().getDependencyArtifacts());
//
//        // 然后再打包一次，本次打包完成后，其中的代码包含所有的class（JDK自身的除外）
//        super.execute();
//
//        File jarFile = new File(jarPath(getProject(), getFinalName()));
//
//        // 校验mainClass
//        resolveEngine.verifyCurrentProjectMainClass(jarFile);
//
//        // 对代码中的一些规则进行校验，主要是校验其是否包含一些不允许使用的类、包、方法等
//        verify(jarFile, mainClass);
//
//        // 删除中间的一些文件
////        try {
////            FileUtils.forceDelete(dstFile);
////        } catch (IOException e) {
////            throw new MojoFailureException(e.getMessage());
////        }
//
        // 若执行到此处没有异常则表明打包成功，打印打包成功消息
//        getLog().info(String.format("JDChain's Contract compile success, path = %s !", finalJarFile.getPath()));
    }

    private String mainClassVerify() throws MojoFailureException {
        // 要求必须有MainClass
        String mainClass;
        try {
            mainClass = super.getJarArchiveConfiguration().getManifest().getMainClass();
            // 校验MainClass，要求MainClass必须不能为空
            if (mainClass == null || mainClass.length() == 0) {
                throw new MojoFailureException("MainClass is NULL !!!");
            }
            super.getLog().debug("MainClass is " + mainClass);
        } catch (Exception e) {
            throw new MojoFailureException("MainClass is null: " + e.getMessage(), e );
        }
        return mainClass;
    }

    private void handleArtifactExclude(Set<Artifact> artifacts) {
        for (Artifact artifact : artifacts) {
            String groupId = artifact.getGroupId(), artifactId = artifact.getArtifactId();
            if (dependencyExclude.isExclude(groupId, artifactId)) {
                getLog().info(String.format("GroupId[%s] ArtifactId[%s] belongs to DependencyExclude !!!", groupId, artifactId));
                // 属于排除的名单之中
                artifact.setScope(SCOPE_PROVIDED);
            } else {
                getLog().info(String.format("GroupId[%s] ArtifactId[%s] not belongs to DependencyExclude !!!", groupId, artifactId));
                // 属于排除的名单之中
                artifact.setScope(SCOPE_COMPILE);
            }
        }
    }

    private void excludeAllArtifactExclude(Set<Artifact> artifacts) {
        for (Artifact artifact : artifacts) {
            artifact.setScope(SCOPE_PROVIDED);
        }
    }

    private void handleArtifactCompile(Set<Artifact> artifacts) {
        for (Artifact artifact : artifacts) {
            if (artifact.getScope().equals(SCOPE_PROVIDED)) {
                // 将所有的provided设置为compile，以便于后续编译
                artifact.setScope(SCOPE_COMPILE);
            }
        }
    }

    private File rename(MavenProject project, String finalName) throws IOException {
        String srcJarPath = jarPath(project, finalName);
        String dstJarPath = project.getBuild().getDirectory() +
                File.separator + finalName + ".jar";
        File dstFile = new File(dstJarPath);
        FileUtils.copyFile(new File(srcJarPath), dstFile);
        FileUtils.forceDelete(new File(srcJarPath));
        return dstFile;
    }

    private String jarPath(MavenProject project, String finalName) {
        return project.getBuild().getDirectory() +
                File.separator + finalName + "-" + JAR_DEPENDENCE + ".jar";
    }

    private void verify(File jarFile, String mainClass) throws MojoFailureException {
        try {
            VerifyEngine verifyEngine = new VerifyEngine(getLog(), jarFile, mainClass, black, white);

            verifyEngine.verify();

            // 校验完成后将该jar包删除
//            FileUtils.forceDelete(jarFile);

        } catch (Exception e) {
            getLog().error(e);
            throw new MojoFailureException(e.getMessage());
        }
    }



    private static void init() {
        try {
            black = AbstractContract.initBlack(loadBlackConf());
            white = AbstractContract.initWhite(loadWhiteConf());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static List<String> loadWhiteConf() {

        return resolveConfig(WHITE_CONF);
    }

    private static List<String> loadBlackConf() {
        return resolveConfig(BLACK_CONF);
    }

    private static List<String> resolveConfig(String fileName) {
        List<String> configs = new ArrayList<>();

        try {
            List<String> readLines = loadConfig(fileName);
            if (!readLines.isEmpty()) {
                for (String readLine : readLines) {
                    String[] lines = readLine.split(",");
                    configs.addAll(Arrays.asList(lines));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return configs;
    }

    public static List<String> loadConfig(String fileName) throws Exception {

        return IOUtils.readLines(
                ContractCompileMojo.class.getResourceAsStream("/" + fileName));
    }
}
