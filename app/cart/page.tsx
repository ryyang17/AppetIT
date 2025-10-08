"use client";

import { useState } from "react";
import { BottomNavigation } from "@/components/ui/bottom-navigation";
import { ShoppingCart, ArrowLeft } from "lucide-react";
import Link from "next/link";

export default function CartPage() {
  // Cart state
  const [cartItems, setCartItems] = useState([
    { id: 1, name: "Chicken Wings", price: 8.50, quantity: 2, emoji: "🍗" },
    { id: 2, name: "Grilled Salmon", price: 18.50, quantity: 1, emoji: "🐟" },
    { id: 3, name: "Margherita Pizza", price: 12.00, quantity: 1, emoji: "🍕" }
  ]);

  // Update quantity functions
  const updateQuantity = (id: number, newQuantity: number) => {
    if (newQuantity < 1) return; // Don't allow quantity below 1
    setCartItems(items => 
      items.map(item => 
        item.id === id ? { ...item, quantity: newQuantity } : item
      )
    );
  };

  const incrementQuantity = (id: number) => {
    const item = cartItems.find(item => item.id === id);
    if (item) {
      updateQuantity(id, item.quantity + 1);
    }
  };

  const decrementQuantity = (id: number) => {
    const item = cartItems.find(item => item.id === id);
    if (item && item.quantity > 1) {
      updateQuantity(id, item.quantity - 1);
    }
  };

  // Calculate totals
  const subtotal = cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const deliveryFee = 2.50;
  const serviceFee = 1.50;
  const total = subtotal + deliveryFee + serviceFee;
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
                    <h1 className="text-xl font-semibold text-gray-800">Shopping Cart</h1>
                  </div>
                </div>

                {/* Scrollable Content */}
                <div className="flex-1 overflow-y-auto p-4 space-y-4 pb-20">
                  {/* Cart Items */}
                  <div className="space-y-3">
                    
                    {/* Dynamic Cart Items */}
                    {cartItems.map((item) => (
                      <div key={item.id} className="bg-white rounded-lg p-4 shadow-sm border border-gray-100">
                        <div className="flex items-center space-x-3">
                          {/* Product Image */}
                          <div className="w-16 h-16 bg-gray-100 rounded-lg flex items-center justify-center">
                            <span className="text-2xl">{item.emoji}</span>
                          </div>
                          
                          {/* Product Details */}
                          <div className="flex-1">
                            <h3 className="font-medium text-gray-800">{item.name}</h3>
                            <p className="text-green-600 font-semibold">€{item.price.toFixed(2)}</p>
                          </div>
                          
                          {/* Quantity Controls */}
                          <div className="flex items-center space-x-2">
                            <button 
                              className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600 text-lg font-bold leading-none hover:bg-gray-50"
                              onClick={() => decrementQuantity(item.id)}
                            >
                              −
                            </button>
                            <span className="font-medium text-gray-800 min-w-[2rem] text-center">{item.quantity}</span>
                            <button 
                              className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600 text-lg font-bold leading-none hover:bg-gray-50"
                              onClick={() => incrementQuantity(item.id)}
                            >
                              +
                            </button>
                          </div>
                        </div>
                      </div>
                    ))}



                  </div>

                  {/* Order Summary */}
                  <div className="bg-white rounded-lg p-4 shadow-sm border border-gray-100 mt-6">
                    <h3 className="font-semibold text-gray-800 mb-3">Order Summary</h3>
                    
                    <div className="space-y-2 text-sm">
                      <div className="flex justify-between">
                        <span className="text-gray-600">Subtotal</span>
                        <span className="text-gray-800">€{subtotal.toFixed(2)}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-600">Delivery Fee</span>
                        <span className="text-gray-800">€{deliveryFee.toFixed(2)}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-600">Service Fee</span>
                        <span className="text-gray-800">€{serviceFee.toFixed(2)}</span>
                      </div>
                      <div className="border-t border-gray-200 pt-2 mt-2">
                        <div className="flex justify-between font-semibold text-lg">
                          <span>Total</span>
                          <span className="text-green-600">€{total.toFixed(2)}</span>
                        </div>
                      </div>
                    </div>

                    {/* Checkout Button */}
                    <button className="w-full bg-green-600 text-white py-3 rounded-lg font-medium mt-4 hover:bg-green-700 transition-colors">
                      Proceed to Checkout
                    </button>
                  </div>
                  
                </div>

                {/* Bottom Navigation - Fixed at bottom */}
                <div className="absolute bottom-0 left-0 right-0">
                  <BottomNavigation />
                </div>

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