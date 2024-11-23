package com.fournier.dependencyanalyzer.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // Ensures unmapped fields are ignored
public class Issue {

    private final int id;
    private final String nodeId;
    private final String title;
    private final String body;
    private final String state;
    private final String url;

    @JsonProperty("html_url")
    private final String htmlUrl;

    private final User user; // Nested object for the issue creator
    private final List<Label> labels;

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("updated_at")
    private final String updatedAt;

    @JsonProperty("closed_at")
    private final String closedAt;

    private final int comments;
    private final String milestone;

    public Issue(
            int id, String nodeId, String title, String body, String state, String url, String htmlUrl, User user,
            List<Label> labels, String createdAt, String updatedAt, String closedAt, int comments, String milestone
    ) {
        this.id = id;
        this.nodeId = nodeId;
        this.title = title;
        this.body = body;
        this.state = state;
        this.url = url;
        this.htmlUrl = htmlUrl;
        this.user = user;
        this.labels = labels;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.closedAt = closedAt;
        this.comments = comments;
        this.milestone = milestone;
    }

    public int getId() {
        return id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getState() {
        return state;
    }

    public String getUrl() {
        return url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public User getUser() {
        return user;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public int getComments() {
        return comments;
    }

    public String getMilestone() {
        return milestone;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", state='" + state + '\'' +
                ", url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", user=" + user +
                ", labels=" + labels +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", closedAt='" + closedAt + '\'' +
                ", comments=" + comments +
                ", milestone='" + milestone + '\'' +
                '}';
    }
}

