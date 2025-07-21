package com.example.NewsWebsite.Service;

import com.example.NewsWebsite.Model.ArticleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewsAggregatorService {

    private static final Logger logger = LoggerFactory.getLogger(NewsAggregatorService.class);

    private final NewsAPIService newsAPIService;
    private final GNewsService gNewsService;

    @Autowired
    public NewsAggregatorService(NewsAPIService newsAPIService, GNewsService gNewsService) {
        this.newsAPIService = newsAPIService;
        this.gNewsService = gNewsService;
    }

    //search news by sentiment only negative and positive are supported
    public List<ArticleDTO> searchNewsBySentiment(String sentiment, String language) {
        List<ArticleDTO> newsFromNewsAPI = new ArrayList<>();
        List<ArticleDTO> newsFromGNews = new ArrayList<>();

        if(language.equals("en")){
            try {
                newsFromNewsAPI = newsAPIService.getNews("latest", language);
            } catch (Exception e) {
                logger.error("Error fetching news from NewsAPI: {}", e.getMessage(), e);
            }
        }

        try {
            newsFromGNews = gNewsService.getNews("latest", language);
        } catch (Exception e) {
            logger.error("Error fetching news from GNews: {}", e.getMessage(), e);
        }

        Map<String, ArticleDTO> articleMap = new LinkedHashMap<>();

        for (ArticleDTO article : newsFromNewsAPI) {
            String key = generateKey(article);
            if (key != null) {
                articleMap.putIfAbsent(key, article);
            }
        }

        for (ArticleDTO article : newsFromGNews) {
            String key = generateKey(article);
            if (key != null) {
                articleMap.putIfAbsent(key, article);
            }
        }

        List<ArticleDTO> filteredArticles = new ArrayList<>();
        for (ArticleDTO article : articleMap.values()) {
            if (article.getSentiment() != null && article.getSentiment().equalsIgnoreCase(sentiment)) {
                filteredArticles.add(article);
            }
        }

        return filteredArticles;
    }


    //get sources from both APIs
    public List<String> getSources(String language){
        List<String> sources = new ArrayList<>();
        if(language.equals("en")){
            try {
                sources.addAll(newsAPIService.getSources(language));
            } catch (Exception e) {
                logger.error("Error fetching sources from NewsAPI: {}", e.getMessage(), e);
            }
        }

        try {
            sources.addAll(gNewsService.getSources(language));
        } catch (Exception e) {
            logger.error("Error fetching sources from GNews: {}", e.getMessage(), e);
        }

        return sources.stream().distinct().collect(Collectors.toList());
    }

    //get top headlines

    public List<ArticleDTO> getTopHeadlines(String language) {
        List<ArticleDTO> topHeadlines = new ArrayList<>();
        // there is no top headlines language wise in news api.
        try {
            topHeadlines.addAll(gNewsService.getTopHeadlines(language));
        } catch (Exception e) {
            logger.error("Error fetching top headlines from GNews: {}", e.getMessage(), e);
        }

        return topHeadlines.stream().distinct().collect(Collectors.toList());
    }

    //search on the basis of query
    public List<ArticleDTO> searchNews(String query, String language) {
        List<ArticleDTO> newsFromNewsAPI = new ArrayList<>();
        List<ArticleDTO> newsFromGNews = new ArrayList<>();

        try {
            newsFromNewsAPI = newsAPIService.getNews(query, language);
        } catch (Exception e) {
            logger.error("Error fetching news from NewsAPI: {}", e.getMessage(), e);
        }

        try {
            newsFromGNews = gNewsService.getNews(query, language);
        } catch (Exception e) {
            logger.error("Error fetching news from GNews: {}", e.getMessage(), e);
        }

        Map<String, ArticleDTO> articleMap = new LinkedHashMap<>();

        for (ArticleDTO article : newsFromNewsAPI) {
            String key = generateKey(article);
            if (key != null) {
                articleMap.putIfAbsent(key, article);
            }
        }

        for (ArticleDTO article : newsFromGNews) {
            String key = generateKey(article);
            if (key != null) {
                articleMap.putIfAbsent(key, article);
            }
        }

        List<ArticleDTO> mergedArticles = new ArrayList<>(articleMap.values());

        mergedArticles.sort((a1, a2) -> {
            LocalDateTime date1 = a1.getPublishedAt() != null ? a1.getPublishedAt() : LocalDateTime.MIN;
            LocalDateTime date2 = a2.getPublishedAt() != null ? a2.getPublishedAt() : LocalDateTime.MIN;
            return date2.compareTo(date1);
        });
        return mergedArticles;
    }

    //search on the basis of source

    public List<ArticleDTO> searchNewsBySource(String source, String language) {
        List<ArticleDTO> newsFromNewsAPI = new ArrayList<>();
        List<ArticleDTO> newsFromGNews = new ArrayList<>();

        if (language.equals("en")) {
            try {
                newsFromNewsAPI = newsAPIService.getNewsBySource(source, language);
            } catch (Exception e) {
                logger.error("Error fetching news from NewsAPI: {}", e.getMessage(), e);
            }
        }

        try {
            newsFromGNews = gNewsService.getNewsBySource(source, language);
        } catch (Exception e) {
            logger.error("Error fetching news from GNews: {}", e.getMessage(), e);
        }

        Map<String, ArticleDTO> articleMap = new LinkedHashMap<>();

        for (ArticleDTO article : newsFromNewsAPI) {
            String key = generateKey(article);
            if (key != null) {
                articleMap.putIfAbsent(key, article);
            }
        }

        for (ArticleDTO article : newsFromGNews) {
            String key = generateKey(article);
            if (key != null) {
                articleMap.putIfAbsent(key, article);
            }
        }

        return new ArrayList<>(articleMap.values());

    }


    //get categories from both APIs
    public List<String> getCategories(String language) {
        List<String> categories = new ArrayList<>();
        try {
            categories.addAll(newsAPIService.getCategories(language));
        } catch (Exception e) {
            logger.error("Error fetching categories from NewsAPI: {}", e.getMessage(), e);
        }
        try {
            categories.addAll(gNewsService.getCategories(language));
        } catch (Exception e) {
            logger.error("Error fetching categories from GNews: {}", e.getMessage(), e);
        }
        return categories.stream().distinct().collect(Collectors.toList());
    }

    //search on the basis of category
    public List<ArticleDTO> searchNewsByCategory(String category, String language) {
        List<ArticleDTO> newsFromNewsAPI = new ArrayList<>();
        List<ArticleDTO> newsFromGNews = new ArrayList<>();

        if (language.equals("en")) {
            try {
                newsFromNewsAPI = newsAPIService.getNewsByCategory(category, language);
            } catch (Exception e) {
                logger.error("Error fetching news by category from NewsAPI: {}", e.getMessage(), e);
            }
        }

        try {
            newsFromGNews = gNewsService.getNewsByCategory(category, language);
        } catch (Exception e) {
            logger.error("Error fetching news by category from GNews: {}", e.getMessage(), e);
        }

        Map<String, ArticleDTO> articleMap = new LinkedHashMap<>();

        for (ArticleDTO article : newsFromNewsAPI) {
            String key = generateKey(article);
            if (key != null) {
                articleMap.putIfAbsent(key, article);
            }
        }

        for (ArticleDTO article : newsFromGNews) {
            String key = generateKey(article);
            if (key != null) {
                articleMap.putIfAbsent(key, article);
            }
        }

        return new ArrayList<>(articleMap.values());
    }

    //search on the basis of date
    public List<ArticleDTO> searchNewsByDate(String fromDate, String toDate, String language) {
        List<ArticleDTO> mergedArticles = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate from = LocalDate.parse(fromDate, formatter);
            LocalDate cutoff = LocalDate.of(2025, 6, 20);

            // Call NewsAPI only if the fromDate is on or after 2025-06-20
            if (!from.isBefore(cutoff) && language.equalsIgnoreCase("en")) {
                mergedArticles.addAll(newsAPIService.getNewsByDateAndLanguage("latest", fromDate, toDate, language));
            }

            // GNews generally supports wider ranges, so we call it always
            mergedArticles.addAll(gNewsService.getNewsByDateAndLanguage("latest", fromDate, toDate, language));

        } catch (Exception e) {
            logger.error("Error fetching news: {}", e.getMessage(), e);
        }

        // Deduplicate using a LinkedHashMap
        Map<String, ArticleDTO> articleMap = new LinkedHashMap<>();
        for (ArticleDTO article : mergedArticles) {
            String key = generateKey(article);
            if (key != null) {
                articleMap.putIfAbsent(key, article);
            }
        }

        return new ArrayList<>(articleMap.values());
    }




    //to generate the unique key for each article so that none of the articles are repeated
    private String generateKey(ArticleDTO article) {
        if (article.getTitle() == null) {
            logger.warn("Article title is null, skipping key generation.");
            return null;
        }
        return article.getTitle().trim().toLowerCase() + "_" +
                (article.getSourceName() != null && !article.getSourceName().trim().isEmpty()
                        ? article.getSourceName().trim().toLowerCase() : "") + "_" +
                (article.getUrl() != null ? article.getUrl().trim().toLowerCase() : "");
    }

}
