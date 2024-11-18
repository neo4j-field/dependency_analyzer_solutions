package com.fournier.dependencyanalyzer.writer;

import org.neo4j.driver.Session;

public class Neo4jWriter {


    private static final int BATCH_SIZE = 1000;

    private final Session session;

    public Neo4jWriter(Session session) {
        this.session = session;
    }


}
