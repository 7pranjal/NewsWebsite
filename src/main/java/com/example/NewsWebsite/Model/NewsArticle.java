package com.example.NewsWebsite.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsArticle {
    private String title;
    private String description;
    private String content;
    private String url;
    private String imageUrl;
    private String source;
    private String publishedAt;
    private String language;
}
