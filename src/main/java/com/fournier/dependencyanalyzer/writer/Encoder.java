package com.fournier.dependencyanalyzer.writer;

import com.fournier.dependencyanalyzer.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Encoder {

    public static Map<String, Object> encodePom(Pom pom) {
        Map<String, Object> map = new HashMap<>();
        map.put("filePath", pom.getFilePath());
        map.put("parentDirectory", pom.getParentDirectory());
        map.put("dependencies", pom.getDependencies().stream()
                .map(Encoder::encodeDependency)
                .collect(Collectors.toList()));
        return map;
    }


    public static Map<String, Object> encodeRepository(Repository repository) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", repository.getId());
        map.put("name", repository.getName());
        map.put("fullName", repository.getFullName());
        map.put("htmlUrl", repository.getHtmlUrl());
        map.put("description", repository.getDescription());
        map.put("language", repository.getLanguage());
        map.put("stargazersCount", repository.getStargazersCount());
        map.put("createdAt", repository.getCreatedAt());
        map.put("updatedAt", repository.getUpdatedAt());
        return map;
    }

    public static Map<String, Object> encodeDependency(Dependency dependency) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", dependency.getGroupId());
        map.put("artifactId", dependency.getArtifactId());
        map.put("version", dependency.getVersion());
        return map;
    }

    public static Map<String, Object> encodeContributor(Contributor contributor) {
        Map<String, Object> map = new HashMap<>();
        map.put("login", contributor.getLogin());
        map.put("id", contributor.getId());
        map.put("nodeId", contributor.getNodeId());
        map.put("avatarUrl", contributor.getAvatarUrl());
        map.put("htmlUrl", contributor.getHtmlUrl());
        map.put("siteAdmin", contributor.getSiteAdmin());
        map.put("contributions", contributor.getContributions());
        return map;
    }

    public static Map<String, Object> encodeCommit(Commit commit) {
        Map<String, Object> map = new HashMap<>();

        map.put("sha", commit.getSha());

        if (commit.getCommit() != null) {
            map.put("message", commit.getCommit().getMessage());

            if (commit.getCommit().getAuthor() != null) {
                map.put("commitAuthor", encodeContributor(commit.getCommit().getAuthor()));
            }
        }

        if (commit.getAuthor() != null) {
            map.put("author", encodeContributor(commit.getAuthor()));
        }

        return map;
    }


    public static Map<String, Object> encodeIssue(Issue issue) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", issue.getId());
        map.put("title", issue.getTitle());
        map.put("body", issue.getBody());
        map.put("state", issue.getState());
        map.put("url", issue.getUrl());
        map.put("htmlUrl", issue.getHtmlUrl());
        map.put("createdAt", issue.getCreatedAt());
        map.put("updatedAt", issue.getUpdatedAt());
        map.put("closedAt", issue.getClosedAt());
        map.put("comments", issue.getComments());
        map.put("milestone", issue.getMilestone());

        if (issue.getUser() != null) {
            map.put("user", encodeUser(issue.getUser()));
        }

        if (issue.getLabels() != null) {
            map.put("labels", issue.getLabels().stream()
                    .map(Encoder::encodeLabel)
                    .collect(Collectors.toList()));
        }

        return map;
    }

    public static Map<String, Object> encodeUser(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("login", user.getLogin());
        map.put("avatarUrl", user.getAvatarUrl());
        map.put("htmlUrl", user.getHtmlUrl());
        return map;
    }

    public static Map<String, Object> encodeLabel(Label label) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", label.getName());
        map.put("color", label.getColor());
        return map;
    }





}
