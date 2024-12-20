package com.fournier.dependencyanalyzer.service;

import com.fournier.dependencyanalyzer.config.GCPConfig;
import com.fournier.dependencyanalyzer.config.GitConfig;
import com.fournier.dependencyanalyzer.model.Repository;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class GitRepoService {

    private final RestTemplate restTemplate;
    private final GitConfig gitConfig;
    private final Storage storage;
    private final GCPConfig gcpConfig;
    private final GitHubService gitHubService;

    public GitRepoService(RestTemplate restTemplate, GitConfig gitConfig, Storage storage, GCPConfig gcpConfig, GitHubService gitHubService) {
        this.restTemplate = restTemplate;
        this.gitConfig = gitConfig;
        this.storage = storage;
        this.gcpConfig = gcpConfig;
        this.gitHubService = gitHubService;
    }


    public void downloadAndUploadAllRepos() {
        Map<String, List<Repository>> allRepositories = gitHubService.getAllRepositories();

        System.out.println("TESTTTTTTTTTTTTT");

        allRepositories.forEach((org, repositories) -> {
            System.out.println("Organization: " + org);
            repositories.forEach(repo -> System.out.println("  Full Name: " + repo.getFullName()));
        });



        allRepositories.forEach((org, repositories) -> {
            for (Repository repo : repositories) {
                try {
                    byte[] content = getRepoContent(org, repo.getName());

                    uploadToGCPBucket(org, repo.getName(), content);
                } catch (Exception e) {
                    System.err.println("Failed to process repository: " + repo.getName() + " from organization: " + org);

                }
            }
        });
    }



    private byte[] getRepoContent(String org, String repo) {
        String url = String.format("%s/repos/%s/%s/zipball", gitConfig.getGitHubApiUrl(), org, repo);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(gitConfig.getGithubPAT());
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, byte[].class);

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to download repository: " + repo);
        }

        return response.getBody();
    }

    private void uploadToGCPBucket(String org, String repo, byte[] content) {
        String blobName = String.format("%s/%s.zip", org, repo);

        BlobId blobId = BlobId.of(gcpConfig.getRepoBucketName(), blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            storage.create(blobInfo, inputStream);
            System.out.println("Uploaded repository: " + repo + " to GCP bucket");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload repository: " + repo + " to GCP bucket", e);
        }
    }
}
