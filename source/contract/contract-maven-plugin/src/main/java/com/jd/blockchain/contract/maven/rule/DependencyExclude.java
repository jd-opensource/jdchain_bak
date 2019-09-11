package com.jd.blockchain.contract.maven.rule;

import com.jd.blockchain.contract.maven.ContractCompileMojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyExclude {

    private static final String COMMON_ARTIFACTID = "*";

    private static final String CONFIG = "providers.conf";

    private static final Map<String, List<Dependency>> DEPENDENCYS = new ConcurrentHashMap<>();

    static {
        try {
            init();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void init() throws Exception {
        List<String> readLines = ContractCompileMojo.loadConfig(CONFIG);
        if (!readLines.isEmpty()) {
            for (String line : readLines) {
                // groupId/artifactId
                String[] lines = line.split(",");
                if (lines.length > 0) {
                    for (String depend : lines) {
                        String[] depends = depend.split("/");
                        if (depends.length == 2) {
                            String groupId = depends[0], artifactId = depends[1];
                            Dependency dependency = new Dependency(groupId, artifactId);
                            addDependency(dependency);
                        }
                    }
                }
            }
        }
    }

    private synchronized static void addDependency(Dependency dependency) {
        String groupId = dependency.groupId;
        if (!DEPENDENCYS.containsKey(groupId)) {
            List<Dependency> dependencies = new ArrayList<>();
            dependencies.add(dependency);
            DEPENDENCYS.put(groupId, dependencies);
        } else {
            List<Dependency> dependencies = DEPENDENCYS.get(groupId);
            dependencies.add(dependency);
        }
    }

    public boolean isExclude(String groupId, String artifactId) {
        List<Dependency> dependencies = DEPENDENCYS.get(groupId);

        if (dependencies == null || dependencies.isEmpty()) {
            return false;
        }

        for (Dependency dependency : dependencies) {
            if (dependency.isEqualArtifactId(artifactId)) {
                return true;
            }
        }

        return false;
    }


    static class Dependency {

        String groupId;

        String artifactId;

        public Dependency(String groupId, String artifactId) {
            this.groupId = groupId;
            this.artifactId = artifactId;
        }

        public boolean isEqualArtifactId(String artiId) {
            if (artifactId.equals(COMMON_ARTIFACTID)) {
                return true;
            }
            return artifactId.equals(artiId);
        }
    }
}
