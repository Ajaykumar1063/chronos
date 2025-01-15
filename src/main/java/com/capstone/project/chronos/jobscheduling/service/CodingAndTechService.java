package com.capstone.project.chronos.jobscheduling.service;


import com.capstone.project.chronos.jobscheduling.model.CodingChallenge;
import com.capstone.project.chronos.jobscheduling.model.TechSuggestion;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class CodingAndTechService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Fetch Daily Coding Challenge from LeetCode
     *
     * @return
     */
    public CodingChallenge fetchDailyLeetCodeChallenge() {
        String graphqlUrl = "https://leetcode.com/graphql";
        String query = "{ \"query\": \"query { randomQuestion { titleSlug title difficulty } }\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        ResponseEntity<String> response = restTemplate.postForEntity(graphqlUrl, query, String.class, headers);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse response
            // Assuming response has titleSlug, title, and difficulty
            return new CodingChallenge("Sample Title", "https://leetcode.com/sample", "Medium");
        }
        throw new RuntimeException("Failed to fetch LeetCode challenge");
    }

    /**
     * Fetch Daily Tech Suggestion from Medium
     *
     * @param tag
     * @return
     */
    public TechSuggestion fetchDailyMediumArticle(String tag) {
        String rssUrl = "https://medium.com/feed/tag/" + tag;

        try {
            Document document = Jsoup.connect(rssUrl).get();
            Elements items = document.select("item");

            for (Element item : items) {
                String title = item.select("title").text();
                String link = item.select("link").text();
                String description = item.select("description").text();

                return new TechSuggestion(title, Jsoup.parse(description).text(), link, tag);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Medium articles", e);
        }

        return null;
    }

}
