package com.fournier.dependencyanalyzer.service;

import com.fournier.dependencyanalyzer.config.GCPConfig;
import com.fournier.dependencyanalyzer.model.Pom;
import com.fournier.dependencyanalyzer.reader.PomReader;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.common.io.FileBackedOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class GCPService {

    private final Storage storage;
    private final PomReader pomReader;
    private final GCPConfig gcpConfig;

    public GCPService(Storage storage, PomReader pomReader, GCPConfig gcpConfig) {
        this.storage = storage;
        this.pomReader = pomReader;
        this.gcpConfig = gcpConfig;
    }


    public Map<String, List<String>> extractZipsFromBucket() {
        Map<String, List<String>> extractedFilesMap = new HashMap<>();

        Iterable<Blob> blobs = storage.list(this.gcpConfig.getRepoBucketName()).iterateAll();

        for (Blob blob : blobs) {
            if (blob.getName().endsWith(".zip")) {
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    blob.downloadTo(outputStream);
                    try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
                        ZipEntry entry;
                        while ((entry = zipInputStream.getNextEntry()) != null) {
                            if (!entry.isDirectory() && isJavaProjectFile(entry.getName())) {
                                String parentDirectory = extractParentDirectory(blob.getName());
                                extractedFilesMap.computeIfAbsent(parentDirectory, k -> new ArrayList<>()).add(entry.getName());

                            }
                            zipInputStream.closeEntry();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Failed to process zip file: " + blob.getName());
                }
            }
        }
        return extractedFilesMap;
    }


    public Map<String, List<Map<String, Pom>>> extractAndParseJavaProjects() {
        Map<String, List<Map<String, Pom>>> result = new HashMap<>();

        Iterable<Blob> blobs = storage.list(this.gcpConfig.getRepoBucketName()).iterateAll();

        for (Blob blob : blobs) {
            if (blob.getName().endsWith(".zip")) {
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    // Download the zip file content
                    blob.downloadTo(outputStream);

                    try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
                        ZipEntry entry;

                        // Process each entry in the zip file
                        while ((entry = zipInputStream.getNextEntry()) != null) {
                            if (!entry.isDirectory() && entry.getName().endsWith("pom.xml")) {
                                String filePath = entry.getName();
                                String parentDirectory = extractParentDirectoryFromEntry(filePath);

                                // Read and parse the POM file
                                try (ByteArrayOutputStream pomOutputStream = new ByteArrayOutputStream()) {
                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = zipInputStream.read(buffer)) > 0) {
                                        pomOutputStream.write(buffer, 0, length);
                                    }

                                    try (InputStream pomInputStream = new ByteArrayInputStream(pomOutputStream.toByteArray())) {
                                        Pom pom = pomReader.readPom(pomInputStream, parentDirectory);
                                        if (pom != null) {
                                            // Add the POM data with additional fields
                                            Map<String, Pom> pomData = new HashMap<>();
                                            pomData.put("parentDirectory", pom);

                                            result.computeIfAbsent(filePath, k -> new ArrayList<>()).add(pomData);
                                        }
                                    }
                                }
                            }
                            zipInputStream.closeEntry();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Failed to process zip file: " + blob.getName());
                }
            }
        }

        return result;
    }

    private String extractParentDirectoryFromEntry(String filePath) {
        int lastSlashIndex = filePath.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            return filePath.substring(0, lastSlashIndex).split("/")[0]; // Extract the top-level directory
        }
        return filePath; // Default to the full path if no directory structure exists
    }


    public Map<String, List<Pom>> extractAndParseJavaProjectsNoFilePath() {
        Map<String, List<Pom>> projectPomsMap = new HashMap<>();

        Iterable<Blob> blobs = storage.list(this.gcpConfig.getRepoBucketName()).iterateAll();

        for (Blob blob : blobs) {
            if (blob.getName().endsWith(".zip")) {
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    blob.downloadTo(outputStream);

                    try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
                        ZipEntry entry;
                        Map<String, List<String>> projectFiles = new HashMap<>();

                        while ((entry = zipInputStream.getNextEntry()) != null) {
                            if (!entry.isDirectory()) {
                                String fileName = entry.getName();

                                if (isJavaProjectFile(fileName)) {
                                    String projectName = extractParentDirectory(blob.getName());
                                    projectFiles.computeIfAbsent(projectName, k -> new ArrayList<>()).add(fileName);
                                }
                            }
                            zipInputStream.closeEntry();
                        }

                        for (String projectName : projectFiles.keySet()) {
                            List<Pom> poms = new ArrayList<>();
                            for (String filePath : projectFiles.get(projectName)) {
                                if (filePath.endsWith("pom.xml")) {
                                    try (InputStream fileInputStream = extractFileFromZip(outputStream.toByteArray(), filePath)) {
                                        if (fileInputStream != null) {
                                            Pom pom = pomReader.readPom(fileInputStream, projectName);
                                            if (pom != null) {
                                                poms.add(pom);
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.err.println("Failed to parse POM file: " + filePath);
                                        e.printStackTrace();
                                    }
                                }
                            }

                            if (!poms.isEmpty()) {
                                projectPomsMap.put(projectName, poms);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Failed to process zip file: " + blob.getName());
                }
            }
        }

        return projectPomsMap;
    }

    private InputStream extractFileFromZip(byte[] zipData, String filePath) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals(filePath)) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zipInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                    return new ByteArrayInputStream(outputStream.toByteArray());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to extract file from zip: " + filePath);
            e.printStackTrace();
        }
        return null;
    }


    public Map<String, Map<String, List<String>>> filterJavaProjects(Map<String, List<String>> extractedFileMap) {
        Map<String, Map<String, List<String>>> projectMap = new HashMap<>();

        extractedFileMap.forEach((parentDirectory, filePaths) -> {
            for (String filePath : filePaths) {
                if (isJavaProjectFile(filePath)) {
                    String subDirectory = extractSubDirectory(filePath);

                    Map<String, List<String>> subDirectoryMap = projectMap.computeIfAbsent(parentDirectory, k -> new HashMap<>());

                    subDirectoryMap.computeIfAbsent(subDirectory, k -> new ArrayList<>()).add(filePath);
                }
            }
        });

        return projectMap;
    }




    public List<Pom> loadAndParsePoms(Map<String, Map<String, List<String>>> projectMap) {
        List<Pom> parsedPoms = new ArrayList<>();

        projectMap.forEach((parentDir, subDirMap) -> {
            subDirMap.forEach((subDir, filePaths) -> {
                filePaths.forEach(filePath -> {
                    Blob blob = storage.get(this.gcpConfig.getRepoBucketName(), filePath);

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
