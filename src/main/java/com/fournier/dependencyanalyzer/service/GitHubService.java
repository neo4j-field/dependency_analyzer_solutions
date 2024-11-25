package com.fournier.dependencyanalyzer.service;


import com.fournier.dependencyanalyzer.config.GitConfig;
import com.fournier.dependencyanalyzer.model.Contributor;
import com.fournier.dependencyanalyzer.model.Issue;
import com.fournier.dependencyanalyzer.model.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;
    private final GitConfig gitConfig;
    private final String owner;

    @Autowired
    public GitHubService(RestTemplateBuilder restTemplateBuilder, GitConfig gitConfig) {
        this.gitConfig = gitConfig;
        this.restTemplate = restTemplateBuilder
                .defaultHeader("Authorization", "Bearer " + gitConfig.getGithubPAT())
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
        this.owner = gitConfig.getGitHubOrg();
    }

    public List<Repository> getRepositories() {
        String url = String.format("%s/orgs/%s/repos", gitConfig.getGitHubApiUrl(), this.owner);

        ResponseEntity<List<Repository>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Repository>>() {}
        );

        if (response.getBody() == null || response.getBody().isEmpty()) {
            return List.of();
        }

        return response.getBody();
    }


    public List<Contributor> getContributors(String repo) {
        String url = String.format("%s/repos/%s/%s/contributors", gitConfig.getGitHubApiUrl(), this.owner, repo);

        ResponseEntity<List<Contributor>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Contributor>>() {}
        );


        if (response.getBody() == null || response.getBody().isEmpty()) {
            System.out.println("No contributors found for repository: " + repo);
            return List.of();
        }


        return response.getBody();
    }

    public List<Issue> getIssues(String repo) {
        String url = String.format("%s/repos/%s/%s/issues", gitConfig.getGitHubApiUrl(), this.owner, repo);

        ResponseEntity<List<Issue>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Issue>>() {}
        );

        if (response.getBody() == null || response.getBody().isEmpty()) {
            System.out.println("No Issues found for repository: " + repo);
            return List.of();
        }


        return response.getBody() == null ? List.of() : response.getBody();
    }






}
