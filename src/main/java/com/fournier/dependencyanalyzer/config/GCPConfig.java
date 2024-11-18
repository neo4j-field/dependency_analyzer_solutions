package com.fournier.dependencyanalyzer.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GCPConfig {

    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.credentials.path}")
    private String credentialsPath;

    @Bean
    public Storage storage() throws IOException {
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(
                        com.google.auth.oauth2.GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                )
                .build()
                .getService();
    }
}
