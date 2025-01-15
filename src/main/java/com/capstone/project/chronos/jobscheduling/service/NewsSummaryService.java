package com.capstone.project.chronos.jobscheduling.service;

import com.capstone.project.chronos.jobscheduling.model.NewsSummaryReport;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewsSummaryService {

    @Value("${news.api.key}")
    private String apiKey;


    private static final String BASE_URL = "https://newsapi.org/v2/";

    private final RestTemplate restTemplate = new RestTemplate();

    public List<NewsSummaryReport> fetchIndiaNews() {
        String url = BASE_URL + "top-headlines?country=in&apiKey=" + apiKey;
        return fetchNews(url, "India");
    }

    public List<NewsSummaryReport> fetchWorldNews() {
        String url = BASE_URL + "top-headlines?language=en&apiKey=" + apiKey;
        return fetchNews(url, "World");
    }

    private List<NewsSummaryReport> fetchNews(String url, String category) {
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> articles = (List<Map<String, Object>>) responseBody.get("articles");

            return articles.stream()
                    .limit(5) // Limit to top 5 news items
                    .map(article -> new NewsSummaryReport(
                            category,
                            (String) article.get("title"),
                            (String) article.get("description"),
                            (String) article.get("url"),
                            (String) article.get("urlToImage")
                    ))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
