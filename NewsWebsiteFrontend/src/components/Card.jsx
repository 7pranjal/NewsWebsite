import React from 'react'

function Card({ article, idx }) {
  
  const getSentimentInfo = (sentiment) => {
    if (!sentiment) return null;
    
    const sentimentLower = sentiment.toLowerCase();
    if (sentimentLower.includes('positive')) {
      return { color: 'green', text: 'Positive', bgColor: 'bg-green-100' };
    } else if (sentimentLower.includes('negative')) {
      return { color: 'red', text: 'Negative', bgColor: 'bg-red-100' };
    }
    return null;
  };

 
  const getSourceName = (article) => {
    
    if (article.source?.name) return article.source.name;
    if (article.source?.id) return article.source.id;
    if (typeof article.source === 'string') return article.source;
    if (article.author) return article.author;
    if (article.source) return article.source;
    
    
    if (article.url) {
      try {
        const url = new URL(article.url);
        return url.hostname.replace('www.', '');
      } catch (e) {
        return 'Unknown Source';
      }
    }
    
    return 'Unknown Source';
  };

  const sentimentInfo = getSentimentInfo(article.sentiment);
  const sourceName = getSourceName(article);

  return (
    <div key={idx} className="w-[70%] md:w-[50%] bg-white shadow-md rounded-lg overflow-hidden mb-4 hover:shadow-lg transition-shadow duration-300">
     
      {article.imageUrl && (
        <div className="relative h-48 overflow-hidden">
          <img 
            src={article.imageUrl} 
            alt={article.title}
            className="w-full h-full object-cover"
            onError={(e) => {
              e.target.style.display = 'none';
            }}
          />
          
          {sentimentInfo && (
            <div className={`absolute top-2 right-2 px-2 py-1 rounded-full text-xs font-medium ${sentimentInfo.bgColor} text-${sentimentInfo.color}-800`}>
              {sentimentInfo.text}
            </div>
          )}
        </div>
      )}
      
      
      <div className="p-4">
        <h2 className="font-bold text-lg mb-2 text-gray-800 line-clamp-2">{article.title}</h2>
        <p className="text-gray-700 mb-3 line-clamp-3">{article.description}</p>
        
        
        <div className="flex justify-between items-center text-xs text-gray-500 mb-3">
          <span className="font-medium">{sourceName}</span>
          <span>{article.publishedAt ? new Date(article.publishedAt).toLocaleDateString() : 'Unknown Date'}</span>
        </div>
        
        
        {sentimentInfo && !article.imageUrl && (
          <div className={`inline-block px-3 py-1 rounded-full text-sm font-medium ${sentimentInfo.bgColor} text-${sentimentInfo.color}-800 mb-3`}>
            Sentiment: {sentimentInfo.text}
          </div>
        )}
        
        <a 
          href={article.url} 
          target="_blank" 
          rel="noopener noreferrer" 
          className="text-blue-600 hover:text-blue-800 underline font-medium"
        >
          Read more →
        </a>
      </div>
  </div>
  )
}

export default Card
