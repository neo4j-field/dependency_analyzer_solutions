package com.fournier.dependencyanalyzer.writer;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class Neo4jWriterGit {

    private final Driver driver;
    private final String database;
    private final SessionConfig sessionConfig;

    public Neo4jWriterGit(Driver driver, @Value("${neo4j.database}") String database) {
        this.driver = driver;
        this.database = database;
        this.sessionConfig = SessionConfig.builder().withDatabase(database).build();
    }


    public void writeContributorRelationships(List<List<Map<String, Object>>> contributorBatches) {
        try (Session session = driver.session(sessionConfig)) {
            for (List<Map<String, Object>> batch : contributorBatches) {
                session.executeWrite(tx -> {
                    String query = """
                UNWIND $batch AS contributor
                MERGE (p:Project {name: contributor.repository})
                MERGE (c:Contributor {login: contributor.login, id: contributor.id})
                SET c.avatarUrl = contributor.avatarUrl,
                    c.htmlUrl = contributor.htmlUrl,
                    c.contributions = contributor.contributions
                MERGE (p)-[:HAS_NEW_CONTRIBUTOR_TEST]->(c)
                """;

                    tx.run(query, Map.of("batch", batch));
                    return null;
                });
            }
        }
    }

    public void writeCommitSequences(List<List<Map<String, Object>>> commitBatches) {
        try (Session session = driver.session(sessionConfig)) {
            for (List<Map<String, Object>> batch : commitBatches) {
                session.executeWrite(tx -> {
                    String query = """
                    UNWIND $batch AS commitData
                    WITH collect(commitData) AS commits

                    // Create NEXT_COMMIT relationships
                    UNWIND range(0, size(commits) - 2) AS index
                    WITH commits, commits[index] AS currentCommit, commits[index + 1] AS nextCommit
                    MERGE (c1:Commit {sha: currentCommit.sha})
                    SET c1.repository = currentCommit.repository
                    MERGE (c2:Commit {sha: nextCommit.sha})
                    SET c2.repository = nextCommit.repository
                    MERGE (c1)-[:NEXT_COMMIT]->(c2)

                    WITH commits[0] AS headCommit
                    MATCH (c:Commit {sha: headCommit.sha}) 
                    MATCH (contributor:Contributor {login: headCommit.author.login})
                    MERGE (contributor)-[:HAS_COMMIT]->(c)
                    
                    WITH headCommit, c
                    
                    MATCH (p:Project {name: headCommit.repository})
                    MERGE (p)-[:HAS_HEAD_COMMIT]->(c)
                """;

                    tx.run(query, Map.of("batch", batch));
                    return null;
                });
            }
        }
    }





    public void writeIssueRelationships(List<List<Map<String, Object>>> issueBatches) {
        try (Session session = driver.session(sessionConfig)) {
            for (List<Map<String, Object>> batch : issueBatches) {
                session.executeWrite(tx -> {
                    String query = """
                UNWIND $batch AS issue
                MERGE (p:Project {name: issue.repository})
                MERGE (i:Issue {id: issue.id})
                SET i.title = issue.title,
                    i.body = issue.body,
                    i.state = issue.state,
                    i.url = issue.url,
                    i.htmlUrl = issue.htmlUrl,
                    i.createdAt = issue.createdAt,
                    i.updatedAt = issue.updatedAt,
                    i.closedAt = issue.closedAt,
                    i.comments = issue.comments,
                    i.milestone = issue.milestone
                MERGE (p)-[:HAS_ISSUE]->(i)
                MERGE (u:User {id: issue.user.id, login: issue.user.login})
                SET u.avatarUrl = issue.user.avatarUrl,
                    u.htmlUrl = issue.user.htmlUrl
                MERGE (i)-[:CREATED_BY]->(u)
                """;

                    tx.run(query, Map.of("batch", batch));
                    return null;
                });
            }
        }
    }





}