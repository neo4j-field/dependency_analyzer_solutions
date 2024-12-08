package com.fournier.dependencyanalyzer.service;


import com.fournier.dependencyanalyzer.config.GitConfig;
import com.fournier.dependencyanalyzer.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;
    private final GitConfig gitConfig;
    private final String owner;
    private final List<String> allOwners;

    @Autowired
    public GitHubService(RestTemplateBuilder restTemplateBuilder, GitConfig gitConfig) {
        this.gitConfig = gitConfig;
        this.restTemplate = restTemplateBuilder
                .defaultHeader("Authorization", "Bearer " + gitConfig.getGithubPAT())
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
        this.owner = gitConfig.getGitHubOrg();
        this.allOwners = gitConfig.getGitHubOrgList();
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

    public Map<String, List<Repository>> getAllRepositories() {
        Map<String, List<Repository>> allRepos = new HashMap<>();

        this.allOwners.forEach(owner -> {
            String url = String.format("%s/orgs/%s/repos", gitConfig.getGitHubApiUrl(), owner);

            try {
                ResponseEntity<List<Repository>> response = restTemplate.exchange(
                        url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Repository>>() {}
                );

                List<Repository> repositories = (response.getBody() == null) ? List.of() : response.getBody();
                allRepos.put(owner, repositories);

            } catch (HttpClientErrorException.NotFound e) {
                System.err.println("Organization not found: " + owner);
            } catch (Exception e) {
                System.err.println("Failed to fetch repositories for organization: " + owner);
            }
        });

        return allRepos;
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

    public List<DependabotAlert> getDependabotAlerts(String repo) {
        String url = String.format("%s/repos/%s/%s/dependabot/alerts", gitConfig.getGitHubApiUrl(), this.owner, repo);

        ResponseEntity<List<DependabotAlert>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<DependabotAlert>>() {}
        );

        if (response.getBody() == null || response.getBody().isEmpty()) {
            System.out.println("No Dependabot Alerts found for repository: " + repo);
            return List.of();
        }

        return response.getBody();
    }


    public List<CodeScanningAlert> getCodeScanningAlerts(String repo) {
        String url = String.format("%s/repos/%s/%s/code-scanning/alerts", gitConfig.getGitHubApiUrl(), this.owner, repo);

        ResponseEntity<List<CodeScanningAlert>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CodeScanningAlert>>() {}
        );

        if (response.getBody() == null || response.getBody().isEmpty()) {
            System.out.println("No Code Scanning Alerts found for repository: " + repo);
            return List.of();
        }

        return response.getBody();
    }

    public List<SecurityAdvisory> getSecurityAdvisories(String repo) {
        String url = String.format("%s/repos/%s/%s/security-advisories", gitConfig.getGitHubApiUrl(), this.owner, repo);

        ResponseEntity<List<SecurityAdvisory>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<SecurityAdvisory>>() {}
        );

        if (response.getBody() == null || response.getBody().isEmpty()) {
            System.out.println("No Security Advisories found for repository: " + repo);
            return List.of();
        }

        return response.getBody();
    }



}
