import React, { useState, useCallback, useEffect } from 'react'
import Navbar from '../components/Navbar'
import Category from '../components/Category'
import SearchBar from '../components/SearchBar'
import Filters from '../components/Filters'
import Card from '../components/Card'
import axios from 'axios'
import { useLanguage } from '../components/LanguageContext'

function Home() {
  const { language } = useLanguage();
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedSource, setSelectedSource] = useState('');
  const[selectedSentiment,setSelectedSentiment]=useState('');
  
  const fetchArticles = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      let url;
      
      if (!searchQuery && !selectedCategory && !selectedSource && !selectedSentiment) {
        // If no filters are selected, fetch top headlines
        url = `http://localhost:8080/api/news/top-headlines?language=${language}`;
        console.log('Fetching top headlines from:', url);
      } else if (searchQuery && !selectedCategory && !selectedSource && !selectedSentiment) {
        // If only search query is selected, use the search endpoint
        url = `http://localhost:8080/api/news/search?query=${searchQuery}&language=${language}`;
        console.log('Making search query API call to:', url);
      } else if(selectedSentiment && !searchQuery && !selectedCategory && !selectedSource){
        url = `http://localhost:8080/api/news/search-by-sentiment?sentiment=${selectedSentiment}&language=${language}`;
        console.log('Making search query API call to:', url);
      }
        else {
        // If multiple filters are selected, use combined search
        const params = new URLSearchParams();
        params.append('language', language);
        
        if (searchQuery) {
          params.append('query', searchQuery);
        }
        if (selectedCategory) {
          params.append('category', selectedCategory);
        }
        if (selectedSource) {
          params.append('source', selectedSource);
        }
        if(selectedSentiment){
          params.append('sentiment',selectedSentiment);
        }

        url = `http://localhost:8080/api/news/search-combined?${params.toString()}`;
        console.log('Making combined search API call to:', url);
      }

      const response = await axios.get(url);
      setArticles(response.data);
    } catch (error) {
      setError('Failed to fetch articles. Please try again.');
    } finally {
      setLoading(false);
    }
  }, [searchQuery, selectedCategory, selectedSource, language,selectedSentiment]);

 
  useEffect(() => {
    fetchArticles();
  }, [fetchArticles]);

  
  const handleSearchUpdate = useCallback((query) => {
    setSearchQuery(query);
    
  }, []);

  const handleCategoryUpdate = useCallback((category) => {
    setSelectedCategory(category);
    
  }, []);

  const handleSourceUpdate = useCallback((source) => {
    setSelectedSource(source);
    
  }, []);

  const handleSentimentUpdate = useCallback((sentiment) => {
    setSelectedSentiment(sentiment);
  },[]);

  
  const clearAllFilters = useCallback(() => {
    setSearchQuery('');
    setSelectedCategory('');
    setSelectedSource('');
    setSelectedSentiment('');
    setArticles([]);
    setError(null);
  }, []);

  return (
    <>
    <Navbar/>
    <div className='mt-8'>
      <Category 
        selectedCategory={selectedCategory}
        onCategoryChange={handleCategoryUpdate} 
      />
    </div>
    <div className='mt-15'>
      <SearchBar 
        searchQuery={searchQuery}
        onSearchChange={handleSearchUpdate} 
      />
    </div>
    <div className='mt-15'>
      <Filters 
        selectedSource={selectedSource}
        onSourceChange={handleSourceUpdate} 
        selectedSentiment={selectedSentiment}
        onSentimentChange={handleSentimentUpdate}
      />
    </div>
    
    {/* Articles Display Section */}
    <div className='mt-15'>
      {/* Loading State */}
      {loading && (
        <div className="text-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-2 text-gray-600">Loading articles...</p>
        </div>
      )}

      {/* Error State */}
      {error && (
        <div className="text-center py-8">
          <p className="text-red-600">{error}</p>
          <button 
            onClick={() => setError(null)}
            className="mt-2 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition duration-200"
          >
            Dismiss
          </button>
        </div>
      )}

      {/* Articles Display */}
      {!loading && !error && articles.length > 0 && (
        <div className="flex flex-col items-center">
          {articles.map((article, idx) => (
            <Card key={`${article.url || idx}-${idx}`} article={article} idx={idx} />
          ))}
        </div>
      )}

      {/* No Articles Found */}
      {!loading && !error && articles.length === 0 && (searchQuery || selectedCategory || selectedSource) && (
        <div className="text-center py-8">
          <p className="text-gray-600">No articles found for the selected filters.</p>
        </div>
      )}

      {/* Initial State */}
      {!loading && !error && articles.length === 0 && !searchQuery && !selectedCategory && !selectedSource && (
        <div className="text-center py-8">
          <p className="text-gray-600">Use the search bar, categories, or sources above to find news articles.</p>
        </div>
      )}
    </div>
    </>
  )
}

export default Home
