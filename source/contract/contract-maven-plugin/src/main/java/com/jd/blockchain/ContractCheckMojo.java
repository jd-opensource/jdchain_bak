package com.jd.blockchain;

import com.jd.blockchain.utils.ConsoleUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.*;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Mojo(name = "contractCheck")
public class ContractCheckMojo extends AbstractMojo {
    Logger logger = LoggerFactory.getLogger(ContractCheckMojo.class);

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
    public void execute() {
        this.compileFiles();

    }

    private void compileFiles(){
        // 获取当前项目pom.xml文件所在路径
//        URL targetClasses = this.getClass().getClassLoader().getResource("");
//        File file = new File(targetClasses.getPath());
//        String pomXmlPath = file.getParentFile().getParent() + File.separator + "pom.xml";

        FileInputStream fis = null;
        try {
//            fis = new FileInputStream(new File(pomXmlPath));
            fis = new FileInputStream(project.getFile());
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(fis);

            //delete this plugin(contractCheck) from destination pom.xml;then add the proper plugins;
            Plugin plugin = model.getBuild().getPluginsAsMap().get("com.jd.blockchain:contract-maven-plugin");
            if(plugin == null){
                plugin = model.getBuild().getPluginsAsMap().get("org.apache.maven.plugins:contract-maven-plugin");
            }

            if(plugin == null) {
                return;
            }

            model.getBuild().removePlugin(plugin);
//            model.getBuild().setPlugins(null);

//            ConsoleUtils.info("----- 不携带Plugin -----");
//            print(model);

            List<Plugin> plugins = new ArrayList<>();
            plugins.add(createAssembly());
            plugins.add(createCheckImports());

            model.getBuild().setPlugins(plugins);

            ConsoleUtils.info("----- add Plugin -----");
            handle(model);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void invokeCompile(File file) {
        InvocationRequest request = new DefaultInvocationRequest();

        Invoker invoker = new DefaultInvoker();
        try {
            request.setPomFile(file);

            request.setGoals( Collections.singletonList( "verify" ) );
//            request.setMavenOpts("-DmainClass="+mainClass);
            invoker.setMavenHome(new File(mvnHome));
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
    }

    private Plugin createCheckImports() {
        Plugin plugin = new Plugin();
        plugin.setGroupId("com.jd.blockchain");
        plugin.setArtifactId("contract-maven-plugin");
        plugin.setVersion(ledgerVersion);

        Xpp3Dom finalNameNode = new Xpp3Dom("finalName");
        finalNameNode.setValue(finalName);
        Xpp3Dom configuration = new Xpp3Dom("configuration");
        configuration.addChild(finalNameNode);
        plugin.setConfiguration(configuration);

        PluginExecution pluginExecution = new PluginExecution();
        pluginExecution.setId("make-assembly");
        pluginExecution.setPhase("verify");
        List <String> goals = new ArrayList<>();
        goals.add("JDChain.Verify");
        pluginExecution.setGoals(goals);
        List<PluginExecution> pluginExecutions = new ArrayList<>();
        pluginExecutions.add(pluginExecution);
        plugin.setExecutions(pluginExecutions);

        return plugin;
    }

    private Plugin createAssembly() {
        Plugin plugin = new Plugin();
        plugin.setArtifactId("maven-assembly-plugin");

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

        PluginExecution pluginExecution = new PluginExecution();
        pluginExecution.setId("make-assembly");
        pluginExecution.setPhase("package");
        List <String> goals = new ArrayList<>();
        goals.add("single");
        pluginExecution.setGoals(goals);
        List<PluginExecution> pluginExecutions = new ArrayList<>();
        pluginExecutions.add(pluginExecution);
        plugin.setExecutions(pluginExecutions);
        return plugin;
    }

    private void handle(Model model) throws IOException {
        MavenXpp3Writer mavenXpp3Writer = new MavenXpp3Writer();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        mavenXpp3Writer.write(outputStream, model);

        byte[] buffer = outputStream.toByteArray();

        //输出文件
//        File fileOutput = new File("fileOut.xml");
//        File fileOutput = File.createTempFile("fileOut",".xml");
        File fileOutput = new File(project.getBasedir().getPath(),"fileOut.xml");
        fileOutput.createNewFile();

        ConsoleUtils.info("fileOutput's path="+fileOutput.getPath());
        //创建文件输出流对象
        FileOutputStream fos = new FileOutputStream(fileOutput);
        //将字节数组fileInput中的内容输出到文件fileOut.xml中;
        ConsoleUtils.info(new String(buffer));
        fos.write(buffer);
        fos.flush();
        fos.close();
        invokeCompile(fileOutput);
    }
}
