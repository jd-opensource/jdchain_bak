package com.jd.blockchain.contract.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.*;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Mojo(name = "contractCheck")
public class ContractCheckMojo extends AbstractMojo {

    Logger LOG = LoggerFactory.getLogger(ContractCheckMojo.class);

    public static final String CONTRACT_VERIFY = "contractVerify";

    private static final String CONTRACT_MAVEN_PLUGIN = "contract-maven-plugin";

    private static final String MAVEN_ASSEMBLY_PLUGIN = "maven-assembly-plugin";

    private static final String JDCHAIN_PACKAGE = "com.jd.blockchain";

    private static final String APACHE_MAVEN_PLUGINS = "org.apache.maven.plugins";

    private static final String GOALS_VERIFY = "package";

    private static final String GOALS_PACKAGE = "package";

    private static final String OUT_POM_XML = "ContractPom.xml";

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * jar's name;
     */
    @Parameter
    private String finalName;

    /**
     * mainClass;
     */
    @Parameter
    private String mainClass;
    /**
     * ledgerVersion;
     */
    @Parameter
    private String ledgerVersion;

    /**
     * mvnHome;
     */
    @Parameter
    private String mvnHome;

    /**
     * first compile the class, then parse it;
     */
    @Override
    public void execute() throws MojoFailureException {
        compileFiles();
    }

    private void compileFiles() throws MojoFailureException {
        try (FileInputStream fis = new FileInputStream(project.getFile())) {

            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(fis);

            //delete this plugin(contractCheck) from destination pom.xml;then add the proper plugins;
            Plugin plugin = model.getBuild().getPluginsAsMap()
                    .get(JDCHAIN_PACKAGE + ":" + CONTRACT_MAVEN_PLUGIN);
            if(plugin == null){
                plugin = model.getBuild().getPluginsAsMap()
                        .get(APACHE_MAVEN_PLUGINS + ":" + CONTRACT_MAVEN_PLUGIN);
            }

            if(plugin == null) {
                return;
            }

            model.getBuild().removePlugin(plugin);

            List<Plugin> plugins = new ArrayList<>();
            plugins.add(createAssembly());
            plugins.add(createContractVerify());

            model.getBuild().setPlugins(plugins);

            handle(model);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new MojoFailureException(e.getMessage());
        }
    }

    private void invokeCompile(File file) {
        InvocationRequest request = new DefaultInvocationRequest();

        Invoker invoker = new DefaultInvoker();
        try {
            request.setPomFile(file);

            request.setGoals(Collections.singletonList(GOALS_VERIFY));
            invoker.setMavenHome(new File(mvnHome));
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            LOG.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    private Plugin createContractVerify() {
        Plugin plugin = new Plugin();
        plugin.setGroupId(JDCHAIN_PACKAGE);
        plugin.setArtifactId(CONTRACT_MAVEN_PLUGIN);
        plugin.setVersion(ledgerVersion);

        Xpp3Dom finalNameNode = new Xpp3Dom("finalName");
        finalNameNode.setValue(finalName);
        Xpp3Dom configuration = new Xpp3Dom("configuration");
        configuration.addChild(finalNameNode);

        plugin.setConfiguration(configuration);
        plugin.setExecutions(pluginExecution("make-assembly", GOALS_VERIFY, CONTRACT_VERIFY));

        return plugin;
    }

    private Plugin createAssembly() {
        Plugin plugin = new Plugin();
        plugin.setArtifactId(MAVEN_ASSEMBLY_PLUGIN);

        Xpp3Dom configuration = new Xpp3Dom("configuration");

        Xpp3Dom mainClassNode = new Xpp3Dom("mainClass");
        mainClassNode.setValue(mainClass);

        Xpp3Dom manifest = new Xpp3Dom("manifest");
        manifest.addChild(mainClassNode);

        Xpp3Dom archive = new Xpp3Dom("archive");
        archive.addChild(manifest);

        Xpp3Dom finalNameNode = new Xpp3Dom("finalName");
        finalNameNode.setValue(finalName);

        Xpp3Dom appendAssemblyId = new Xpp3Dom("appendAssemblyId");
        appendAssemblyId.setValue("false");

        Xpp3Dom descriptorRef = new Xpp3Dom("descriptorRef");
        descriptorRef.setValue("jar-with-dependencies");
        Xpp3Dom descriptorRefs = new Xpp3Dom("descriptorRefs");
        descriptorRefs.addChild(descriptorRef);

        configuration.addChild(finalNameNode);
        configuration.addChild(appendAssemblyId);
        configuration.addChild(archive);
        configuration.addChild(descriptorRefs);

        plugin.setConfiguration(configuration);
        plugin.setExecutions(pluginExecution("make-assembly", GOALS_PACKAGE, "single"));

        return plugin;
    }

    private List<PluginExecution> pluginExecution(String id, String phase, String goal) {
        PluginExecution pluginExecution = new PluginExecution();
        pluginExecution.setId(id);
        pluginExecution.setPhase(phase);
        List<String> goals = new ArrayList<>();
        goals.add(goal);
        pluginExecution.setGoals(goals);
        List<PluginExecution> pluginExecutions = new ArrayList<>();
        pluginExecutions.add(pluginExecution);

        return pluginExecutions;
    }

    private void handle(Model model) throws IOException {

        MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        mavenXpp3Writer.write(outputStream, model);

        byte[] buffer = outputStream.toByteArray();

        File outPom = new File(project.getBasedir().getPath(), OUT_POM_XML);

        FileUtils.writeByteArrayToFile(outPom, buffer);

        invokeCompile(outPom);
    }
}
