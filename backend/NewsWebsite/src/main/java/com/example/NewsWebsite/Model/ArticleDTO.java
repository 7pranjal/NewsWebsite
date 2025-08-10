package com.example.NewsWebsite.Model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ArticleDTO {
     private String title;
     private String description;
     private String content;
     private String url;
     private String imageUrl;
     private String sourceName;
     private LocalDateTime publishedAt;
     private String language;
     private String sentiment;
     private String Category;

     
     public ArticleDTO(String title, String description, String content, String url, String urlToImage, String sourceName, LocalDateTime publishedAt, String language, String sentiment, String Category) {
          this.title = title;
          this.description = description;
          this.content = content;
          this.url = url;
          this.imageUrl = urlToImage;
          this.sourceName = sourceName;
          this.publishedAt = publishedAt;
          this.language = language;
          this.sentiment = sentiment;
            this.Category = Category;
     }



     public String getTitle() {
          return title;
     }

     public void setTitle(String title) {
          this.title = title;
     }

     public String getDescription() {
          return description;
     }

     public void setDescription(String description) {
          this.description = description;
     }

     public String getContent() {
          return content;
     }

     public void setContent(String content) {
          this.content = content;
     }

     public String getUrl() {
          return url;
     }

     public void setUrl(String url) {
          this.url = url;
     }

     public String getImageUrl() {
          return imageUrl;
     }

     public void setImageUrl(String imageUrl) {
          this.imageUrl = imageUrl;
     }

     public String getSourceName() {
          return sourceName;
     }

     public void setSourceName(String sourceName) {
          this.sourceName = sourceName;
     }

     public LocalDateTime getPublishedAt() {
          return publishedAt;
     }

     public void setPublishedAt(LocalDateTime publishedAt) {
          this.publishedAt = publishedAt;
     }

     public String getLanguage() {
          return language;
     }

     public void setLanguage(String language) {
          this.language = language;
     }

     public String getSentiment() {
          return sentiment;
     }

     public void setSentiment(String sentiment) {
          this.sentiment = sentiment;
     }
        public String getCategory() {
            return Category;
        }
        public void setCategory(String category) {
            this.Category = category;
        }



}
