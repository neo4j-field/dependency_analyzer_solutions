package com.fournier.dependencyanalyzer.service;

import com.fournier.dependencyanalyzer.model.Pom;
import com.fournier.dependencyanalyzer.util.BatchUtils;
import com.fournier.dependencyanalyzer.writer.Encoder;
import com.fournier.dependencyanalyzer.writer.Neo4jWriterGCP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        zipPipeline();


    }


    public void zipPipeline(){

        Map<String, List<Pom>> data = gcpService.extractAndParseJavaProjects();

        List<Map<String, Object>> encodedPomParameters = data.values().stream()
                .flatMap(List::stream)
                .map(Encoder::encodePom)
                .toList();



        Map<String, List<Map<String, Object>>> encodedDependencies = data.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .flatMap(pom -> pom.getDependencies().stream())
                                .map(Encoder::encodeDependency)
                                .toList()
                ));


        List<Map<String, Object>> flattenedDependencies = encodedDependencies.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .peek(dependency -> dependency.put("filePath", entry.getKey()))) // Add filePath to each dependency
                .toList();


        System.out.println("Flattened Dependencies (First 10 Records):");
        flattenedDependencies.stream()
                .limit(10) // Limit to the first 10 records
                .forEach(dependency -> {
                    System.out.println("Dependency Record:");
                    System.out.println("  filePath: " + dependency.get("filePath"));
                    System.out.println("  groupId: " + dependency.get("groupId"));
                    System.out.println("  artifactId: " + dependency.get("artifactId"));
                    System.out.println("  version: " + dependency.get("version"));
                    System.out.println("  Other Fields: " + dependency); // Print entire map for additional details
                    System.out.println("----------");
                });

        System.out.println("Encoded POM Parameters (First 10 Records):");
        encodedPomParameters.stream()
                .limit(10) // Limit to the first 10 records
                .forEach(pom -> {
                    System.out.println("POM Record:");
                    System.out.println("  filePath: " + pom.get("filePath"));
                    System.out.println("  parentDirectory: " + pom.get("parentDirectory"));
                    System.out.println("  Other Fields: " + pom); // Print entire map for additional details
                    System.out.println("----------");
                });


        Instant totalStart = Instant.now();

        List<List<Map<String, Object>>> dependencyBatches = BatchUtils.batchParameters(flattenedDependencies, 1000);

        List<List<Map<String, Object>>> pomBatches = BatchUtils.batchParameters(encodedPomParameters, 1000);

        if (!dependencyBatches.isEmpty()) {
            System.out.println("Top 5 records from the first batch of dependencies:");
            List<Map<String, Object>> firstBatch = dependencyBatches.get(0);
            firstBatch.stream().limit(5).forEach(record -> {
                System.out.println("Dependency Record: ");
                record.forEach((key, value) -> System.out.println("  " + key + ": " + value));
            });
        } else {
            System.out.println("No dependency batches found.");
        }

        System.out.println("Beginning writing POM Batches...");
        Instant pomWriteStart = Instant.now();
        neo4JWriterGCP.writePomBatches(pomBatches);
        Instant pomWriteEnd = Instant.now();
        System.out.println("Completed POM Batches in " + Duration.between(pomWriteStart, pomWriteEnd).toMillis() + " ms.");

        System.out.println("Beginning writing Dependency Batches...");
        Instant dependencyWriteStart = Instant.now();
        neo4JWriterGCP.writeDependencyRelationships(dependencyBatches);
        Instant dependencyWriteEnd = Instant.now();
        System.out.println("Completed Dependency Batches in " + Duration.between(dependencyWriteStart, dependencyWriteEnd).toMillis() + " ms.");

        // Total elapsed time
        Instant totalEnd = Instant.now();
        System.out.println("Total time taken for process: " + Duration.between(totalStart, totalEnd).toMillis() + " ms.");




    }


    public void filePipeline(){

        System.out.println("Running Dependency Analyzer Task");

        Map<String, List<String>> extractProjectMaps = gcpService.extractZipsFromBucket();
        Map<String, Map<String, List<String>>> filteredJavaProjects = gcpService.filterJavaProjects(extractProjectMaps);

        List<Pom> poms = gcpService.loadAndParsePoms(filteredJavaProjects);

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

