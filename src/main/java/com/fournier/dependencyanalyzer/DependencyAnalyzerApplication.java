package com.fournier.dependencyanalyzer;

import com.fournier.dependencyanalyzer.service.GCPService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;


@SpringBootApplication
public class DependencyAnalyzerApplication implements CommandLineRunner {

    private final GCPService gcpService;

    public DependencyAnalyzerApplication(GCPService gcpService) {
        this.gcpService = gcpService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DependencyAnalyzerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Listing files in bucket:");

        var result = gcpService.filterJavaProjects();

        result.forEach((parentDir, subMap) -> {
            System.out.println("Parent Directory: " + parentDir);

            // Iterate through the inner map
            subMap.forEach((subDir, files) -> {
                System.out.println("  Subdirectory: " + subDir);
                files.forEach(file -> System.out.println("    File: " + file));
            });
        });

        System.out.println("done");
    }
}
