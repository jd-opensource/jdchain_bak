package com.jd.blockchain;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


@Mojo(name = "checkImports")
public class CheckImportsMojo extends AbstractMojo {
    Logger logger = LoggerFactory.getLogger(CheckImportsMojo.class);

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoFailureException {
        List<Path> sources;
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String[] packageBlackList = properties.getProperty("blacklist").split(",");
            Path baseDirPath = project.getBasedir().toPath();
            sources = Files.find(baseDirPath, Integer.MAX_VALUE, (file, attrs) -> (file.toString().endsWith(".java"))).collect(Collectors.toList());
            for (Path path : sources) {
                CompilationUnit compilationUnit = JavaParser.parse(path);
                NodeList<ImportDeclaration> imports = compilationUnit.getImports();
                for (ImportDeclaration imp : imports) {
                    String importName = imp.getName().asString();
                    for (String item : packageBlackList) {
                        if (importName.startsWith(item)) {
                            throw new MojoFailureException("在源码中不允许包含此引入包:" + importName);
                        }
                    }
                }

            }
        } catch (IOException exception) {
            logger.error(exception.getMessage());
            throw new MojoFailureException("IO ERROR");
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
    }
}
