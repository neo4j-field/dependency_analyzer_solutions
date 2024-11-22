package com.fournier.dependencyanalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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

        System.out.println("Reading Contributors...");
        String testRepo = repositories.get(0).get("name").toString();
        var test = gitHubService.getContributors(testRepo);
        test.forEach(System.out::println);
        System.out.println("Complete");


    }
}
