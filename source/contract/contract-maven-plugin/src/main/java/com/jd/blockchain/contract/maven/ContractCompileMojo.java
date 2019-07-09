package com.jd.blockchain.contract.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.assembly.mojos.SingleAssemblyMojo;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "compile")
public class ContractCompileMojo extends SingleAssemblyMojo {

    public static final String JAR_DEPENDENCE = "jar-with-dependencies";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // 要求必须有MainClass
        try {
            String mainClass = super.getJarArchiveConfiguration().getManifest().getMainClass();
            // 校验MainClass，要求MainClass必须不能为空
            if (mainClass == null || mainClass.length() == 0) {
                throw new MojoFailureException("MainClass is NULL !!!");
            }
            super.getLog().debug("MainClass is " + mainClass);
        } catch (Exception e) {
            throw new MojoFailureException("MainClass is null: " + e.getMessage(), e );
        }

        // 此参数用于设置将所有第三方依赖的Jar包打散为.class，与主代码打包在一起，生成一个jar包
        super.setDescriptorRefs(new String[]{JAR_DEPENDENCE});

        // 执行打包命令
        super.execute();

        ContractResolveEngine engine = new ContractResolveEngine(getLog(), getProject(), getFinalName());

        // 打包并进行校验
        engine.compileAndVerify();
    }
}
