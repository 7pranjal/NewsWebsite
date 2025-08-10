package com.example.NewsWebsite.Service;

import com.example.NewsWebsite.Model.ArticleDTO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GNewsService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GNewsService.class);


     @Autowired
     private SentimentService sentimentService;;
    @Value("${gnews.key}")
    private String token;

    private final String BASE_URL = "https://gnews.io/api/v4/search";
    private final String TOP_HEADLINES_URL = "https://gnews.io/api/v4/top-headlines?";

    // for query search
    public List<ArticleDTO> getNews(String query, String language) {
        List<ArticleDTO> articles = new ArrayList<>();

        try {
            String url = BASE_URL +
                    "?q=" + query +
                    "&lang=" + language +
                    "&sortby=publishedAt" +
                    "&token=" + token;

            WebClient webClient = WebClient.create();
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JSONObject json = new JSONObject(response);

            if (!json.has("articles")) {
                log.warn("GNews response missing 'articles' field: {}", response);
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
                        a.optString("image"),
                        (source != null ? source.optString("name") : "Unknown"),
                        parseDate(a.optString("publishedAt")),
                        a.optString("language"),
                        sentiment,
                        null  // Category unknown for search results
                );

                articles.add(article);
            }

        } catch (Exception e) {
            log.error("GNews fetch failed: {}", e.getMessage());
        }

        return articles;
    }

    public List<ArticleDTO> getNewsBySource(String sourceName, String language) {
        String url = BASE_URL +
                "?q=latest" +
                "&lang=" + language +
                "&sortby=publishedAt" +
                "&apikey=" + token;

        try {
            String response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArticles = jsonObject.getJSONArray("articles");

            List<ArticleDTO> articles = new ArrayList<>();

            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject a = jsonArticles.getJSONObject(i);
                JSONObject sourceObj = a.optJSONObject("source");
                String articleSourceName = sourceObj != null ? sourceObj.optString("name", "") : "";

                if (!articleSourceName.trim().equalsIgnoreCase(sourceName.trim())) {
                    continue;
                }

                ArticleDTO article = new ArticleDTO(
                    a.optString("title"),
                    a.optString("description"),
                    a.optString("content"),
                    a.optString("url"),
                    a.optString("image"),
                    articleSourceName,
                    parseDate(a.optString("publishedAt")),
                    language,
                    sentimentService.analyzeSentiment(a.optString("title") + " " + a.optString("description")),
                    null // Category unknown for search results
                );
                articles.add(article);
            }

            return articles;

        } catch (Exception e) {
            log.error("Error fetching news using WebClient: " + e.getMessage());
            return Collections.emptyList();
        }
    }




    //get sources from the API

    public List<String> getSources(String language) {
        Set<String> sources = new LinkedHashSet<>();
        try {
            // You can use top headlines or any news endpoint
            String url = "https://gnews.io/api/v4/search?q=latest&lang="+language+"&sortby=publishedAt&apikey=" + token;
            WebClient webClient = WebClient.create();
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                log.warn("GNews response was null");
                return new ArrayList<>(sources);
            }

            JSONObject json = new JSONObject(response);
            if (!json.has("articles")) {
                log.warn("GNews top headlines response missing 'articles' field: {}", response);
                return new ArrayList<>(sources);
            }

            JSONArray jsonArticles = json.getJSONArray("articles");
            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject a = jsonArticles.getJSONObject(i);
                JSONObject sourceObj = a.optJSONObject("source");
                String articleSourceName = sourceObj != null ? sourceObj.optString("name", "") : "";
                if (articleSourceName != null && !articleSourceName.isEmpty()) {
                    sources.add(articleSourceName);
                }
            }

        } catch (Exception e) {
            log.error("GNews fetch sources from articles failed: {}", e.getMessage(), e);
        }
        return new ArrayList<>(sources);
    }


    //get top headlines

    public List<ArticleDTO> getTopHeadlines(String language) {
        List<ArticleDTO> topHeadlines = new ArrayList<>();
        try {
            String url = TOP_HEADLINES_URL +
                    "lang=" + language + "&token=" + token;

            WebClient webClient = WebClient.create();
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JSONObject json = new JSONObject(response);

            if (!json.has("articles")) {
                log.warn("GNews top headlines response missing 'articles' field: {}", response);
                return topHeadlines;
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
                        a.optString("image"),
                        (source != null ? source.optString("name") : "Unknown"),
                        parseDate(a.optString("publishedAt")),
                        language,
                        sentiment,
                        null // Category unknown for search results
                );

                topHeadlines.add(article);
            }

        } catch (Exception e) {
            log.error("GNews fetch top headlines failed: {}", e.getMessage());
        }
        return topHeadlines;
    }

    //get categories

    public List<String> getCategories(String language) {
        List<String> categories = new ArrayList<>();
        return Arrays.asList(
                "general", "world", "nation", "business", "technology", "entertainment", "sports", "science", "health"
        );
    }

    //get news by categories
    public List<ArticleDTO> getNewsByCategory(String category, String language) {
        List<ArticleDTO> articles = new ArrayList<>();
        try {
            String url = "https://gnews.io/api/v4/top-headlines?category=" + category + "&lang=" + language + "&token=" + token;
            WebClient webClient = WebClient.create();
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JSONObject json = new JSONObject(response);

            if (!json.has("articles")) {
                log.warn("GNews response missing 'articles' field: {}", response);
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
                        a.optString("image"),
                        (source != null ? source.optString("name") : "Unknown"),
                        parseDate(a.optString("publishedAt")),
                        a.optString("language"),
                        sentiment,
                        category  // Add category information
                );

                articles.add(article);
            }
        } catch (Exception e) {
            log.error("GNews fetch by category failed: {}", e.getMessage());
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

    //combined news but no source wise filtering
    public List<ArticleDTO> getCombinedNews(String query, String language, String category, String sentiment,String source) {
        List<ArticleDTO> allArticles = new ArrayList<>();

        StringBuilder urlBuilder = new StringBuilder(TOP_HEADLINES_URL);


            if(category != null && !category.trim().isEmpty()) {
                urlBuilder.append("category=").append(category.trim()).append("&");
            }

            if (query != null && !query.trim().isEmpty()) {
                urlBuilder.append("q=").append(query.trim()).append("&");
            }

            if (language != null && !language.trim().isEmpty()) {
                urlBuilder.append("lang=").append(language.trim()).append("&");
            }



        urlBuilder.append("apikey=").append(token);

        try {
            String response = webClientBuilder.build()
                    .get()
                    .uri(urlBuilder.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArticles = jsonObject.getJSONArray("articles");

            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject a = jsonArticles.getJSONObject(i);
                JSONObject sourceObj = a.optJSONObject("source");
                String articleSource = sourceObj != null ? sourceObj.optString("name", "") : "";

                if (source != null && !source.trim().isEmpty() &&
                        !articleSource.equalsIgnoreCase(source.trim())) {
                    continue;
                }

                String title = a.optString("title", "");
                String description = a.optString("description", "");
                String content = a.optString("content", "");

                String combinedText = (title + " " + description + " " + content).trim();
                String analyzedSentiment = sentimentService.analyzeSentiment(combinedText);

                if (sentiment != null && !sentiment.trim().isEmpty() &&
                        !analyzedSentiment.equalsIgnoreCase(sentiment.trim())) {
                    continue;
                }

                ArticleDTO article = new ArticleDTO(
                        title,
                        description,
                        content,
                        a.optString("url"),
                        a.optString("image"),
                        articleSource,
                        parseDate(a.optString("publishedAt")),
                        language,
                        analyzedSentiment,
                        category
                );

                allArticles.add(article);
            }

        } catch (Exception e) {
            log.error("Error fetching news in searchCombined: " + e.getMessage());
            return Collections.emptyList();
        }

        return allArticles;
    }








}

