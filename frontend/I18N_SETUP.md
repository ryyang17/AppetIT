# 🌐 i18n Implementation - Complete Guide

## ✅ What Has Been Done

### Files Created:
1. **`lib/i18n/config.ts`** - Locale configuration (en, nl)
2. **`app/locales/en.ts`** - English translations
3. **`app/locales/nl.ts`** - Dutch translations  
4. **`app/locales/index.ts`** - Dictionary helper functions
5. **`middleware.ts`** - Automatic redirect to /en/ if no language
6. **`components/ui/language-switcher.tsx`** - Language toggle button
7. **`app/[lang]/page.tsx`** - Main menu page with i18n

### Files Updated:
- **`components/ui/product-detail-modal.tsx`** - Added i18n support
- **`components/ui/bottom-navigation.tsx`** - Added i18n support

---

## 🚀 How It Works

### URL Structure:
- `/` → Redirects to `/en/` (default language)
- `/en/` → English homepage
- `/nl/` → Dutch homepage
- `/en/cart` → English cart page
- `/nl/cart` → Dutch cart page

### Language Switching:
Click the **🌐 Globe button** in the top right corner to switch between:
- 🇬🇧 EN (English)
- 🇳🇱 NL (Nederlands)

---

## 📝 How to Use i18n in Your Components

### Client Component (with "use client"):
\`\`\`typescript
"use client";

import { useParams } from "next/navigation";
import { getDictionarySync } from "@/app/locales";
import { type Locale } from "@/lib/i18n/config";

export default function MyComponent() {
  const params = useParams();
  const lang = (params.lang as Locale) || 'en';
  const dict = getDictionarySync(lang);
  
  return (
    <div>
      <h1>{dict.menu.title}</h1>
      <p>{dict.common.loading}</p>
    </div>
  );
}
\`\`\`

### Server Component (async):
\`\`\`typescript
import { getDictionary } from '@/app/locales';
import { type Locale } from '@/lib/i18n/config';

export default async function MyPage({
  params,
}: {
  params: Promise<{ lang: Locale }>;
}) {
  const { lang } = await params;
  const dict = await getDictionary(lang);
  
  return <h1>{dict.menu.title}</h1>;
}
\`\`\`

---

## 🔗 Creating Links

Always include the language parameter in your links:

\`\`\`typescript
import { useParams } from "next/navigation";
import Link from "next/link";

const params = useParams();
const lang = params.lang as string;

// Good ✅
<Link href={\`/\${lang}/cart\`}>Cart</Link>
<Link href={\`/\${lang}/orders\`}>Orders</Link>

// Bad ❌
<Link href="/cart">Cart</Link>
\`\`\`

---

## ➕ Adding New Translations

### Step 1: Add to English translations (`app/locales/en.ts`):
\`\`\`typescript
const en = {
  common: {
    // ... existing translations
  },
  myNewSection: {
    title: "My Title",
    description: "My Description",
  },
};
\`\`\`

### Step 2: Add to Dutch translations (`app/locales/nl.ts`):
\`\`\`typescript
const nl = {
  common: {
    // ... existing translations
  },
  myNewSection: {
    title: "Mijn Titel",
    description: "Mijn Beschrijving",
  },
};
\`\`\`

### Step 3: Use in your component:
\`\`\`typescript
<h1>{dict.myNewSection.title}</h1>
<p>{dict.myNewSection.description}</p>
\`\`\`

---

## 🔄 Dynamic Values

Use `{{placeholder}}` syntax for dynamic values:

### In translation file:
\`\`\`typescript
showAll: "Show all ({{count}} hidden)"
\`\`\`

### In component:
\`\`\`typescript
dict.menu.showAll.replace('{{count}}', selectedTags.length.toString())
\`\`\`

---

## 📁 Moving Existing Pages to [lang] Folder

### Example: Move `app/cart/page.tsx` → `app/[lang]/cart/page.tsx`

1. **Create the new folder:**
   \`\`\`
   app/[lang]/cart/
   \`\`\`

2. **Move the file and update imports:**
   \`\`\`typescript
   import { useParams } from "next/navigation";
   import { getDictionarySync } from "@/app/locales";
   import { type Locale } from "@/lib/i18n/config";
   
   const params = useParams();
   const lang = (params.lang as Locale) || 'en';
   const dict = getDictionarySync(lang);
   \`\`\`

3. **Replace hardcoded text with translations:**
   \`\`\`typescript
   // Before:
   <h1>Shopping Cart</h1>
   
   // After:
   <h1>{dict.cart.title}</h1>
   \`\`\`

4. **Update all links:**
   \`\`\`typescript
   // Before:
   <Link href="/checkout">Checkout</Link>
   
   // After:
   <Link href={\`/\${lang}/checkout\`}>Checkout</Link>
   \`\`\`

---

## 🎯 Current Translation Coverage

### ✅ Completed:
- Main menu page (`app/[lang]/page.tsx`)
- Product detail modal
- Bottom navigation
- Loading/error states
- Search functionality
- Allergen filtering

### ⏳ To Do:
- Cart page (`app/cart/page.tsx`)
- Other pages in your app

---

## 🐛 Troubleshooting

### Problem: Page not loading
**Solution:** Make sure you're accessing `/en/` or `/nl/` (with the language prefix)

### Problem: Translations not showing
**Solution:** Check that:
1. You imported `getDictionarySync` 
2. You're using `dict.section.key` correctly
3. The translation exists in both `en.ts` and `nl.ts`

### Problem: Links not working
**Solution:** Always use \`/\${lang}/path\` format for links

---

## 🎉 Testing Your Implementation

1. **Start dev server:**
   \`\`\`bash
   npm run dev
   \`\`\`

2. **Open browser:**
   - Go to `http://localhost:3000`
   - You'll be redirected to `http://localhost:3000/en/`

3. **Test language switching:**
   - Click the 🌐 Globe button in top right
   - Page should switch between English and Dutch
   - URL should change from `/en/` to `/nl/`

4. **Test navigation:**
   - Click on products, cart, etc.
   - All pages should maintain the language in the URL

---

## 📚 Available Translations

### Common:
- `dict.common.loading`
- `dict.common.error`

### Menu:
- `dict.menu.title`
- `dict.menu.search`
- `dict.menu.hideAllergies`
- `dict.menu.showAll`
- `dict.menu.safeMenu`
- `dict.menu.allergensExcluded`
- `dict.menu.searchResults`
- `dict.menu.noResults`
- `dict.menu.comingSoon`
- etc.

### Navigation:
- `dict.navigation.home`
- `dict.navigation.orders`
- `dict.navigation.cart`
- `dict.navigation.profile`

### Product:
- `dict.product.allergens`
- `dict.product.addToCart`
- `dict.product.quantity`
- `dict.product.description`
- `dict.product.available`
- `dict.product.total`

---

## 🌟 Best Practices

1. **Always use translations** - Never hardcode text in components
2. **Keep translations organized** - Group by section (menu, product, etc.)
3. **Use descriptive keys** - `menu.search` not `search1`
4. **Test both languages** - Make sure all translations work
5. **Update both files** - Always add to en.ts AND nl.ts

---

## 📞 Need Help?

If you need to add translations or move more pages to the [lang] structure, just ask!

The implementation follows the same pattern as your groupmate's approach. 🎯
