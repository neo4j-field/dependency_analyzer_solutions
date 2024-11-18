package com.fournier.dependencyanalyzer.model;

import java.util.List;


public class Pom {
    private String filePath;
    private String parentDirectory;
    private List<Dependency> dependencies;

    public Pom(String filePath, String parentDirectory, List<Dependency> dependencies) {
        this.filePath = filePath;
        this.parentDirectory = parentDirectory;
        this.dependencies = dependencies;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(String parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "Pom{" +
                "filePath='" + filePath + '\'' +
                ", parentDirectory='" + parentDirectory + '\'' +
                ", dependencies=" + dependencies +
                '}';
    }
}
