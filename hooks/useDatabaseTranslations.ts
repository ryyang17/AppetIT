"use client";

import { useState, useEffect } from 'react';
import { type Locale } from '@/lib/i18n/config';
import { fetchAllTranslations } from '@/lib/services/translation-service';

interface Translations {
  categories: Record<number, string>;
  products: Record<number, string>;
  tags: Record<number, string>;
}

/**
 * Hook to fetch and use translations from database
 * Fetches all translations at once for better performance
 */
export function useDatabaseTranslations(locale: Locale) {
  const [translations, setTranslations] = useState<Translations>({
    categories: {},
    products: {},
    tags: {},
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // If English, no need to fetch (use original names)
    if (locale === 'en') {
      setTranslations({ categories: {}, products: {}, tags: {} });
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    fetchAllTranslations(locale)
      .then((data) => {
        console.log('📦 Loaded translations:', data);
        setTranslations(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Failed to load translations:', err);
        setError(err.message);
        setLoading(false);
      });
  }, [locale]);

  /**
   * Get translated category name
   * Falls back to original name if translation not found
   */
  const getCategoryTranslation = (categoryId: number, fallbackName: string): string => {
    if (locale === 'en') return fallbackName;
    return translations.categories[categoryId] || fallbackName;
  };

  /**
   * Get translated product name
   * Falls back to original name if translation not found
   */
  const getProductTranslation = (productId: number, fallbackName: string): string => {
    if (locale === 'en') return fallbackName;
    return translations.products[productId] || fallbackName;
  };

  /**
   * Get translated tag name
   * Falls back to original name if translation not found
   */
  const getTagTranslation = (tagId: number, fallbackName: string): string => {
    if (locale === 'en') return fallbackName;
    return translations.tags[tagId] || fallbackName;
  };

  return {
    translations,
    loading,
    error,
    getCategoryTranslation,
    getProductTranslation,
    getTagTranslation,
  };
}
