package com.fournier.dependencyanalyzer;

import com.fournier.dependencyanalyzer.model.Dependency;
import com.fournier.dependencyanalyzer.model.Pom;
import com.fournier.dependencyanalyzer.service.GCPService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import java.util.List;


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

        var result = gcpService.filterJavaProjects();
        System.out.println("Project Map");
        gcpService.printProjectMap(result);

        List<Pom> poms = gcpService.loadAndParsePoms(result);


    }
}
