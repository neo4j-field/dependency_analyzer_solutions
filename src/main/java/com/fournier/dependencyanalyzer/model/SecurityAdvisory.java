package com.fournier.dependencyanalyzer.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityAdvisory {

    private final long id;

    @JsonProperty("ghsa_id")
    private final String ghsaId;

    @JsonProperty("severity")
    private final String severity;

    @JsonProperty("summary")
    private final String summary;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("published_at")
    private final String publishedAt;

    @JsonProperty("updated_at")
    private final String updatedAt;

    public SecurityAdvisory(
            long id, String ghsaId, String severity, String summary, String description,
            String publishedAt, String updatedAt) {
        this.id = id;
        this.ghsaId = ghsaId;
        this.severity = severity;
        this.summary = summary;
        this.description = description;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public String getGhsaId() {
        return ghsaId;
    }

    public String getSeverity() {
        return severity;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "SecurityAdvisory{" +
                "id=" + id +
                ", ghsaId='" + ghsaId + '\'' +
                ", severity='" + severity + '\'' +
                ", summary='" + summary + '\'' +
                ", description='" + description + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}

