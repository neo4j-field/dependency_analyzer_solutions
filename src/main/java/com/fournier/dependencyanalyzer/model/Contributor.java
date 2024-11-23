package com.fournier.dependencyanalyzer.model;

public class Contributor extends User {

    private final Integer contributions;

    public Contributor(String login, Long id, String nodeId, String avatarUrl, String htmlUrl, Boolean siteAdmin, Integer contributions) {
        super(login, id, nodeId, avatarUrl, htmlUrl, siteAdmin);
        this.contributions = contributions;
    }

    public Integer getContributions() {
        return contributions;
    }

    @Override
    public String toString() {
        return "Contributor{" +
                "login='" + getLogin() + '\'' +
                ", id=" + getId() +
                ", nodeId='" + getNodeId() + '\'' +
                ", avatarUrl='" + getAvatarUrl() + '\'' +
                ", htmlUrl='" + getHtmlUrl() + '\'' +
                ", siteAdmin=" + getSiteAdmin() +
                ", contributions=" + contributions +
                '}';
    }
}
