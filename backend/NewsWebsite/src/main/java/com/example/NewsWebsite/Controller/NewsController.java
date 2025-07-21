package com.example.NewsWebsite.Controller;

import com.example.NewsWebsite.Model.ArticleDTO;
import com.example.NewsWebsite.Service.NewsAggregatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {

    @Autowired
    private NewsAggregatorService newsAggregatorService;

    //search by query endpoint
    @GetMapping("/search")
    public ResponseEntity<List<ArticleDTO>> searchNews(@RequestParam String query, @RequestParam(defaultValue = "en") String language) {
        List<ArticleDTO> articles = newsAggregatorService.searchNews(query, language);
        return ResponseEntity.ok(articles);
    }

    //endpoint to get sources from both APIs
    @GetMapping("/sources")
    public ResponseEntity<List<String>> getSources(@RequestParam(defaultValue = "en") String language){
        List<String> sources=newsAggregatorService.getSources(language);
        return ResponseEntity.ok(sources);
    }

    //endpoint to get top headlines
    @GetMapping("/top-headlines")
    public ResponseEntity<List<ArticleDTO>> getTopHeadlines(@RequestParam(defaultValue = "en") String language) {
        List<ArticleDTO> topHeadlines = newsAggregatorService.getTopHeadlines(language);
        return ResponseEntity.ok(topHeadlines);
    }

    //endpoint to get news by source
    @GetMapping("/search-by-source")
    public ResponseEntity<List<ArticleDTO>> searchNewsBySource(@RequestParam String source, @RequestParam(defaultValue = "en") String language) {
        List<ArticleDTO> articles = newsAggregatorService.searchNewsBySource(source, language);
        return ResponseEntity.ok(articles);
    }

    //endpoint to get news by category
    @GetMapping("/search-by-category")
    public ResponseEntity<List<ArticleDTO>> searchNewsByCategory(@RequestParam String category, @RequestParam(defaultValue = "en") String language) {
        List<ArticleDTO> articles = newsAggregatorService.searchNewsByCategory(category, language);
        return ResponseEntity.ok(articles);
    }

    //endpoint to get categories
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(@RequestParam(defaultValue = "en") String language) {
        List<String> categories = newsAggregatorService.getCategories(language);
        return ResponseEntity.ok(categories);
    }

   //endpoint to search by date
    @GetMapping("/search-by-date")
    public ResponseEntity<List<ArticleDTO>> searchNewsByDate(@RequestParam String fromDate, @RequestParam String toDate, @RequestParam(defaultValue = "en") String language) {
        List<ArticleDTO> articles = newsAggregatorService.searchNewsByDate(fromDate, toDate, language);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/search-by-sentiment")
    public ResponseEntity<List<ArticleDTO>> searchNewsBySentiment(@RequestParam String sentiment, @RequestParam(defaultValue = "en") String language) {
        List<ArticleDTO> articles = newsAggregatorService.searchNewsBySentiment(sentiment, language);
        return ResponseEntity.ok(articles);
    }
}

