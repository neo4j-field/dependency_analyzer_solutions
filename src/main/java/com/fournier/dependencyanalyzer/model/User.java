package com.fournier.dependencyanalyzer.model;

public class User {
    private final String login;
    private final Long id;
    private final String nodeId;
    private final String avatarUrl;
    private final String htmlUrl;
    private final Boolean siteAdmin;

    public User(String login, Long id, String nodeId, String avatarUrl, String htmlUrl, Boolean siteAdmin) {
        this.login = login;
        this.id = id;
        this.nodeId = nodeId;
        this.avatarUrl = avatarUrl;
        this.htmlUrl = htmlUrl;
        this.siteAdmin = siteAdmin;
    }

    public String getLogin() {
        return login;
    }

    public Long getId() {
        return id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public Boolean getSiteAdmin() {
        return siteAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", siteAdmin=" + siteAdmin +
                '}';
    }
}
