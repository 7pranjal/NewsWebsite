# Combined Search API Documentation

## Overview

The Combined Search API provides a unified endpoint to search for news articles across multiple sources (NewsAPI and GNews) with advanced filtering capabilities. This API allows you to combine multiple search criteria in a single request.

## Endpoint

### GET `/api/news/search/combined`

**Base URL**: `http://localhost:8080/api/news/search/combined`

## Query Parameters

| Parameter | Type | Required | Description | Example |
|-----------|------|----------|-------------|---------|
| `query` | String | No | Search keywords in title, description, or content | "artificial intelligence" |
| `category` | String | No | News category | "technology", "business" |
| `source` | String | No | News source filter | "newsapi", "gnews", "all" |
| `sentiment` | String | No | Sentiment analysis result | "positive", "negative" |
| `from` | String | No | Start date (YYYY-MM-DD) | "2025-01-01" |
| `to` | String | No | End date (YYYY-MM-DD) | "2025-01-31" |
| `language` | String | No | Language code (default: "en") | "en", "es", "fr" |

## Search Logic

The API uses intelligent search logic based on the provided parameters:

### 1. Category-Based Search
- If `category` is provided, the API searches for top headlines in that category
- Uses NewsAPI's `/top-headlines` endpoint with category parameter
- Uses GNews's `/top-headlines` endpoint with category parameter

### 2. Query-Based Search
- If no category is provided, the API performs a keyword search
- If date range (`from` and `to`) is provided, searches within that date range
- Otherwise, performs a general search

### 3. Source Filtering
- `source=newsapi`: Only search NewsAPI
- `source=gnews`: Only search GNews
- `source=all` or not provided: Search both sources

### 4. Sentiment Filtering
- Applied after retrieving articles from APIs
- Filters articles based on sentiment analysis results
- Only returns articles matching the specified sentiment

## Usage Examples

### Example 1: Category Search
```bash
curl "http://localhost:8080/api/news/search/combined?category=technology&language=en"
```

### Example 2: Query Search with Date Range
```bash
curl "http://localhost:8080/api/news/search/combined?query=AI&from=2025-01-01&to=2025-01-31&language=en"
```

### Example 3: Source-Specific Search
```bash
curl "http://localhost:8080/api/news/search/combined?query=technology&source=newsapi&language=en"
```

### Example 4: Sentiment-Based Search
```bash
curl "http://localhost:8080/api/news/search/combined?category=business&sentiment=positive&language=en"
```

### Example 5: Complex Multi-Filter Search
```bash
curl "http://localhost:8080/api/news/search/combined?query=artificial%20intelligence&category=technology&source=all&sentiment=positive&from=2025-01-01&to=2025-01-31&language=en"
```

## Response Format

The API returns a JSON array of `ArticleDTO` objects:

```json
[
  {
    "title": "Article Title",
    "description": "Article description",
    "content": "Full article content",
    "url": "https://example.com/article",
    "imageUrl": "https://example.com/image.jpg",
    "sourceName": "BBC News",
    "publishedAt": "2025-01-15T10:30:00",
    "language": "en",
    "sentiment": "positive",
    "category": "technology"
  }
]
```

## API Limitations

### NewsAPI (newsapi.org)
- **Language**: Only supports English ("en")
- **Date Range**: Limited historical data (after 2025-06-20)
- **Category**: Supported in `/top-headlines` endpoint
- **Query**: Supported in `/everything` endpoint

### GNews API (gnews.io)
- **Language**: Supports multiple languages
- **Date Range**: Better historical data support
- **Category**: Supported in `/top-headlines` endpoint
- **Query**: Supported in `/search` endpoint

## Error Handling

The API gracefully handles:
- Invalid date formats
- Missing API responses
- Network timeouts
- Invalid filter combinations
- Empty results

All errors are logged, and the API continues to return results from available sources.

## Performance Features

1. **Smart API Selection**: Chooses the best API based on filter requirements
2. **Deduplication**: Removes duplicate articles automatically
3. **Error Resilience**: Continues operation even if one API fails
4. **Caching**: Results are cached to avoid redundant API calls
5. **Sorting**: Results are sorted by publication date (newest first)

## Best Practices

1. **Use Specific Categories**: For better results, use specific categories like "technology", "business"
2. **Combine Filters Wisely**: Don't use too many restrictive filters at once
3. **Date Ranges**: Use reasonable date ranges to avoid overwhelming results
4. **Language Consideration**: NewsAPI only works with English
5. **Source Filtering**: Use source filtering to reduce API calls and improve performance

## Frontend Integration Example

```javascript
// JavaScript example for frontend integration
async function searchCombined(filters) {
    const params = new URLSearchParams();
    
    if (filters.query) params.append('query', filters.query);
    if (filters.category) params.append('category', filters.category);
    if (filters.source) params.append('source', filters.source);
    if (filters.sentiment) params.append('sentiment', filters.sentiment);
    if (filters.from) params.append('from', filters.from);
    if (filters.to) params.append('to', filters.to);
    if (filters.language) params.append('language', filters.language);
    
    const response = await fetch(`/api/news/search/combined?${params}`);
    return await response.json();
}

// Usage examples
const techNews = await searchCombined({
    category: 'technology',
    language: 'en'
});

const aiNews = await searchCombined({
    query: 'artificial intelligence',
    from: '2025-01-01',
    to: '2025-01-31',
    sentiment: 'positive',
    language: 'en'
});
```

## Troubleshooting

### Common Issues

1. **No Results**: Check if filters are too restrictive
2. **Slow Response**: Reduce the number of concurrent filters
3. **Date Errors**: Ensure date format is YYYY-MM-DD
4. **Language Issues**: Use "en" for NewsAPI compatibility

### Debug Tips

1. Start with minimal filters and add more gradually
2. Check API logs for error messages
3. Verify date formats and ranges
4. Test with different language codes
5. Use source filtering to isolate issues 