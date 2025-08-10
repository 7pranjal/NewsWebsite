import React, { useEffect, useState } from 'react'
import { useLanguage } from './LanguageContext';
import axios from 'axios';

function Category({ selectedCategory, onCategoryChange }) {
  const { language } = useLanguage();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchCategories = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await axios.get(`http://localhost:8080/api/news/categories?language=${language}`);
        setCategories(response.data);
      } catch (error) {
        console.error("Error fetching categories:", error);
        setError("Failed to load categories");
      } finally {
        setLoading(false);
      }
    };

    fetchCategories();
  }, [language]);

  const handleCategoryClick = (category) => {
    onCategoryChange(category);
  };

  if (loading) {
    return (
      <div className="text-center mt-4">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
        <p className="mt-2 text-gray-600">Loading categories...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center mt-4">
        <p className="text-red-600">{error}</p>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto mt-6">
      {/* Categories Section */}
      <div className="mb-6">
        <div className="flex justify-evenly gap-5 font-serif mt-3.5 italic text-2xl">
          {categories.map((category) => (
            <div
              key={category}
              className={`relative group cursor-pointer transition-colors duration-200 ${
                selectedCategory === category ? 'text-blue-600' : ''
              }`}
              onClick={() => handleCategoryClick(category)}
            >
              <span className="group-hover:underline-animation">{category}</span>
              <span
                className={`absolute left-0 -bottom-0.5 h-0.5 transition-all duration-300 group-hover:w-full ${
                  selectedCategory === category ? 'w-full bg-blue-600' : 'w-0 bg-black'
                }`}
              />
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default Category
