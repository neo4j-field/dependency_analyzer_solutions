package com.fournier.dependencyanalyzer.model;


public class Commit {
    private String sha;
    private CommitMetadata commit;
    private Contributor author;

    public Commit() {
    }

    public Commit(String sha, CommitMetadata commit, Contributor author) {
        this.sha = sha;
        this.commit = commit;
        this.author = author;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public CommitMetadata getCommit() {
        return commit;
    }

    public void setCommit(CommitMetadata commit) {
        this.commit = commit;
    }

    public Contributor getAuthor() {
        return author;
    }

    public void setAuthor(Contributor author) {
        this.author = author;
    }

    public static class CommitMetadata {
        private String message;
        private Contributor author;

        public CommitMetadata() {
        }

        public CommitMetadata(String message, Contributor author) {
            this.message = message;
            this.author = author;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Contributor getAuthor() {
            return author;
        }

        public void setAuthor(Contributor author) {
            this.author = author;
        }
    }
}
