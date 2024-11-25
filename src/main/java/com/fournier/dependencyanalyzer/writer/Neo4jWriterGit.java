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
                MERGE (p)-[:HAS_CONTRIBUTOR]->(c)
                """;

                    tx.run(query, Map.of("batch", batch));
                    return null;
                });
            }
        }
    }


}