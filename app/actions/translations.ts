"use server";

import { API_BASE_URL } from "@/lib/config";

export type Translation = {
  translationId: number;
  entityType: string;
  entityId: number;
  language: string;
  translation: string | null;
};

// Index-structuur die het makkelijk maakt om per entiteit en taal op te zoeken.
export type TranslationIndex = {
  [entityType: string]: {
    [entityId: number]: {
      [language: string]: string | null;
    };
  };
};

/**
 * Converteert de platte lijst uit de API naar een genest index-object
 * zodat je eenvoudig kunt doen: index["product"][42]["nl"]
 */
function buildIndex(list: Translation[]): TranslationIndex {
  const index: TranslationIndex = {};

  for (const t of list) {
    if (!index[t.entityType]) {
      index[t.entityType] = {};
    }
    if (!index[t.entityType][t.entityId]) {
      index[t.entityType][t.entityId] = {};
    }
    index[t.entityType][t.entityId][t.language] = t.translation;
  }

  return index;
}

/**
 * Haalt vertalingen op via de API.
 * - Als `language` is opgegeven: GET /translations/language/{language}
 * - Anders: GET /translations (alle talen)
 */
export async function fetchTranslations(language?: string): Promise<TranslationIndex> {
  const url = language
    ? `${API_BASE_URL}/translations/language/${language}`
    : `${API_BASE_URL}/translations`;

  const res = await fetch(url, { cache: "no-store" });

  if (!res.ok) {
    throw new Error(`Failed to fetch translations: ${res.status} ${res.statusText}`);
  }

  const data = (await res.json()) as Translation[];
  return buildIndex(data);
}

/**
 * Upsert één vertaling in de API (POST /translations).
 * Dit wordt gebruikt door de centrale translations-pagina én
 * door CRUD-pagina's waar je inline wilt vertalen.
 */
export async function upsertTranslation(params: {
  entityType: string;
  entityId: number;
  language: string;
  translation: string | null;
}): Promise<Translation> {
  const res = await fetch(`${API_BASE_URL}/translations`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(params),
  });

  if (!res.ok) {
    throw new Error(`Failed to upsert translation: ${res.status} ${res.statusText}`);
  }

  return (await res.json()) as Translation;
}


