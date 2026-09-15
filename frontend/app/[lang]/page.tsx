"use client";

import { useState, useEffect, useMemo } from "react";
import { useParams } from "next/navigation";
import { getDictionarySync } from "@/app/locales";
import { type Locale } from "@/lib/i18n/config";
import { useDatabaseTranslations } from "@/hooks/useDatabaseTranslations";
import { Card, CardContent } from "@/components/ui/card";
import { BottomNavigation } from "@/components/ui/bottom-navigation";
import { ProductDetailModal } from "@/components/ui/product-detail-modal";
import { LanguageSwitcher } from "@/components/ui/language-switcher";
import { TableSelector } from "@/components/ui/table-selector";
import { useProducts } from "@/hooks/useProducts";
import { useCart } from "@/contexts/CartContext";
import { Loader2, Search } from "lucide-react";
import { Product } from "@/lib/interfaces/product";
import { Tag } from "@/lib/interfaces/tag";
import { fetchTags } from "@/app/actions/tag";
import Image from "next/image";

export default function Home() {
  const params = useParams();
  const lang = (params.lang as Locale) || 'en';
  const dict = getDictionarySync(lang);
  
  // Fetch database translations - ONE hook for all translations
  const {
    getCategoryTranslation,
    getProductTranslation,
    getProductDescriptionTranslation,
    getTagTranslation,
    loading: translationsLoading
  } = useDatabaseTranslations(lang);
  
  const { products, categories, loading, error, loadProducts } = useProducts();
  const { addToCart } = useCart();
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedTags, setSelectedTags] = useState<number[]>([]);
  const [allTags, setAllTags] = useState<Tag[]>([]);

  const handleProductClick = (product: Product) => {
    setSelectedProduct(product);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedProduct(null);
  };

  const handleAddToCart = (product: Product, quantity: number) => {
    addToCart(product, quantity);
    console.log(`Added ${quantity}x ${product.name} to cart`);
  };

  // Fetch tags on component mount
  useEffect(() => {
    const loadTags = async () => {
      try {
        const tagData = await fetchTags();
        setAllTags(tagData);
      } catch (error) {
        console.error('Failed to fetch tags:', error);
      }
    };
    loadTags();
  }, []);

  // Toggle tag selection
  const toggleTag = (tagId: number) => {
    setSelectedTags(prev => {
      const newSelectedTags = prev.includes(tagId) 
        ? prev.filter(id => id !== tagId)
        : [...prev, tagId];
      return newSelectedTags;
    });
  };

  // Load products when tags change
  useEffect(() => {
    if (selectedTags.length > 0) {
      loadProducts(selectedTags);
    } else {
      loadProducts();
    }
  }, [selectedTags]);

  // Clear all selected tags and reload all products
  const clearAllTags = () => {
    setSelectedTags([]);
    loadProducts();
  };

  // Filter products based on search query (tag filtering is done server-side)
  const filteredProducts = products.filter(product => {
    if (!searchQuery) return true;
    
    // Search in translated product name
    const translatedName = getProductTranslation(product.id, product.name);
    
    return translatedName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      product.description.toLowerCase().includes(searchQuery.toLowerCase());
  });

  // Build completely dynamic category hierarchy from database with translations
  const buildCategoryHierarchy = useMemo(() => {
    if (!categories || categories.length === 0) return [];

    const parentCategories = categories.filter(cat => {
      const hasChildren = categories.some(child => child.parentId === cat.id);
      const isNotFoodRoot = cat.name !== 'Food';
      return hasChildren && isNotFoodRoot;
    });

    const sortedParentCategories = parentCategories.sort((a, b) => a.id - b.id);

    return sortedParentCategories.map(parentCat => {
      const childCategories = categories.filter(cat => cat.parentId === parentCat.id);
      
      const getAllDescendantIds = (categoryId: number): number[] => {
        const directChildren = categories.filter(cat => cat.parentId === categoryId);
        const allIds = [categoryId];
        
        directChildren.forEach(child => {
          allIds.push(...getAllDescendantIds(child.id));
        });
        
        return allIds;
      };
      
      const allCategoryIds = getAllDescendantIds(parentCat.id);
      
      return {
        id: parentCat.id,
        name: parentCat.name,
        // Use translated category name
        displayName: getCategoryTranslation(parentCat.id, parentCat.name),
        categoryIds: allCategoryIds,
        childCategories: childCategories.map(child => ({
          id: child.id,
          name: child.name,
          // Use translated category name for children too
          displayName: getCategoryTranslation(child.id, child.name)
        }))
      };
    });
  }, [categories, getCategoryTranslation]);

  // Group products by dynamic category hierarchy
  const productsToUse = searchQuery || selectedTags.length > 0 ? filteredProducts : products;
  
  const groupedProducts = buildCategoryHierarchy.map(categoryGroup => {
    const categoryProducts = productsToUse.filter(product => 
      product.categoryId !== null && categoryGroup.categoryIds.includes(product.categoryId)
    );
    
    return {
      id: categoryGroup.id,
      name: categoryGroup.name,
      displayName: categoryGroup.displayName,
      products: categoryProducts,
      childCategories: categoryGroup.childCategories
    };
  }).filter(group => group.products.length > 0);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="flex flex-col min-h-screen bg-gray-50">
        
        {/* Header */}
        <div className="bg-white p-4 sm:p-6 shadow-sm sticky top-0 z-10">
          <div className="flex justify-between items-center mb-3">
            <h1 className="text-xl sm:text-2xl font-semibold text-gray-800">
              {dict.menu.title}
            </h1>
            <div className="flex items-center gap-3">
              <TableSelector dictionary={{
                selectTable: dict.menu.selectTable,
                table: dict.menu.table,
                noTables: dict.menu.noTables,
                loading: dict.common.loading,
              }} />
              <LanguageSwitcher />
            </div>
          </div>
          
          {/* Search Bar */}
          <div className="relative max-w-md mb-3">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
            <input
              type="text"
              placeholder={dict.menu.search}
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-10 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>

          {/* Tag Filter Section */}
          <div className="mb-3">
            <div className="flex justify-between items-center mb-2">
              <h3 className="text-sm font-medium text-gray-700">
                {dict.menu.hideAllergies}
              </h3>
              {selectedTags.length > 0 && (
                <button
                  onClick={clearAllTags}
                  className="text-xs text-blue-600 hover:text-blue-800"
                >
                  {dict.menu.showAll.replace('{{count}}', selectedTags.length.toString())}
                </button>
              )}
            </div>
            
            {/* Tag Filter Buttons */}
            <div className="overflow-x-auto scrollbar-hide">
              <div className="flex gap-2 pb-2" style={{ minWidth: 'max-content' }}>
                {allTags.map((tag) => (
                  <button
                    key={tag.id}
                    onClick={() => toggleTag(tag.id)}
                    className={`flex items-center gap-1 px-3 py-2 rounded-full text-sm font-medium transition-colors whitespace-nowrap flex-shrink-0 ${
                      selectedTags.includes(tag.id)
                        ? "bg-red-500 text-white border-2 border-red-600"
                        : "bg-gray-100 text-gray-700 hover:bg-gray-200 border-2 border-transparent"
                    }`}
                  >
                    <div 
                      className="w-4 h-4 flex-shrink-0"
                      dangerouslySetInnerHTML={{ 
                        __html: tag.svgIcon.replace('<svg', '<svg width="16" height="16"') 
                      }}
                    />
                    {/* Use translated tag name */}
                    <span>
                      {translationsLoading ? '...' : getTagTranslation(tag.id, tag.name)}
                    </span>
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 pb-20 max-w-4xl mx-auto w-full">
          {/* Loading State */}
          {(loading || translationsLoading) && (
            <div className="flex justify-center items-center py-8">
              <Loader2 className="h-8 w-8 animate-spin text-gray-500" />
              <span className="ml-2 text-gray-500">{dict.common.loading}</span>
            </div>
          )}

          {/* Error State */}
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
              <p>{dict.common.error}: {error}</p>
            </div>
          )}

          {/* Search Results or Categories and Products */}
          {!loading && !error && (
            <div className="space-y-8">
              {/* Show search results only for text search (not tag filtering) */}
              {searchQuery && !selectedTags.length && (
                <div>
                  <h2 className="text-lg font-semibold text-gray-800 mb-3">
                    {dict.menu.searchResults.replace('{{count}}', filteredProducts.length.toString())}
                  </h2>
                  
                  {filteredProducts.length === 0 ? (
                    <div className="text-center py-8 text-gray-500">
                      <Search className="h-12 w-12 mx-auto mb-4 text-gray-300" />
                      <p>{dict.menu.noResults}</p>
                      <p className="text-sm">{dict.menu.tryAdjusting}</p>
                    </div>
                  ) : (
                    <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-4">
                      {filteredProducts.map((product) => (
                        <Card 
                          key={product.id} 
                          className="overflow-hidden cursor-pointer transition-transform hover:scale-105"
                          onClick={() => handleProductClick(product)}
                        >
                          <CardContent className="p-0">
                            <div className="aspect-square bg-gray-100 relative flex items-center justify-center">
                              <Image 
                                src={product?.imageUrl || '/placeholder-food.jpg'}
                                alt={getProductTranslation(product.id, product.name)}
                                width={300}
                                height={300}
                                className="w-full h-full object-cover"
                                onError={(e) => {
                                  const target = e.target as HTMLImageElement;
                                  target.style.display = 'none';
                                  target.nextElementSibling?.classList.remove('hidden');
                                }}
                              />
                              <div className="absolute inset-0 bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center hidden">
                                <span className="text-gray-500 font-medium text-2xl">🍽</span>
                              </div>
                            </div>
                            <div className="p-3">
                              <h3 className="font-medium text-gray-800 line-clamp-1">{getProductTranslation(product.id, product.name)}</h3>
                              <p className="text-green-600 font-semibold">€{product.price.toFixed(2)}</p>
                              {product.description && (
                                <p className="text-gray-500 text-xs mt-1 line-clamp-2">{getProductDescriptionTranslation(product.id, product.description)}</p>
                              )}
                              {product.tags && product.tags.length > 0 && (
                                <div className="flex gap-1 mt-2 flex-wrap">
                                  {product.tags.slice(0, 3).map((tag) => (
                                    <div 
                                      key={tag.id}
                                      className="w-4 h-4 flex-shrink-0"
                                      title={getTagTranslation(tag.id, tag.name)}
                                      dangerouslySetInnerHTML={{ 
                                        __html: tag.svgIcon.replace('<svg', '<svg width="16" height="16"') 
                                      }}
                                    />
                                  ))}
                                  {product.tags.length > 3 && (
                                    <span className="text-xs text-gray-400">+{product.tags.length - 3}</span>
                                  )}
                                </div>
                              )}
                            </div>
                          </CardContent>
                        </Card>
                      ))}
                    </div>
                  )}
                </div>
              )}

              {/* Show categorized menu (always, but filtered when tags are selected) */}
              {(!searchQuery || selectedTags.length > 0) && (
                <>
                  {selectedTags.length > 0 && (
                    <div className="mb-6">
                      <h2 className="text-lg font-semibold text-gray-800 mb-2">
                        {dict.menu.safeMenu.replace('{{count}}', groupedProducts.reduce((total, cat) => total + cat.products.length, 0).toString())}
                        <span className="text-sm font-normal text-gray-600 ml-2">
                          - {(selectedTags.length > 1 ? dict.menu.allergensExcluded_plural : dict.menu.allergensExcluded).replace('{{count}}', selectedTags.length.toString())}
                        </span>
                      </h2>
                    </div>
                  )}

                  {groupedProducts.map((category) => (
                    <div key={category.id}>
                      <h2 className="text-xl sm:text-2xl font-bold text-gray-800 mb-2 px-1">
                        {category.displayName}
                      </h2>
                      
                      {category.childCategories && category.childCategories.length > 0 && (
                        <div className="mb-4 px-1">
                          <p className="text-sm text-gray-600">
                            {dict.menu.includes.replace('{{categories}}', 
                              category.childCategories
                                .map((child: {id: number; name: string; displayName: string}) => child.displayName)
                                .join(', ')
                            )}
                          </p>
                        </div>
                      )}
                      
                      {category.products.length === 0 ? (
                        <div className="text-center py-8 text-gray-500">
                          <p className="text-sm">{dict.menu.comingSoon}</p>
                        </div>
                      ) : (
                        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-4 mb-4">
                          {category.products.map((product: Product) => (
                            <Card 
                              key={product.id} 
                              className="overflow-hidden cursor-pointer transition-transform hover:scale-105"
                              onClick={() => handleProductClick(product)}
                            >
                              <CardContent className="p-0">
                                <div className="aspect-square bg-gray-100 relative flex items-center justify-center">
                                  <Image
                                    src={product?.imageUrl || '/placeholder-food.jpg'}
                                    alt={getProductTranslation(product.id, product.name)}
                                    width={300}
                                    height={300}
                                    className="w-full h-full object-cover"
                                    onError={(e) => {
                                      const target = e.target as HTMLImageElement;
                                      target.style.display = 'none';
                                      target.nextElementSibling?.classList.remove('hidden');
                                    }}
                                  />
                                  <div className="absolute inset-0 bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center hidden">
                                    <span className="text-gray-500 font-medium text-2xl">🍽</span>
                                  </div>
                                </div>
                                <div className="p-3">
                                  <h3 className="font-medium text-gray-800 line-clamp-1">{getProductTranslation(product.id, product.name)}</h3>
                                  <p className="text-green-600 font-semibold">€{product.price.toFixed(2)}</p>
                                  {product.description && (
                                    <p className="text-gray-500 text-xs mt-1 line-clamp-2">{getProductDescriptionTranslation(product.id, product.description)}</p>
                                  )}
                                  {product.tags && product.tags.length > 0 && (
                                    <div className="flex gap-1 mt-2 flex-wrap">
                                      {product.tags.slice(0, 3).map((tag: Tag) => (
                                        <div 
                                          key={tag.id}
                                          className="w-4 h-4 flex-shrink-0"
                                          title={getTagTranslation(tag.id, tag.name)}
                                          dangerouslySetInnerHTML={{ 
                                            __html: tag.svgIcon.replace('<svg', '<svg width="16" height="16"') 
                                          }}
                                        />
                                      ))}
                                      {product.tags.length > 3 && (
                                        <span className="text-xs text-gray-400">+{product.tags.length - 3}</span>
                                      )}
                                    </div>
                                  )}
                                </div>
                              </CardContent>
                            </Card>
                          ))}
                        </div>
                      )}
                    </div>
                  ))}
                  
                  {groupedProducts.length === 0 && (
                    <div className="text-center py-16 text-gray-500">
                      <h3 className="text-lg font-medium mb-2">{dict.menu.noCategories}</h3>
                      <p>{dict.menu.checkBackLater}</p>
                    </div>
                  )}
                </>
              )}
            </div>
          )}
        </div>

        {/* Product Detail Modal */}
        <ProductDetailModal
          product={selectedProduct}
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          onAddToCart={handleAddToCart}
        />

        {/* Bottom Navigation - Fixed at bottom */}
        <div className="fixed bottom-0 left-0 right-0">
          <BottomNavigation />
        </div>
      </div>
    </div>
  );
}
