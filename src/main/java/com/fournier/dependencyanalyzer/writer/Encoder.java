package com.fournier.dependencyanalyzer.writer;

import com.fournier.dependencyanalyzer.model.Dependency;
import com.fournier.dependencyanalyzer.model.Pom;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Encoder {

    public static Map<String, Object> encodePom(Pom pom) {
        Map<String, Object> map = new HashMap<>();
        map.put("filePath", pom.getFilePath());
        map.put("parentDirectory", pom.getParentDirectory());
        map.put("dependencies", pom.getDependencies().stream()
                .map(Encoder::encodeDependency)
                .collect(Collectors.toList()));
        return map;
    }

    public static Map<String, Object> encodeDependency(Dependency dependency) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", dependency.getGroupId());
        map.put("artifactId", dependency.getArtifactId());
        map.put("version", dependency.getVersion());
        return map;
    }



}
