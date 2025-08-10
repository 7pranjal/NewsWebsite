# Postman Test Endpoints for Combined Search

## Base URL
```
http://localhost:8080/api/news
```

## 1. Basic Combined Search Tests

### Test 1: Simple Query Search
```
GET http://localhost:8080/api/news/search/combined?query=technology
```

### Test 2: Category Only Search
```
GET http://localhost:8080/api/news/search/combined?category=business
```

### Test 3: Query + Category
```
GET http://localhost:8080/api/news/search/combined?query=AI&category=technology
```

## 2. Advanced Filtering Tests

### Test 4: Date Range Search (FIXED - using fromDate and toDate)
```
GET http://localhost:8080/api/news/search/combined?query=technology&fromDate=2024-01-01&toDate=2024-01-31
```

### Test 5: Sentiment Filtering
```
GET http://localhost:8080/api/news/search/combined?query=technology&sentiment=positive
```

### Test 6: Source Specific Search
```
GET http://localhost:8080/api/news/search/combined?query=technology&source=newsapi
```

### Test 7: Language Filtering
```
GET http://localhost:8080/api/news/search/combined?query=technology&language=en
```

## 3. Complex Combined Filters

### Test 8: Multiple Filters (Technology + Positive Sentiment + Date Range)
```
GET http://localhost:8080/api/news/search/combined?query=technology&category=technology&sentiment=positive&fromDate=2024-01-01&toDate=2024-01-31
```

### Test 9: Business News with Date Range
```
GET http://localhost:8080/api/news/search/combined?category=business&fromDate=2024-01-01&toDate=2024-01-15&language=en
```

### Test 10: AI News from Specific Source with Date Range
```
GET http://localhost:8080/api/news/search/combined?query=AI&source=gnews&sentiment=positive&fromDate=2024-01-01&toDate=2024-01-31
```

## 4. Edge Cases and Error Testing

### Test 11: Empty Query (Should Return All News)
```
GET http://localhost:8080/api/news/search/combined
```

### Test 12: Invalid Category (Should Handle Gracefully)
```
GET http://localhost:8080/api/news/search/combined?category=invalidcategory
```

### Test 13: Invalid Date Format (Should Handle Gracefully)
```
GET http://localhost:8080/api/news/search/combined?fromDate=invalid-date&toDate=2024-01-31
```

### Test 14: Non-English Language
```
GET http://localhost:8080/api/news/search/combined?query=technology&language=es
```

## 5. Source-Specific Tests

### Test 15: NewsAPI Only
```
GET http://localhost:8080/api/news/search/combined?query=technology&source=newsapi
```

### Test 16: GNews Only
```
GET http://localhost:8080/api/news/search/combined?query=technology&source=gnews
```

### Test 17: All Sources (Default)
```
GET http://localhost:8080/api/news/search/combined?query=technology&source=all
```

## 6. Category-Specific Tests

### Test 18: Business Category
```
GET http://localhost:8080/api/news/search/combined?category=business
```

### Test 19: Technology Category
```
GET http://localhost:8080/api/news/search/combined?category=technology
```

### Test 20: Sports Category
```
GET http://localhost:8080/api/news/search/combined?category=sports
```

### Test 21: Entertainment Category
```
GET http://localhost:8080/api/news/search/combined?category=entertainment
```

## 7. Sentiment Analysis Tests

### Test 22: Positive Sentiment
```
GET http://localhost:8080/api/news/search/combined?sentiment=positive
```

### Test 23: Negative Sentiment
```
GET http://localhost:8080/api/news/search/combined?sentiment=negative
```

### Test 24: Neutral Sentiment
```
GET http://localhost:8080/api/news/search/combined?sentiment=neutral
```

## 8. Performance and Load Tests

### Test 25: Large Date Range
```
GET http://localhost:8080/api/news/search/combined?fromDate=2023-01-01&toDate=2024-01-31
```

### Test 26: Broad Query
```
GET http://localhost:8080/api/news/search/combined?query=news
```

## 9. Date Filtering Specific Tests (FIXED)

### Test 27: Recent News (Last Week)
```
GET http://localhost:8080/api/news/search/combined?fromDate=2024-01-08&toDate=2024-01-15
```

### Test 28: Date Range with Query
```
GET http://localhost:8080/api/news/search/combined?query=technology&fromDate=2024-01-01&toDate=2024-01-31
```

### Test 29: Date Range with Category
```
GET http://localhost:8080/api/news/search/combined?category=business&fromDate=2024-01-01&toDate=2024-01-31
```

### Test 30: Date Range with Source
```
GET http://localhost:8080/api/news/search/combined?source=newsapi&fromDate=2024-01-01&toDate=2024-01-31
```

## Postman Collection Setup

### Headers
```
Content-Type: application/json
Accept: application/json
```

### Environment Variables (Optional)
```
base_url: http://localhost:8080
api_path: /api/news
```

## Expected Response Format
```json
[
  {
    "title": "Article Title",
    "description": "Article description...",
    "url": "https://example.com/article",
    "urlToImage": "https://example.com/image.jpg",
    "publishedAt": "2024-01-15T10:30:00",
    "source": {
      "name": "Source Name",
      "id": "source-id"
    },
    "author": "Author Name",
    "content": "Article content...",
    "sentiment": "positive",
    "category": "technology"
  }
]
```

## Testing Tips

1. **Start Simple**: Begin with basic queries to ensure the endpoint is working
2. **Test Each Filter**: Test each parameter individually before combining
3. **Check Response Times**: Monitor how long each request takes
4. **Verify Deduplication**: Check that no duplicate articles appear
5. **Test Error Handling**: Try invalid parameters to ensure graceful handling
6. **Compare Sources**: Test the same query with different sources to see differences
7. **Date Format**: Always use YYYY-MM-DD format for dates

## Common Issues to Watch For

- **CORS Issues**: If testing from a browser, you might need CORS configuration
- **API Rate Limits**: External APIs may have rate limits
- **Date Format**: Ensure dates are in YYYY-MM-DD format
- **Empty Results**: Some combinations might return empty results (this is normal)
- **Timeout**: Large date ranges or complex queries might take longer

## Quick Test Sequence

1. Test basic functionality: `GET /api/news/search/combined?query=technology`
2. Test category filtering: `GET /api/news/search/combined?category=business`
3. Test date filtering: `GET /api/news/search/combined?fromDate=2024-01-01&toDate=2024-01-31`
4. Test combined filters: `GET /api/news/search/combined?query=AI&category=technology&sentiment=positive&fromDate=2024-01-01&toDate=2024-01-31`
5. Test source filtering: `GET /api/news/search/combined?query=technology&source=newsapi`

## IMPORTANT: Date Parameter Fix

**The date parameters have been fixed from `from`/`to` to `fromDate`/`toDate` to match the service method signatures.**

### Before (Broken):
```
GET /api/news/search/combined?from=2024-01-01&to=2024-01-31
```

### After (Fixed):
```
GET /api/news/search/combined?fromDate=2024-01-01&toDate=2024-01-31
``` 