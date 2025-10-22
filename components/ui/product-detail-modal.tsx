"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { X, Plus, Minus, ShoppingCart } from "lucide-react";
import { Product } from "@/lib/api";

interface ProductDetailModalProps {
  product: Product | null;
  isOpen: boolean;
  onClose: () => void;
  onAddToCart: (product: Product, quantity: number) => void;
}

export function ProductDetailModal({ 
  product, 
  isOpen, 
  onClose, 
  onAddToCart 
}: ProductDetailModalProps) {
  const [quantity, setQuantity] = useState(1);

  if (!isOpen || !product) return null;

  const handleAddToCart = () => {
    onAddToCart(product, quantity);
    onClose();
    setQuantity(1); // Reset quantity
  };

  const incrementQuantity = () => setQuantity(prev => prev + 1);
  const decrementQuantity = () => setQuantity(prev => prev > 1 ? prev - 1 : 1);

  return (
    <div className="fixed top-18 bottom-13 left-0 right-0 z-40 flex items-center justify-center p-4">
      {/* Background overlay */}
      <div className="absolute top-0 left-0 right-0 bottom-0 bg-black/30 backdrop-blur-sm" onClick={onClose}></div>
      
      <Card className="relative z-50 w-full max-w-md max-h-full bg-white rounded-xl overflow-hidden shadow-2xl flex flex-col border-0">
        <CardContent className="p-0 flex flex-col max-h-full overflow-y-auto">
          {/* Product Image */}
          <div className="relative h-40 sm:h-44 bg-gray-100 flex-shrink-0">
            <Button
              variant="ghost"
              size="icon"
              onClick={onClose}
              className="absolute top-3 right-3 z-10 h-9 w-9 bg-white/90 hover:bg-white rounded-full shadow-md"
            >
              <X className="h-5 w-5" />
            </Button>
            
            <img 
              src={product.imageUrl}
              alt={product.name}
              className="w-full h-full object-cover"
              onError={(e) => {
                const target = e.target as HTMLImageElement;
                target.style.display = 'none';
                target.nextElementSibling?.classList.remove('hidden');
              }}
            />
            <div className="w-full h-full bg-gradient-to-br from-gray-200 to-gray-300 flex items-center justify-center hidden">
              <span className="text-gray-500 font-medium text-4xl">🍽</span>
            </div>
          </div>

          {/* Product Details */}
          <div className="p-4 sm:p-5 flex-1 flex flex-col justify-between min-h-0">
            <div className="mb-3">
              <h2 className="text-lg sm:text-xl font-bold text-gray-900 mb-1">{product.name}</h2>
              <p className="text-xl sm:text-2xl font-bold text-green-600">€{product.price.toFixed(2)}</p>
            </div>

            {product.description && (
              <div className="mb-3">
                <h3 className="text-sm font-semibold text-gray-700 mb-1">Description</h3>
                <p className="text-gray-600 text-sm leading-relaxed">{product.description}</p>
              </div>
            )}

            {/* Availability Status */}
            <div className="mb-3">
              <span className={`inline-block px-2 py-1 rounded-full text-sm font-medium ${
                product.available 
                  ? 'bg-green-100 text-green-800' 
                  : 'bg-red-100 text-red-800'
              }`}>
                {product.available ? 'Available' : 'Not Available'}
              </span>
            </div>

            {/* Quantity Selector */}
            <div className="mb-3">
              <h3 className="text-sm font-semibold text-gray-700 mb-2">Quantity</h3>
              <div className="flex items-center space-x-3">
                <Button
                  variant="outline"
                  size="icon"
                  onClick={decrementQuantity}
                  className="h-9 w-9 rounded-full"
                >
                  <Minus className="h-4 w-4" />
                </Button>
                <span className="text-lg font-semibold min-w-[2.5rem] text-center">{quantity}</span>
                <Button
                  variant="outline"
                  size="icon"
                  onClick={incrementQuantity}
                  className="h-9 w-9 rounded-full"
                >
                  <Plus className="h-4 w-4" />
                </Button>
              </div>
            </div>

            {/* Total Price */}
            <div className="mb-3 p-3 bg-gray-50 rounded-lg">
              <div className="flex justify-between items-center">
                <span className="font-medium text-gray-700">Total:</span>
                <span className="text-lg font-bold text-gray-900">
                  €{(product.price * quantity).toFixed(2)}
                </span>
              </div>
            </div>

            {/* Add to Cart Button */}
            <Button
              onClick={handleAddToCart}
              disabled={!product.available}
              className="w-full bg-green-600 hover:bg-green-700 text-white py-2.5 text-base font-semibold flex-shrink-0"
            >
              <ShoppingCart className="h-5 w-5 mr-2" />
              {product.available ? 'Add to Cart' : 'Out of Stock'}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}