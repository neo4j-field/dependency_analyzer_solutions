package com.fournier.dependencyanalyzer.service;

import com.fournier.dependencyanalyzer.model.Pom;
import com.fournier.dependencyanalyzer.reader.PomReader;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.common.io.FileBackedOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GCPService {

    private final Storage storage;
    private final PomReader pomReader;

    @Value("${gcp.bucket-name}")
    private String bucketName;

    public GCPService(Storage storage, PomReader pomReader) {
        this.storage = storage;
        this.pomReader = pomReader;
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

    public List<Pom> loadAndParsePoms(Map<String, Map<String, List<String>>> projectMap) {
        List<Pom> parsedPoms = new ArrayList<>();

        projectMap.forEach((parentDir, subDirMap) -> {
            subDirMap.forEach((subDir, filePaths) -> {
                filePaths.forEach(filePath -> {
                    Blob blob = storage.get(this.bucketName, filePath);

                    if (blob == null || blob.getSize() <= 0) {
                        System.err.println("Blob is empty or null: " + filePath);
                        return;
                    }

                    File tempFile = null;
                    try {
                        tempFile = File.createTempFile("pom", ".xml");

                        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                            blob.downloadTo(outputStream);
                        }
                        Pom pom = pomReader.readPom(tempFile.getAbsolutePath(), parentDir);
                        parsedPoms.add(pom);
                    } catch (Exception e) {
                        System.err.println("Failed to process POM file: " + filePath);

                        e.printStackTrace();
                    } finally {
                        if (tempFile != null && tempFile.exists()) {
                            if (!tempFile.delete()) {
                                System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
                            }
                        }
                    }
                });
            });
        });
        return parsedPoms;
    }


    private String extractParentDirectory(String filePath) {
        String[] parts = filePath.split("/");
        return parts.length > 0 ? parts[0] : "unknown";
    }

    private String extractSubDirectory(String filePath) {
        int firstSlash = filePath.indexOf('/');
        int lastSlash = filePath.lastIndexOf('/');


        if (firstSlash == -1 || lastSlash == -1 || firstSlash >= lastSlash) {
            return "root";
        }

        return filePath.substring(firstSlash + 1, lastSlash);
    }

    private boolean isJavaProjectFile(String filePath) {
        return filePath.endsWith("pom.xml") || filePath.endsWith("build.gradle") || filePath.endsWith("build.gradle.kts");
    }

    public void printProjectMap(Map<String, Map<String, List<String>>> nestedMap) {
        nestedMap.forEach((parentDir, subDirMap) -> {
            System.out.println("Parent Directory: " + parentDir);
            subDirMap.forEach((subDir, filePaths) -> {
                System.out.println("  Subdirectory: " + subDir);
                filePaths.forEach(filePath -> System.out.println("    File: " + filePath));
            });
        });
    }
}
