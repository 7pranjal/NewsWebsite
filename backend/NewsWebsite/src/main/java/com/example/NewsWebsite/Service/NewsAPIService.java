package com.example.NewsWebsite.Service;

import com.example.NewsWebsite.Model.ArticleDTO;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class NewsAPIService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NewsAPIService.class);

    @Autowired
    private SentimentService sentimentService;

    @Value("${newsapi.key}")
    private String apiKey;

    private final String EVERYTHING_URL = "https://newsapi.org/v2/everything";

    // for query search
    public List<ArticleDTO> getNews(String query, String language) {
        List<ArticleDTO> articles = new ArrayList<>();
        try {
            String url = EVERYTHING_URL +
                    "?q=" + query +
                    "&language=" + language +
                    "&sortBy=publishedAt" +
                    "&apiKey=" + apiKey;
            WebClient webClient=WebClient.create();
            String response=webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JSONObject json=new JSONObject(response);

            if(!json.has("articles")){
                log.warn("NewsAPI response missing 'articles' field: {}", response);
                return articles;
            }

            JSONArray jsonArticles = json.getJSONArray("articles");

            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject a = jsonArticles.getJSONObject(i);
                JSONObject source = a.getJSONObject("source");

                String sentimentInput = a.optString("title") + " " + a.optString("description");
                String sentiment = sentimentService.analyzeSentiment(sentimentInput);

                ArticleDTO article = new ArticleDTO(
                        a.optString("title"),
                        a.optString("description"),
                        a.optString("content"),
                        a.optString("url"),
                        a.optString("urlToImage"),
                        source.optString("name"),
                        parseDate(a.optString("publishedAt")),
                        a.optString("language"),
                        sentiment
                );

                articles.add(article);
            }

        } catch (Exception e) {
            log.error("NewsAPI fetch failed: {}", e.getMessage());
        }

        return articles;
    }

    //for getting news between two dates
    public List<ArticleDTO> getNewsByDateAndLanguage(String query, String fromDate, String toDate, String language) {
        String url = String.format(
                "https://newsapi.org/v2/everything?q=%s&from=%s&to=%s&language=%s&apiKey=%s",
                query, fromDate, toDate, language, apiKey
        );

        List<ArticleDTO> articles = new ArrayList<>();
        try {
            WebClient webClient = WebClient.create();
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JSONObject json = new JSONObject(response);

            if (!json.has("articles")) {
                log.warn("NewsAPI response missing 'articles' field: {}", response);
                return articles;
            }

            JSONArray jsonArticles = json.getJSONArray("articles");
            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject a = jsonArticles.getJSONObject(i);
                JSONObject source = a.optJSONObject("source");

                String sentimentInput = a.optString("title") + " " + a.optString("description");
                String sentiment = sentimentService.analyzeSentiment(sentimentInput);

                ArticleDTO article = new ArticleDTO(
                        a.optString("title"),
                        a.optString("description"),
                        a.optString("content"),
                        a.optString("url"),
                        a.optString("urlToImage"),
                        (source != null ? source.optString("name") : "Unknown"),
                        parseDate(a.optString("publishedAt")),
                        language,
                        sentiment
                );

                articles.add(article);
            }

        } catch (Exception e) {
            log.error("NewsAPI fetch by date failed: {}", e.getMessage());
        }
        return articles;
    }

    //for getting sources
    public List<String> getSources(String language) {
        List<String> sources = new ArrayList<>();
        try {
            String url = "https://newsapi.org/v2/top-headlines/sources?language=" + language + "&apiKey=" + apiKey;
            WebClient webClient = WebClient.create();
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JSONObject json = new JSONObject(response);

            if (!json.has("sources")) {
                log.warn("NewsAPI response missing 'sources' field: {}", response);
                return sources;
            }

            JSONArray jsonSources = json.getJSONArray("sources");
            for (int i = 0; i < jsonSources.length(); i++) {
                JSONObject source = jsonSources.getJSONObject(i);
                sources.add(source.optString("name"));
            }

        } catch (Exception e) {
            log.error("NewsAPI fetch sources failed: {}", e.getMessage());
        }

        return sources;
    }

    //for getting news by sources
    public List<ArticleDTO> getNewsBySource(String sourceName, String language) {
        List<ArticleDTO> articles = new ArrayList<>();
        try {

            String sourcesUrl = "https://newsapi.org/v2/top-headlines/sources?language=" + language + "&apiKey=" + apiKey;
            WebClient webClient = WebClient.create();

            String sourcesResponse = webClient.get()
                    .uri(sourcesUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject sourcesJson = new JSONObject(sourcesResponse);
            JSONArray sourcesArray = sourcesJson.getJSONArray("sources");


            String matchedSourceId = null;
            for (int i = 0; i < sourcesArray.length(); i++) {
                JSONObject sourceObj = sourcesArray.getJSONObject(i);
                String name = sourceObj.optString("name");
                if (name != null && name.equalsIgnoreCase(sourceName)) {
                    matchedSourceId = sourceObj.optString("id");
                    break;
                }
            }

            if (matchedSourceId == null || matchedSourceId.isEmpty()) {
                log.warn("No matching source ID found for source name: {} in language: {}", sourceName, language);
                return articles;
            }


            String newsUrl = "https://newsapi.org/v2/everything?sources=" + matchedSourceId
                    + "&language=" + language
                    + "&apiKey=" + apiKey;

            String newsResponse = webClient.get()
                    .uri(newsUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject newsJson = new JSONObject(newsResponse);
            if (!newsJson.has("articles")) {
                log.warn("No articles field in response from NewsAPI for source: {}", matchedSourceId);
                return articles;
            }

            JSONArray jsonArticles = newsJson.getJSONArray("articles");
            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject a = jsonArticles.getJSONObject(i);
                JSONObject sourceObj = a.optJSONObject("source");

                String sentimentInput = a.optString("title") + " " + a.optString("description");
                String sentiment = sentimentService.analyzeSentiment(sentimentInput);

                ArticleDTO article = new ArticleDTO(
                        a.optString("title"),
                        a.optString("description"),
                        a.optString("content"),
                        a.optString("url"),
                        a.optString("urlToImage"),
                        (sourceObj != null ? sourceObj.optString("name") : "Unknown"),
                        parseDate(a.optString("publishedAt")),
                        language,
                        sentiment
                );

                articles.add(article);
            }

        } catch (Exception e) {
            log.error("Failed to fetch news by source: {} and language: {}. Error: {}", sourceName, language, e.getMessage());
        }

        return articles;
    }

    //get categories
    public List<String> getCategories(String language) {
        List<String> categories = new ArrayList<>();
        try {
            String url = "https://newsapi.org/v2/sources?language=" + language + "&apiKey=" + apiKey;
            WebClient webClient = WebClient.create();
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JSONObject json = new JSONObject(response);

            if (!json.has("sources")) {
                log.warn("NewsAPI response missing 'sources' field: {}", response);
                return categories;
            }

            JSONArray jsonSources = json.getJSONArray("sources");
            for (int i = 0; i < jsonSources.length(); i++) {
                JSONObject source = jsonSources.getJSONObject(i);
                String category = source.optString("category");
                if (category != null && !category.isEmpty() && !categories.contains(category)) {
                    categories.add(category);
                }
            }
        } catch (Exception e) {
            log.error("NewsAPI fetch categories failed: {}", e.getMessage());
        }
        return categories;
    }

    //get news by categories
    public List<ArticleDTO> getNewsByCategory(String category, String language) {
        List<ArticleDTO> articles = new ArrayList<>();
        try {
            String url = "https://newsapi.org/v2/top-headlines?category=" + category + "&language=" + language + "&apiKey=" + apiKey;
            WebClient webClient = WebClient.create();
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JSONObject json = new JSONObject(response);

            if (!json.has("articles")) {
                log.warn("NewsAPI response missing 'articles' field: {}", response);
                return articles;
            }

            JSONArray jsonArticles = json.getJSONArray("articles");
            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject a = jsonArticles.getJSONObject(i);
                JSONObject source = a.optJSONObject("source");

                String sentimentInput = a.optString("title") + " " + a.optString("description");
                String sentiment = sentimentService.analyzeSentiment(sentimentInput);

                ArticleDTO article = new ArticleDTO(
                        a.optString("title"),
                        a.optString("description"),
                        a.optString("content"),
                        a.optString("url"),
                        a.optString("urlToImage"),
                        (source != null ? source.optString("name") : "Unknown"),
                        parseDate(a.optString("publishedAt")),
                        a.optString("language"),
                        sentiment
                );

                articles.add(article);
            }
        } catch (Exception e) {
            log.error("NewsAPI fetch by category failed: {}", e.getMessage());
        }
        return articles;
    }

    //parse date from string
    private LocalDateTime parseDate(String dateString) {
        try {
            return OffsetDateTime.parse(dateString).toLocalDateTime();

        } catch (Exception e) {
            log.warn("Date parse failed: {}", dateString);
            return null;
        }
    }
}

