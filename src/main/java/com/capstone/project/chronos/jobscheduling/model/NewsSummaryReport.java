package com.capstone.project.chronos.jobscheduling.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsSummaryReport {
    private String title;
    private String description;
    private String category; // "India" or "Rest of the World"
    private String imageUrl;
    private String sourceUrl;

    // Method to get a formatted summary
    public String getSummary() {
        return new StringBuilder()
                .append("Title: ").append(title).append("\n")
                .append("Description: ").append(description).append("\n")
                .append("Source: ").append(sourceUrl).append("\n")
                .append(imageUrl != null ? "Image: " + imageUrl + "\n" : "")
                .toString();
    }
}
