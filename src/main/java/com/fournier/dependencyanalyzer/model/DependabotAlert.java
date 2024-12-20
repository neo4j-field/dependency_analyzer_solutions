package com.fournier.dependencyanalyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DependabotAlert {

    private final long id;

    @JsonProperty("number")
    private final int alertNumber;

    @JsonProperty("state")
    private final String state;

    @JsonProperty("severity")
    private final String severity;

    @JsonProperty("package_name")
    private final String packageName;

    @JsonProperty("vulnerable_version_range")
    private final String vulnerableVersionRange;

    @JsonProperty("fixed_version")
    private final String fixedVersion;

    @JsonProperty("html_url")
    private final String htmlUrl;

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("dismissed_at")
    private final String dismissedAt;

    public DependabotAlert(
            long id, int alertNumber, String state, String severity, String packageName,
            String vulnerableVersionRange, String fixedVersion, String htmlUrl,
            String createdAt, String dismissedAt) {
        this.id = id;
        this.alertNumber = alertNumber;
        this.state = state;
        this.severity = severity;
        this.packageName = packageName;
        this.vulnerableVersionRange = vulnerableVersionRange;
        this.fixedVersion = fixedVersion;
        this.htmlUrl = htmlUrl;
        this.createdAt = createdAt;
        this.dismissedAt = dismissedAt;
    }

    public long getId() {
        return id;
    }

    public int getAlertNumber() {
        return alertNumber;
    }

    public String getState() {
        return state;
    }

    public String getSeverity() {
        return severity;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getVulnerableVersionRange() {
        return vulnerableVersionRange;
    }

    public String getFixedVersion() {
        return fixedVersion;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDismissedAt() {
        return dismissedAt;
    }

    @Override
    public String toString() {
        return "DependabotAlert{" +
                "id=" + id +
                ", alertNumber=" + alertNumber +
                ", state='" + state + '\'' +
                ", severity='" + severity + '\'' +
                ", packageName='" + packageName + '\'' +
                ", vulnerableVersionRange='" + vulnerableVersionRange + '\'' +
                ", fixedVersion='" + fixedVersion + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", dismissedAt='" + dismissedAt + '\'' +
                '}';
    }
}

