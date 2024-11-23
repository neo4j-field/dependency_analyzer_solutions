package com.fournier.dependencyanalyzer.writer;
import org.neo4j.driver.Driver;
import org.neo4j.driver.SessionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

}