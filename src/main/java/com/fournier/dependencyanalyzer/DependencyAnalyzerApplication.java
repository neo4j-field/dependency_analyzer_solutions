package com.fournier.dependencyanalyzer;

import com.fournier.dependencyanalyzer.model.Dependency;
import com.fournier.dependencyanalyzer.model.Pom;
import com.fournier.dependencyanalyzer.service.GCPService;
import com.fournier.dependencyanalyzer.util.BatchUtils;
import com.fournier.dependencyanalyzer.writer.Encoder;
import com.fournier.dependencyanalyzer.writer.Neo4jWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@SpringBootApplication
public class DependencyAnalyzerApplication {



    public static void main(String[] args) {
        SpringApplication.run(DependencyAnalyzerApplication.class, args);
        }


}
