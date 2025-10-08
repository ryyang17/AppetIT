"use client";

import { useState } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { BottomNavigation } from "@/components/ui/bottom-navigation";
import { ProductDetailModal } from "@/components/ui/product-detail-modal";
import { useProducts } from "@/hooks/useProducts";
import { Search, Mic, Loader2, ArrowLeft } from "lucide-react";
import Link from "next/link";
import { Product } from "@/lib/api";

export default function SearchPage() {
  const { products, loading, error } = useProducts();
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleProductClick = (product: Product) => {
    setSelectedProduct(product);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedProduct(null);
  };

  const handleAddToCart = (product: Product, quantity: number) => {
    console.log(`Added ${quantity}x ${product.name} to cart`);
    alert(`Added ${quantity}x ${product.name} to cart!`);
  };

  // Filter products based on search query
  const filteredProducts = products.filter(product =>
    product.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    product.description.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900 flex items-center justify-center p-2">
      {/* Phone Frame */}
      <div className="relative">
        {/* Phone Outer Frame */}
        <div className="bg-black rounded-[2rem] sm:rounded-[3rem] p-1 sm:p-2 shadow-2xl">
          {/* Phone Inner Frame */}
          <div className="bg-gray-900 rounded-[1.5rem] sm:rounded-[2.5rem] p-0.5 sm:p-1">
            {/* Phone Screen */}
            <div className="bg-white rounded-[1rem] sm:rounded-[2rem] overflow-hidden w-[280px] h-[560px] sm:w-[320px] sm:h-[640px] relative">
              
              {/* Status Bar */}
              <div className="bg-white px-6 py-2 flex justify-between items-center text-black text-sm font-medium">
                <span>9:41</span>
                <div className="flex items-center space-x-1">
                  <div className="flex space-x-1">
                    <div className="w-1 h-1 bg-black rounded-full"></div>
                    <div className="w-1 h-1 bg-black rounded-full"></div>
                    <div className="w-1 h-1 bg-black rounded-full"></div>
                    <div className="w-1 h-1 bg-gray-300 rounded-full"></div>
                  </div>
                  <div className="w-6 h-3 border border-black rounded-sm">
                    <div className="w-4 h-2 bg-black rounded-sm ml-0.5 mt-0.5"></div>
                  </div>
                </div>
              </div>

              {/* App Content Container */}
              <div className="flex flex-col h-[calc(100%-2rem)] bg-gray-50">
                
                {/* Header */}
                <div className="bg-white p-4 shadow-sm">
                  <div className="flex items-center mb-3">
                    <Link href="/">
                      <ArrowLeft className="h-6 w-6 text-gray-600 mr-3" />
                    </Link>
                    <h1 className="text-xl font-semibold text-gray-800">Search Food</h1>
                  </div>
                  
                  {/* Search Bar */}
                  <div className="relative">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                    <input
                      type="text"
                      placeholder="Search for food, restaurants..."
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      className="w-full pl-10 pr-10 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      autoFocus
                    />
                    <Mic className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                  </div>
                </div>

                {/* Scrollable Content */}
                <div className="flex-1 overflow-y-auto p-4 space-y-6 pb-20">
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

                  {/* Search Results */}
                  {!loading && !error && searchQuery && (
                    <div>
                      <h2 className="text-lg font-semibold text-gray-800 mb-3">
                        Search Results ({filteredProducts.length})
                      </h2>
                      
                      {filteredProducts.length === 0 ? (
                        <div className="text-center py-8 text-gray-500">
                          <Search className="h-12 w-12 mx-auto mb-4 text-gray-300" />
                          <p>No results found for "{searchQuery}"</p>
                          <p className="text-sm">Try different keywords</p>
                        </div>
                      ) : (
                        <div className="grid grid-cols-2 gap-3">
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
                                  <p className="text-gray-600 text-sm">€{product.price.toFixed(2)}</p>
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

                  {/* Initial State */}
                  {!loading && !error && !searchQuery && (
                    <div className="text-center py-16 text-gray-500">
                      <Search className="h-16 w-16 mx-auto mb-4 text-gray-300" />
                      <h3 className="text-lg font-medium mb-2">Search for Food</h3>
                      <p>Start typing to find your favorite dishes</p>
                    </div>
                  )}
                </div>

                {/* Bottom Navigation - Fixed at bottom */}
                <div className="absolute bottom-0 left-0 right-0">
                  <BottomNavigation />
                </div>

                {/* Product Detail Modal */}
                <ProductDetailModal
                  product={selectedProduct}
                  isOpen={isModalOpen}
                  onClose={handleCloseModal}
                  onAddToCart={handleAddToCart}
                />

              </div>
            </div>
          </div>
        </div>

        {/* Home Indicator (iPhone style) */}
        <div className="absolute bottom-2 left-1/2 transform -translate-x-1/2 w-32 h-1 bg-white rounded-full"></div>
      </div>
    </div>
  );
}