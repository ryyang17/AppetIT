import { type TranslationIndex } from "@/app/actions/translations";

/**
 * Haalt de vertaalde waarde op voor een specifieke entiteit + taal,
 * met een fallback naar de originele (niet-vertaalde) waarde.
 */
export function getTranslationLabel(
  index: TranslationIndex | null,
  entityType: string,
  entityId: number,
  language: string,
  fallback: string,
): string {
  if (!index) return fallback;
  return index[entityType]?.[entityId]?.[language] ?? fallback;
}

