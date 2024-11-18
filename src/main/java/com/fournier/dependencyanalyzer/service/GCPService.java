package com.fournier.dependencyanalyzer.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GCPService {

    private final Storage storage;

    @Value("${gcp.bucket-name}")
    private String bucketName;

    public GCPService(Storage storage) {
        this.storage = storage;
    }

    public Map<String, Map<String, List<String>>> filterJavaProjects() {
        Map<String, Map<String, List<String>>> projectMap = new HashMap<>();

        Iterable<Blob> blobs = storage.list(this.bucketName).iterateAll();

        for (Blob blob : blobs) {
            String filePath = blob.getName();

            if (isJavaProjectFile(filePath)) {
                String parentDirectory = extractParentDirectory(filePath);
                String subDirectory = extractSubDirectory(filePath);

                Map<String, List<String>> subDirectoryMap = projectMap.computeIfAbsent(parentDirectory, k -> new HashMap<>());

                subDirectoryMap.computeIfAbsent(subDirectory, k -> new ArrayList<>()).add(filePath);
            }
        }

        return projectMap;
    }

    private String extractParentDirectory(String filePath) {
        String[] parts = filePath.split("/");
        return parts.length > 0 ? parts[0] : "unknown";
    }

    private String extractSubDirectory(String filePath) {
        int firstSlash = filePath.indexOf('/');
        int lastSlash = filePath.lastIndexOf('/');

        // Ensure valid indices for substring
        if (firstSlash == -1 || lastSlash == -1 || firstSlash >= lastSlash) {
            return "root"; // No valid subdirectory path
        }

        return filePath.substring(firstSlash + 1, lastSlash);
    }
    private boolean isJavaProjectFile(String filePath) {
        return filePath.endsWith("pom.xml") || filePath.endsWith("build.gradle") || filePath.endsWith("build.gradle.kts");
    }
}
