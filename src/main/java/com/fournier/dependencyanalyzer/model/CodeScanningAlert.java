package com.fournier.dependencyanalyzer.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeScanningAlert {

    private final long id;

    @JsonProperty("number")
    private final int alertNumber;

    @JsonProperty("state")
    private final String state;

    @JsonProperty("severity")
    private final String severity;

    @JsonProperty("tool")
    private final String tool;

    @JsonProperty("rule_id")
    private final String ruleId;

    @JsonProperty("html_url")
    private final String htmlUrl;

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("dismissed_at")
    private final String dismissedAt;

    public CodeScanningAlert(
            long id, int alertNumber, String state, String severity, String tool, String ruleId,
            String htmlUrl, String createdAt, String dismissedAt) {
        this.id = id;
        this.alertNumber = alertNumber;
        this.state = state;
        this.severity = severity;
        this.tool = tool;
        this.ruleId = ruleId;
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

    public String getTool() {
        return tool;
    }

    public String getRuleId() {
        return ruleId;
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
        return "CodeScanningAlert{" +
                "id=" + id +
                ", alertNumber=" + alertNumber +
                ", state='" + state + '\'' +
                ", severity='" + severity + '\'' +
                ", tool='" + tool + '\'' +
                ", ruleId='" + ruleId + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", dismissedAt='" + dismissedAt + '\'' +
                '}';
    }
}