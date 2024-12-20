package com.fournier.dependencyanalyzer.model;

public class Contributor extends User {

    private Integer contributions;
    private String name;
    private String email;
    private String date;

    public Contributor(String login, Long id, String nodeId, String avatarUrl, String htmlUrl, Boolean siteAdmin,
                       Integer contributions, String name, String email, String date) {
        super(login, id, nodeId, avatarUrl, htmlUrl, siteAdmin);
        this.contributions = contributions;
        this.name = name;
        this.email = email;
        this.date = date;
    }

    public Integer getContributions() {
        return contributions;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
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
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
