import React from 'react'
import { useLanguage } from './LanguageContext';

function Navbar() {
  const { language, setLanguage } = useLanguage();
  const toggleLanguage = () => setLanguage(language === 'en' ? 'hi' : 'en');
  return (
    <>
    <nav className=" sticky top-0 z-50 flex items-center justify-between p-4 shadow-md bg-white">
      <h1 className="font-serif font-bold text-3xl font-stretch-ultra-expanded italic text-black-800">EchoBulletin</h1>
      <button className="bg-black rounded-full p-2 text-sm font-medium text-white " onClick={toggleLanguage}>
        {language === 'en' ? 'EN | हिंदी' : 'हिंदी | EN'}
      </button>
    </nav>
    </>
  )
}

export default Navbar
