package com.fournier.dependencyanalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "runner.repo.enabled", havingValue = "true", matchIfMissing = true)
public class GitRepoRunner implements CommandLineRunner {

    private final GitRepoService repositoryDownloaderService;

    @Autowired
    public GitRepoRunner(GitRepoService repositoryDownloaderService) {
        this.repositoryDownloaderService = repositoryDownloaderService;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Starting to download and upload repositories...");

        repositoryDownloaderService.downloadAndUploadAllRepos();

        System.out.println("Completed downloading and uploading repositories to GCP bucket");
    }
}
