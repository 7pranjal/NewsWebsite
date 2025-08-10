import React, { useState } from 'react'

function Sentiment({ selectedSentiment, onSentimentChange }) {
  const handleChange = (e) => {
    onSentimentChange(e.target.value);
  };

  const handleClear = () => {
    onSentimentChange(''); 
  };

  return (
    <>
      <div className="max-w-md mx-auto mt-6">
        <div className="flex items-center gap-2">
          <select
            id="sentiment" 
            value={selectedSentiment}
            onChange={handleChange}
            className="w-52 border border-gray-600 rounded-xl px-2 py-1.5 text-gray-800 shadow-sm focus:ring-2 focus:ring-black focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <option value="">Select Sentiment</option> 
            <option value="positive">Positive</option>
            <option value="negative">Negative</option>
          </select>

          {selectedSentiment && ( 
            <button
              onClick={handleClear}
              className="px-3 py-1.5 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition duration-200 text-sm"
            >
              Clear
            </button>
          )}
        </div>
      </div>
    </>
  )
}

export default Sentiment