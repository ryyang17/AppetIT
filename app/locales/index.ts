import type { Locale } from '@/lib/i18n/config';
import en from './en';
import nl from './nl';

const dictionaries = {
  en,
  nl,
};

export type Dictionary = typeof en;

// For server components (async)
export const getDictionary = async (locale: Locale): Promise<Dictionary> => {
  return dictionaries[locale];
};

// For client components (synchronous)
export const getDictionarySync = (locale: Locale): Dictionary => {
  return dictionaries[locale];
};
