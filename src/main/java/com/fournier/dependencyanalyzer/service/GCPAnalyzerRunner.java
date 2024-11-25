package com.fournier.dependencyanalyzer.service;

import com.fournier.dependencyanalyzer.model.Pom;
import com.fournier.dependencyanalyzer.util.BatchUtils;
import com.fournier.dependencyanalyzer.writer.Encoder;
import com.fournier.dependencyanalyzer.writer.Neo4jWriterGCP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "runner.dependency-analyzer.enabled", havingValue = "true", matchIfMissing = true)
public class GCPAnalyzerRunner implements CommandLineRunner {

    private final GCPService gcpService;
    private final Neo4jWriterGCP neo4JWriterGCP;

    @Autowired
    public GCPAnalyzerRunner(GCPService gcpService, Neo4jWriterGCP neo4JWriterGCP) {
        this.gcpService = gcpService;
        this.neo4JWriterGCP = neo4JWriterGCP;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Running Dependency Analyzer Task");

        Map<String, Map<String, List<String>>> javaProjectMap = gcpService.filterJavaProjects();

        List<Pom> poms = gcpService.loadAndParsePoms(javaProjectMap);

        List<Map<String, Object>> encodedPomParameters = poms.stream()
                .map(Encoder::encodePom)
                .toList();

        Map<String, List<Map<String, Object>>> encodedDependencies = poms.stream()
                .collect(Collectors.toMap(
                        Pom::getFilePath,
                        pom -> pom.getDependencies().stream()
                                .map(Encoder::encodeDependency)
                                .toList()
                ));

        List<Map<String, Object>> flattenedDependencies = encodedDependencies.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .peek(dependency -> dependency.put("filePath", entry.getKey())))
                .toList();

        List<List<Map<String, Object>>> dependencyBatches = BatchUtils.batchParameters(flattenedDependencies, 1000);
        List<List<Map<String, Object>>> pomBatches = BatchUtils.batchParameters(encodedPomParameters, 1000);

        System.out.println("Beginning writing POM Batches");
        neo4JWriterGCP.writePomBatches(pomBatches);
        System.out.println("Completed Pom Batches");

        System.out.println("Beginning writing dependency Batches");
        neo4JWriterGCP.writeDependencyRelationships(dependencyBatches);
        System.out.println("Completed dependency Batches");
    }
}

