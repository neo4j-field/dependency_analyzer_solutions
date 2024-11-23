package com.fournier.dependencyanalyzer.writer;

import java.util.List;
import java.util.Map;

public interface Writer {
    void writePomBatches(List<List<Map<String, Object>>> pomBatches);
    void writeDependencyRelationships(List<List<Map<String, Object>>> dependencyBatches);
}
