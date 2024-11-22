package com.fournier.dependencyanalyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class GitConfig {

    @Value("${github.pat}")
    private String patPath;

    @Value("${github.api.url}")
    private String gitHubApiUrl;

    @Value("${github.org}")
    private String gitHubOrg;

    private String gitHubPAT;


    @PostConstruct
    public void init() {
        try {
            // Read the PAT from the file at application startup
            this.gitHubPAT = new String(Files.readAllBytes(Paths.get(patPath))).trim();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read GitHub PAT from file: " + patPath, e);
        }
    }

    public String getGitHubApiUrl() {
        return gitHubApiUrl;
    }

    public String getGithubPAT() {
        return gitHubPAT;
    }

    public String getGitHubOrg() {
        return gitHubOrg;
    }

}
