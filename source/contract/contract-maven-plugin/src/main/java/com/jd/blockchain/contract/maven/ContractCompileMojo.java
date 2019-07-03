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
            super.getLog().debug("MainClass is " + mainClass);
        } catch (Exception e) {
            throw new MojoFailureException("MainClass is null: " + e.getMessage(), e );
        }
        super.setDescriptorRefs(new String[]{JAR_DEPENDENCE});

        super.execute();

        ContractResolveEngine engine = new ContractResolveEngine(getLog(), getProject(), getFinalName());

        engine.compileAndVerify();
    }
}
