import React, { useState } from 'react'

function SearchBar({ searchQuery, onSearchChange }) {
  const [query, setQuery] = useState(searchQuery || '');

  const handleSearch = () => {
    if (!query.trim()) return;
    onSearchChange(query.trim());
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleClear = () => {
    setQuery('');
    onSearchChange('');
  };

  return (
    <div className="flex justify-center mt-6">
      <input
        type="text"
        value={query}
        onChange={e => setQuery(e.target.value)}
        onKeyPress={handleKeyPress}
        placeholder="Search for news..."
        className="w-[70%] md:w-[50%] border border-gray-700 rounded-full px-5 py-2 text-base shadow-sm focus:outline-none focus:ring-2 focus:ring-black transition duration-200"
      />
      <button 
        className="ml-3 px-4 py-2 bg-black text-white rounded-full hover:bg-gray-800 transition duration-200 disabled:opacity-50 disabled:cursor-not-allowed" 
        onClick={handleSearch}
        disabled={!query.trim()}
      >
        Search
      </button>
      {searchQuery && (
        <button 
          className="ml-2 px-4 py-2 bg-gray-500 text-white rounded-full hover:bg-gray-600 transition duration-200" 
          onClick={handleClear}
        >
          Clear
        </button>
      )}
    </div>
  )
}

export default SearchBar
