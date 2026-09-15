import { fetchTranslations } from '@/app/actions/translations';

// In-memory cache voor translations
const translationCache = new Map<string, Record<number, string>>();

/**
 * Fetch all translations for a specific language
 * Returns a map of entity_type -> entity_id -> translation
 * Uses server action to ensure correct API_BASE_URL in production
 */
export async function fetchAllTranslations(language: 'en' | 'nl'): Promise<{
  categories: Record<number, string>;
  products: Record<number, string>;
  productDescriptions: Record<number, string>;
  tags: Record<number, string>;
}> {
  const cacheKey = `all_${language}`;

  try {
    // Use server action instead of direct fetch
    const translationIndex = await fetchTranslations(language);
    console.log(`✅ Fetched all translations for ${language}:`, translationIndex);

    // Organize by entity type
    const result = {
      categories: {} as Record<number, string>,
      products: {} as Record<number, string>,
      productDescriptions: {} as Record<number, string>,
      tags: {} as Record<number, string>,
    };

    // Group translations by entity type from the index
    for (const [entityType, entities] of Object.entries(translationIndex)) {
      for (const [entityIdStr, languages] of Object.entries(entities)) {
        const entityId = Number(entityIdStr);
        const translation = languages[language];
        
        if (!translation) continue; // Skip null translations
        
        if (entityType === 'category') {
          result.categories[entityId] = translation;
        } else if (entityType === 'product') {
          result.products[entityId] = translation;
        } else if (entityType === 'product_description') {
          result.productDescriptions[entityId] = translation;
        } else if (entityType === 'tag') {
          result.tags[entityId] = translation;
        }
      }
    }

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
 * Uses server action to ensure correct API_BASE_URL in production
 */
export async function fetchTranslation(
  entityType: 'category' | 'product' | 'tag',
  entityId: number,
  language: 'en' | 'nl'
): Promise<string | null> {
  try {
    // Use server action instead of direct fetch
    const translationIndex = await fetchTranslations(language);
    const translation = translationIndex[entityType]?.[entityId]?.[language];
    return translation || null;
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
