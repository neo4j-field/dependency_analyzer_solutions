package com.fournier.dependencyanalyzer.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BatchUtils {

    public static <T> List<List<T>> batchParameters(List<T> items, int batchSize) {
        if (items == null || batchSize <= 0) {
            return Collections.emptyList();
        }

        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < items.size(); i += batchSize) {
            int end = Math.min(i + batchSize, items.size());
            batches.add(items.subList(i, end));
        }

        return batches;
    }

    public static List<List<Map<String, Object>>> groupCommitsByRepositoryAndAuthor(List<Map<String, Object>> flattenedCommits) {
        Map<String, List<Map<String, Object>>> groupedCommits = flattenedCommits.stream()
                .filter(commit ->
                        commit.get("repository") != null &&
                                commit.get("author") instanceof Map &&
                                ((Map<?, ?>) commit.get("author")).get("login") != null)
                .collect(Collectors.groupingBy(commit ->
                        commit.get("repository") + "_" +
                                ((Map<?, ?>) commit.get("author")).get("login")
                ));

        return new ArrayList<>(groupedCommits.values());
    }
}

