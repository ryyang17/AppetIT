const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// In-memory cache voor translations
const translationCache = new Map<string, Record<number, string>>();

/**
 * Fetch all translations for a specific language
 * Returns a map of entity_type -> entity_id -> translation
 */
export async function fetchAllTranslations(language: 'en' | 'nl'): Promise<{
  categories: Record<number, string>;
  products: Record<number, string>;
  productDescriptions: Record<number, string>;
  tags: Record<number, string>;
}> {
  const cacheKey = `all_${language}`;

  try {
    const response = await fetch(
      `${API_BASE_URL}/translations/language/${language}`,
      { 
        cache: 'no-store',
        headers: {
          'Content-Type': 'application/json',
        }
      }
    );

    if (!response.ok) {
      throw new Error(`Failed to fetch translations: ${response.status}`);
    }

    const data = await response.json();
    console.log(`✅ Fetched all translations for ${language}:`, data);

    // Organize by entity type
    const result = {
      categories: {} as Record<number, string>,
      products: {} as Record<number, string>,
      productDescriptions: {} as Record<number, string>,
      tags: {} as Record<number, string>,
    };

    // Group translations by entity type - API uses camelCase
    data.forEach((item: {
      entityType: string;
      entityId: number;
      translation: string | null;
    }) => {
      if (!item.translation) return; // Skip null translations
      
      if (item.entityType === 'category') {
        result.categories[item.entityId] = item.translation;
      } else if (item.entityType === 'product') {
        result.products[item.entityId] = item.translation;
      } else if (item.entityType === 'product_description') {
        result.productDescriptions[item.entityId] = item.translation;
      } else if (item.entityType === 'tag') {
        result.tags[item.entityId] = item.translation;
      }
    });

    console.log(`📦 Organized translations:`, result);
    return result;
  } catch (error) {
    console.error(`Failed to fetch translations for ${language}:`, error);
    return {
      categories: {},
      products: {},
      productDescriptions: {},
      tags: {},
    };
  }
}

/**
 * Fetch single translation
 */
export async function fetchTranslation(
  entityType: 'category' | 'product' | 'tag',
  entityId: number,
  language: 'en' | 'nl'
): Promise<string | null> {
  try {
    const response = await fetch(
      `${API_BASE_URL}/translations/${entityType}/${entityId}/${language}`
    );

    if (!response.ok) {
      return null;
    }

    const data = await response.json();
    return data.translation;
  } catch (error) {
    console.error('Failed to fetch translation:', error);
    return null;
  }
}

/**
 * Clear translation cache (useful when switching languages)
 */
export function clearTranslationCache(): void {
  translationCache.clear();
  console.log('🗑️ Translation cache cleared');
}
