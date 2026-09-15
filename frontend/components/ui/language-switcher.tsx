"use client";

import { useParams, usePathname, useRouter } from 'next/navigation';
import { Globe } from 'lucide-react';
import { type Locale } from '@/lib/i18n/config';

export function LanguageSwitcher() {
  const params = useParams();
  const pathname = usePathname();
  const router = useRouter();
  
  const currentLang = (params.lang as Locale) || 'en';

  const switchLanguage = () => {
    const newLang: Locale = currentLang === 'en' ? 'nl' : 'en';
    const newPathname = pathname.replace(`/${currentLang}`, `/${newLang}`);
    router.push(newPathname);
  };

  return (
    <button
      onClick={switchLanguage}
      className="flex items-center gap-2 px-3 py-2 rounded-lg bg-gray-100 hover:bg-gray-200 transition-colors"
      aria-label="Switch language"
    >
      <Globe className="h-4 w-4 text-gray-600" />
      <span className="text-sm font-medium text-gray-700 uppercase">
        {currentLang === 'en' ? '🇬🇧 EN' : '🇳🇱 NL'}
      </span>
    </button>
  );
}
