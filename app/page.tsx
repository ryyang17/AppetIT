"use client";

import { useState, useEffect } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { BottomNavigation } from "@/components/ui/bottom-navigation";
import { ProductDetailModal } from "@/components/ui/product-detail-modal";
import { useProducts } from "@/hooks/useProducts";
import { useCart } from "@/contexts/CartContext";
import { Search, Mic, Loader2 } from "lucide-react";
import { Product, Category, fetchCategories } from "@/lib/api";

export default function Home() {
  const { products, categories, loading, error } = useProducts();
  const { addToCart } = useCart();
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<string>("All");
  const [allCategories, setAllCategories] = useState<Category[]>([]);

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

  // Fetch categories on component mount
  useEffect(() => {
    const loadCategories = async () => {
      try {
        const categoryData = await fetchCategories();
        setAllCategories(categoryData);
      } catch (error) {
        console.error('Failed to fetch categories:', error);
      }
    };
    loadCategories();
  }, []);

  // Function to get all child category IDs for a given category
  const getAllChildCategoryIds = (categoryName: string): number[] => {
    if (categoryName === "All") return [];
    
    const category = allCategories.find(cat => cat.name === categoryName);
    if (!category) return [];

    const childIds: number[] = [category.id];
    
    // Recursive function to find all descendants
    const findChildren = (parentId: number) => {
      const children = allCategories.filter(cat => cat.parentId === parentId);
      children.forEach(child => {
        childIds.push(child.id);
        findChildren(child.id); // Recursively find grandchildren
      });
    };
    
    findChildren(category.id);
    return childIds;
  };

  // Get category options for filter
  const categoryOptions = ["All", ...allCategories.map(cat => cat.name)];

  // Filter products based on search query and selected category
  const filteredProducts = products.filter(product => {
    const matchesSearch = product.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      product.description.toLowerCase().includes(searchQuery.toLowerCase());
    
    if (selectedCategory === "All") {
      return matchesSearch;
    }
    
    // Get all child category IDs for the selected category
    const allowedCategoryIds = getAllChildCategoryIds(selectedCategory);
    const matchesCategory = product.categoryId !== null && allowedCategoryIds.includes(product.categoryId);
    
    return matchesSearch && matchesCategory;
  });

  // Define category mapping with multiple keys per category
  const categoryMapping = [
    { 
      keys: ['Appetizers', 'Bread & Starters', 'Small Plates'], 
      displayName: 'Voorgerecht' 
    },
    { 
      keys: ['Main Courses', 'Meat Dishes', 'Pasta', 'Seafood'], 
      displayName: 'Hoofdgerecht' 
    },
    { 
      keys: ['Desserts', 'Cakes', 'Ice Cream'], 
      displayName: 'Nagerecht' 
    },
    {
      keys: ['Hot Drinks', 'Cold Drinks', 'Beverages', 'Alcoholic'],
      displayName: 'Drinken'
    }
  ];

  // Group products by category - use filtered products if searching/filtering
  const productsToUse = searchQuery || selectedCategory !== "All" ? filteredProducts : products;
  
  const groupedProducts = categoryMapping.map(categoryMap => {
    // Find all categories that match any of the keys
    const matchingCategories = categories.filter(cat => 
      categoryMap.keys.includes(cat.name)
    );
    
    // Get all category IDs from matching categories
    const categoryIds = matchingCategories.map(cat => cat.id);
    
    // Filter products that belong to any of these categories
    const categoryProducts = productsToUse.filter(product => 
      product.categoryId !== null && categoryIds.includes(product.categoryId)
    );
    
    return {
      id: categoryMap.keys.join('-'),
      name: categoryMap.keys.join(' + '),
      displayName: categoryMap.displayName,
      products: categoryProducts
    };
  });

  return (
    <div className="min-h-screen bg-gray-50">
      {/* App Content Container */}
      <div className="flex flex-col min-h-screen bg-gray-50">
        
        {/* Header */}
        <div className="bg-white p-4 sm:p-6 shadow-sm sticky top-0 z-10">
          <h1 className="text-xl sm:text-2xl font-semibold text-gray-800 mb-3">Menu</h1>
          
          {/* Search Bar */}
          <div className="relative max-w-md mb-3">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
            <input
              type="text"
              placeholder="Search for food"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-10 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
            />
            
          </div>

          {/* Category Filter Buttons */}
          <div className="overflow-x-auto scrollbar-hide">
            <div className="flex gap-2 pb-2" style={{ minWidth: 'max-content' }}>
              {categoryOptions.map((categoryName) => (
                <button
                  key={categoryName}
                  onClick={() => setSelectedCategory(categoryName)}
                  className={`px-3 py-1 rounded-full text-sm font-medium transition-colors whitespace-nowrap flex-shrink-0 ${
                    selectedCategory === categoryName
                      ? "bg-green-500 text-white"
                      : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                  }`}
                >
                  {categoryName}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-6 pb-20 max-w-4xl mx-auto w-full">
          {/* Loading State */}
          {loading && (
            <div className="flex justify-center items-center py-8">
              <Loader2 className="h-8 w-8 animate-spin text-gray-500" />
              <span className="ml-2 text-gray-500">Loading menu...</span>
            </div>
          )}

          {/* Error State */}
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
              <p>Error loading menu: {error}</p>
            </div>
          )}

          {/* Search Results or Categories and Products */}
          {!loading && !error && (
            <div className="space-y-8">
              {/* Show search/filter results */}
              {(searchQuery || selectedCategory !== "All") && (
                <div>
                  <h2 className="text-lg font-semibold text-gray-800 mb-3">
                    {searchQuery 
                      ? `Search Results (${filteredProducts.length})` 
                      : `${selectedCategory} (${filteredProducts.length})`
                    }
                  </h2>
                  
                  {filteredProducts.length === 0 ? (
                    <div className="text-center py-8 text-gray-500">
                      <Search className="h-12 w-12 mx-auto mb-4 text-gray-300" />
                      <p>No products found for "{searchQuery || selectedCategory}"</p>
                      <p className="text-sm">Try different keywords</p>
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
                              <img 
                                src={product?.imageUrl}
                                alt={product.name}
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
                              <h3 className="font-medium text-gray-800 line-clamp-1">{product.name}</h3>
                              <p className="text-green-600 font-semibold">€{product.price.toFixed(2)}</p>
                              {product.description && (
                                <p className="text-gray-500 text-xs mt-1 line-clamp-2">{product.description}</p>
                              )}
                            </div>
                          </CardContent>
                        </Card>
                      ))}
                    </div>
                  )}
                </div>
              )}

              {/* Show normal categorized menu when not searching/filtering */}
              {!searchQuery && selectedCategory === "All" && (
                <>
                  {groupedProducts.map((category) => (
                    <div key={category.id}>
                      {/* Category Header */}
                      <h2 className="text-xl sm:text-2xl font-bold text-gray-800 mb-4 px-1">
                        {category.displayName}
                      </h2>
                      
                      {/* Category Description for empty categories */}
                      {category.products.length === 0 ? (
                        <div className="text-center py-8 text-gray-500">
                          <p className="text-sm">Binnenkort beschikbaar</p>
                        </div>
                      ) : (
                        /* Products Grid */
                        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3 sm:gap-4">
                          {category.products.map((product) => (
                            <Card 
                              key={product.id} 
                              className="overflow-hidden cursor-pointer transition-transform hover:scale-105"
                              onClick={() => handleProductClick(product)}
                            >
                              <CardContent className="p-0">
                                <div className="aspect-square bg-gray-100 relative flex items-center justify-center">
                                  <img 
                                    src={product?.imageUrl}
                                    alt={product.name}
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
                                  <h3 className="font-medium text-gray-800 line-clamp-1">{product.name}</h3>
                                  <p className="text-green-600 font-semibold">€{product.price.toFixed(2)}</p>
                                  {product.description && (
                                    <p className="text-gray-500 text-xs mt-1 line-clamp-2">{product.description}</p>
                                  )}
                                </div>
                              </CardContent>
                            </Card>
                          ))}
                        </div>
                      )}
                    </div>
                  ))}
                  
                  {/* No categories message */}
                  {groupedProducts.length === 0 && (
                    <div className="text-center py-16 text-gray-500">
                      <h3 className="text-lg font-medium mb-2">No menu categories available</h3>
                      <p>Check back later for delicious options!</p>
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