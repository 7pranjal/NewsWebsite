import React from 'react'
import Sources from './Sources'
import Sentiment from './Sentiment'

function Filters({ selectedSource, onSourceChange,selectedSentiment,onSentimentChange }) {
  return (
    <div className='flex gap-0.5 justify-center'>
      <Sources selectedSource={selectedSource} onSourceChange={onSourceChange} />
      <Sentiment selectedSentiment={selectedSentiment} onSentimentChange={onSentimentChange} />
    </div>
  )
}

export default Filters
