package com.fournier.dependencyanalyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Milestone {

    private String title;
    private String description;

    @JsonProperty("due_on")
    private String dueOn;

    // Other fields from the API response, if needed

    public Milestone() {}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueOn() {
        return dueOn;
    }

    @Override
    public String toString() {
        return "Milestone{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueOn='" + dueOn + '\'' +
                '}';
    }
}
