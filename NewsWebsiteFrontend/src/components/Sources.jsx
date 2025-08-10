import React, { useEffect, useState } from "react";
import { useLanguage } from "./LanguageContext";
import axios from "axios";

function Sources({ selectedSource, onSourceChange }) {
  const { language } = useLanguage();
  const [sources, setSources] = useState([]);
  const [sourcesLoading, setSourcesLoading] = useState(false);
  

  useEffect(() => {
    const fetchSources = async () => {
      setSourcesLoading(true);
      
      try {
        const response = await axios.get(`http://localhost:8080/api/news/sources?language=${language}`);
        setSources(response.data);
      } catch (error) {
        console.error("Error fetching sources:", error);
        
      } finally {
        setSourcesLoading(false);
      }
    };

    fetchSources();
  }, [language]);

  const handleChange = (e) => {
    onSourceChange(e.target.value);
  };

  const handleClear = () => {
    onSourceChange('');
  };

  return (
     <div className="max-w-md mx-auto mt-6">
       {/* {sourcesLoading && (
         <div className="text-center mb-4">
          <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-1 text-sm text-gray-600">Loading sources...</p>
        </div>
      )} */}

      

      <div className="flex items-center gap-2">
        <select
          id="source"
          value={selectedSource}
          onChange={handleChange}
          disabled={sourcesLoading}
          className="w-52 border border-gray-600 rounded-xl px-2 py-1.5 text-gray-800 shadow-sm focus:ring-2 focus:ring-black focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <option value="" disabled>
            {sourcesLoading ? "Loading sources..." : "Select a source"}
          </option>
          {sources.map((source, index) => (
            <option key={index} value={source}>
              {source}
            </option>
          ))}
        </select>

        {selectedSource && (
          <button 
            onClick={handleClear}
            className="px-3 py-1.5 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition duration-200 text-sm"
          >
            Clear
          </button>
        )}
      </div>
    </div>
  );
}

export default Sources;

