package com.fournier.dependencyanalyzer.service;

import com.fournier.dependencyanalyzer.model.*;
import com.fournier.dependencyanalyzer.util.BatchUtils;
import com.fournier.dependencyanalyzer.writer.Encoder;
import com.fournier.dependencyanalyzer.writer.Neo4jWriterGit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "runner.git-analyzer.enabled", havingValue = "true", matchIfMissing = true)
public class GitAnalyzerRunner implements CommandLineRunner {

    private final GitHubService gitHubService;
    private final Neo4jWriterGit neo4jWriterGit;


    @Autowired
    public GitAnalyzerRunner(GitHubService gitHubService, Neo4jWriterGit neo4jWriterGit) {
        this.gitHubService = gitHubService;
        this.neo4jWriterGit = neo4jWriterGit;
    }


    @Override
    public void run(String... args) throws Exception {







    }

    private void issuePipeline(List<String> repositoryNames){
        Map<String, List<Issue>> repoIssuesMap = repositoryNames.stream()
                .collect(Collectors.toMap(
                        repo -> repo,
                        gitHubService::getIssues
                ));

        Map<String, List<Map<String, Object>>> encodedRepoIssuesMap = repoIssuesMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(Encoder::encodeIssue)
                                .collect(Collectors.toList())
                ));

        List<Map<String, Object>> flattenedIssues = encodedRepoIssuesMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .peek(issue -> issue.put("repository", entry.getKey())))
                .toList();

        List<List<Map<String, Object>>> issueBatches = BatchUtils.batchParameters(flattenedIssues, 1000);

        this.neo4jWriterGit.writeIssueRelationships(issueBatches);
    }

    private void contributorPipeline(List<String> repositoryNames){

        Map<String, List<Contributor>> repoContributorsMap = repositoryNames.stream()
                .collect(Collectors.toMap(
                        repo -> repo,
                        gitHubService::getContributors
                ));

        Map<String, List<Map<String, Object>>> encodedRepoContributorsMap = repoContributorsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(Encoder::encodeContributor)
                                .collect(Collectors.toList())
                ));


        List<Map<String, Object>> flattenedContributors = encodedRepoContributorsMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .peek(contributor -> contributor.put("repository", entry.getKey())))
                .toList();


        List<List<Map<String, Object>>> contributorBatches = BatchUtils.batchParameters(flattenedContributors, 1000);

        this.neo4jWriterGit.writeContributorRelationships(contributorBatches);

    }
}
