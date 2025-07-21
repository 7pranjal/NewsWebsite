package com.example.NewsWebsite.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Service
public class SentimentService {
    private final WebClient webClient;
    public SentimentService(@Value("${sentiment.api.url}") String sentimentApiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(sentimentApiUrl)
                .build();
    }
    public String analyzeSentiment(String text){
        try{
            return webClient.post()
                    .uri("/analyze")
                    .bodyValue(Map.of("text", text))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(response -> response.containsKey("sentiment") ? (String) response.get("sentiment") : null)
                    .block();
        } catch(WebClientResponseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
