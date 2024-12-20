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

        Map<String, List<Repository>> allRepos = gitHubService.getAllRepositories();
        commitPipeline(allRepos);


    }


    private void commitPipeline(Map<String, List<Repository>> allRepos){
        Map<String, String> repoToOwnerMap = allRepos.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(repo -> Map.entry(repo.getName(), entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, List<Commit>> repoCommitsMap = repoToOwnerMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> gitHubService.getCommits(entry.getKey(), entry.getValue())
                ));


        Map<String, List<Map<String, Object>>> encodedRepoCommitsMap = repoCommitsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(commit -> {
                                    Map<String, Object> commitMap = Encoder.encodeCommit(commit);
                                    commitMap.remove("message");
                                    return commitMap;
                                })
                                .collect(Collectors.toList())
                ));

        List<Map<String, Object>> flattenedCommits = encodedRepoCommitsMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .peek(commit -> commit.put("repository", entry.getKey())))
                .toList();


        List<List<Map<String, Object>>> commitBatches = BatchUtils.groupCommitsByRepositoryAndAuthor(flattenedCommits);

        neo4jWriterGit.writeCommitSequences(commitBatches);







    }

    private void issuePipeline(Map<String, List<Repository>> allRepos) {
        Map<String, String> repoToOwnerMap = allRepos.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(repo -> Map.entry(repo.getName(), entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, List<Issue>> repoIssuesMap = repoToOwnerMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> gitHubService.getIssues(entry.getKey(), entry.getValue())
                ));

        Map<String, List<Map<String, Object>>> encodedRepoIssuesMap = repoIssuesMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(issue -> {
                                    Map<String, Object> issueMap = Encoder.encodeIssue(issue);
                                    issueMap.remove("milestone");
                                    return issueMap;
                                })
                                .collect(Collectors.toList())
                ));

        List<Map<String, Object>> flattenedIssues = encodedRepoIssuesMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .peek(issue -> issue.put("repository", entry.getKey())))
                .toList();

        List<List<Map<String, Object>>> issueBatches = BatchUtils.batchParameters(flattenedIssues, 1000);
        this.neo4jWriterGit.writeIssueRelationships(issueBatches);
    }

    private void contributorPipeline(Map<String, List<Repository>> allRepos){

        Map<String, String> repoToOwnerMap = allRepos.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(repo -> Map.entry(repo.getName(), entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, List<Contributor>> repoContributorsMap = repoToOwnerMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // Key: repository name
                        entry -> gitHubService.getContributors(entry.getKey(), entry.getValue()) // Get contributors
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

    private String findOwnerForRepository(String repo, Map<String, List<Repository>> allRepos) {
        for (Map.Entry<String, List<Repository>> entry : allRepos.entrySet()) {
            String owner = entry.getKey();
            List<Repository> repos = entry.getValue();
            if (repos.stream().anyMatch(r -> r.getName().equals(repo))) {
                return owner;
            }
        }
        throw new IllegalArgumentException("Owner not found for repository: " + repo);
    }
}
