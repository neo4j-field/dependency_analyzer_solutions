package com.fournier.dependencyanalyzer.writer;

import com.fournier.dependencyanalyzer.util.BatchUtils;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.TransactionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class Neo4jWriter {

    private final Driver driver;
    private final String database;
    private final SessionConfig sessionConfig;


    public Neo4jWriter(Driver driver, @Value("${neo4j.database}") String database) {
        this.driver = driver;
        this.database = database;
        this.sessionConfig = SessionConfig.builder().withDatabase(database).build();
    }

    public void writePomBatches(List<List<Map<String, Object>>> pomBatches) {
        try (Session session = driver.session(this.sessionConfig)) {
            for (List<Map<String, Object>> batch : pomBatches) {
                session.executeWrite(tx -> {
                    writePomBatch(tx, batch);
                    return null;
                });
            }
        }
    }


    private void writePomBatch(TransactionContext tx, List<Map<String, Object>> batch) {
        String query = """
                UNWIND $batch AS pom
                MERGE (p:Pom {filePath: pom.filePath, parentDirectory: pom.parentDirectory})
                RETURN p
                """;

        tx.run(query, Map.of("batch", batch));
    }

    public void writeDependencyRelationships(List<List<Map<String, Object>>> dependencyBatches) {
        try (Session session = driver.session(sessionConfig)) {
            for (List<Map<String, Object>> batch : dependencyBatches) {
                session.executeWrite(tx -> {
                    String query = """
                    UNWIND $batch AS dependency
                    MATCH (p:Pom {filePath: dependency.filePath})
                    MERGE (d:Dependency {groupId: dependency.groupId, artifactId: dependency.artifactId, version: dependency.version})
                    MERGE (p)-[:DEPENDS_ON]->(d)
                    """;

                    tx.run(query, Map.of("batch", batch));
                    return null;
                });
            }
        }
    }





}