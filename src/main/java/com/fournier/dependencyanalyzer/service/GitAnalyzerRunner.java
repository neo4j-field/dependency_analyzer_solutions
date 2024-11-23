package com.fournier.dependencyanalyzer.service;

import com.fournier.dependencyanalyzer.model.Contributor;
import com.fournier.dependencyanalyzer.model.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GitAnalyzerRunner implements CommandLineRunner {

    private final GitHubService gitHubService;


    @Autowired
    public GitAnalyzerRunner(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("Reading Repositories...");
        List<Map<String, Object>> repositories = gitHubService.getRepositories();
        System.out.println("Completed Repositories ...");
        List<String> repositoryNames = gitHubService.extractRepositoryNames(repositories);

        Map<String, List<Contributor>> repositoryContributorMap = repositoryNames.stream()
                .collect(Collectors.toMap(
                        repositoryName -> repositoryName,
                        gitHubService::getContributors
                ));




    }
}
