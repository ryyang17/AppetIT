"use client";

import { BottomNavigation } from "@/components/ui/bottom-navigation";
import { ShoppingCart, ArrowLeft, Trash2 } from "lucide-react";
import Link from "next/link";
import { useCart } from "@/contexts/CartContext";
import { useOrders } from "@/hooks/useOrders";
import { createOrder, createOrderItem } from "@/lib/api";
import { useState } from "react";

export default function CartPage() {
  const { cartItems, updateQuantity, removeFromCart, getTotalItems, getSubtotal, clearCart } = useCart();
  const { orders, orderItems, refreshOrders } = useOrders();
  const [viewMode, setViewMode] = useState<'cart' | 'orders'>('cart');
  
  const incrementQuantity = (id: number) => {
    const item = cartItems.find(item => item.product.id === id);
    if (item) {
      updateQuantity(id, item.quantity + 1);
    }
  };

  const decrementQuantity = (id: number) => {
    const item = cartItems.find(item => item.product.id === id);
    if (item && item.quantity > 1) {
      updateQuantity(id, item.quantity - 1);
    }
  };

  const handleRemoveItem = (id: number) => {
    removeFromCart(id);
  };

  const handleProceedToCheckout = async () => {
    try {
      // Create order
      const order = await createOrder({ status: 'pending' });
      
      // Create order items
      for (const cartItem of cartItems) {
        await createOrderItem({
          orderId: order.id,
          productId: cartItem.product.id,
          quantity: cartItem.quantity,
          price: cartItem.product.price,
          status: 'pending'
        });
      }
      
      clearCart();
      await refreshOrders();
      console.log('Order completed successfully');
    } catch (error) {
      console.error('Failed to create order:', error);
    }
  };

  // Calculate totals
  const subtotal = getSubtotal();
  const deliveryFee = 2.50;
  const serviceFee = 1.50;
  const total = subtotal + deliveryFee + serviceFee;

  // Group orderItems by orderId
  const groupedOrderItems = orderItems.reduce((acc, item) => {
    if (!acc[item.orderId]) {
      acc[item.orderId] = [];
    }
    acc[item.orderId].push(item);
    return acc;
  }, {} as Record<number, typeof orderItems>);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* App Content Container */}
      <div className="flex flex-col min-h-screen bg-gray-50">
        
        {/* Header */}
        <div className="bg-white p-4 sm:p-6 shadow-sm sticky top-0 z-10">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center">
              <Link href="/">
                <ArrowLeft className="h-6 w-6 text-gray-600 mr-3" />
              </Link>
              <h1 className="text-xl sm:text-2xl font-semibold text-gray-800">
                {viewMode === 'cart' ? 'Shopping Cart' : 'Orders'}
              </h1>
            </div>
          </div>
          
          {/* Toggle Buttons */}
          <div className="flex bg-gray-100 rounded-lg p-1 max-w-sm">
            <button
              className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors ${
                viewMode === 'cart'
                  ? 'bg-white text-gray-900 shadow-sm'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
              onClick={() => setViewMode('cart')}
            >
              🛒 Cart
            </button>
            <button
              className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors ${
                viewMode === 'orders'
                  ? 'bg-white text-gray-900 shadow-sm'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
              onClick={() => setViewMode('orders')}
            >
              📋 Orders
            </button>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-4 pb-20 max-w-4xl mx-auto w-full">
                  
                  {viewMode === 'cart' ? (
                    // Cart View
                    <>
                      {/* Cart Items */}
                      {cartItems.length === 0 ? (
                        // Empty Cart State
                        <div className="text-center py-16 text-gray-500">
                          <ShoppingCart className="h-16 w-16 mx-auto mb-4 text-gray-300" />
                          <h3 className="text-lg font-medium mb-2">Your cart is empty</h3>
                          <p>Add some delicious items to get started!</p>
                          <Link 
                            href="/" 
                            className="mt-4 inline-block bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition-colors"
                          >
                            Browse Menu
                          </Link>
                        </div>
                      ) : (
                        <div className="space-y-3">
                          {/* Dynamic Cart Items */}
                          {cartItems.map((item) => (
                            <div key={item.product.id} className="bg-white rounded-lg p-4 shadow-sm border border-gray-100">
                              <div className="flex items-center space-x-3">
                                {/* Product Image */}
                                <div className="w-16 h-16 bg-gray-100 rounded-lg overflow-hidden flex items-center justify-center">
                                  {item.product.imageUrl ? (
                                    <img 
                                      src={item.product.imageUrl} 
                                      alt={item.product.name}
                                      className="w-full h-full object-cover"
                                      onError={(e) => {
                                        const target = e.target as HTMLImageElement;
                                        target.style.display = 'none';
                                        target.nextElementSibling?.classList.remove('hidden');
                                      }}
                                    />
                                  ) : null}
                                  <div className={`text-2xl ${item.product.imageUrl ? 'hidden' : ''}`}>
                                    🍽
                                  </div>
                                </div>
                                
                                {/* Product Details */}
                                <div className="flex-1">
                                  <h3 className="font-medium text-gray-800">{item.product.name}</h3>
                                  <p className="text-green-600 font-semibold">€{item.product.price.toFixed(2)}</p>
                                </div>
                                
                                {/* Controls Column */}
                                <div className="flex flex-col items-center space-y-2">
                                  {/* Quantity Controls */}
                                  <div className="flex items-center space-x-2">
                                    <button 
                                      className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600 text-lg font-bold leading-none hover:bg-gray-50"
                                      onClick={() => decrementQuantity(item.product.id)}
                                    >
                                      −
                                    </button>
                                    <span className="font-medium text-gray-800 min-w-[2rem] text-center">{item.quantity}</span>
                                    <button 
                                      className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600 text-lg font-bold leading-none hover:bg-gray-50"
                                      onClick={() => incrementQuantity(item.product.id)}
                                    >
                                      +
                                    </button>
                                  </div>
                                  
                                  {/* Delete Button */}
                                  <button
                                    className="w-8 h-8 rounded-full border border-red-300 bg-red-50 flex items-center justify-center text-red-600 hover:bg-red-100 transition-colors"
                                    onClick={() => handleRemoveItem(item.product.id)}
                                    title="Remove item"
                                  >
                                    <Trash2 className="h-4 w-4" />
                                  </button>
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      )}

                      {/* Only show order summary if there are items */}
                      {cartItems.length > 0 && (
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
                          <button 
                            className="w-full bg-green-600 text-white py-3 rounded-lg font-medium mt-4 hover:bg-green-700 transition-colors"
                            onClick={handleProceedToCheckout}
                          >
                            Proceed to Checkout
                          </button>
                        </div>
                      )}
                    </>
                  ) : (
                    // Orders View
                    <div className="space-y-4">
                      {orders.length === 0 ? (
                        <div className="text-center py-16 text-gray-500">
                          <h3 className="text-lg font-medium mb-2">No orders yet</h3>
                          <p>Your order history will appear here</p>
                        </div>
                      ) : (
                        orders
                          .sort((a, b) => b.id - a.id) // Sort newest first
                          .map((order) => {
                            const items = groupedOrderItems[order.id] || [];
                            const orderTotal = items.reduce((sum, item) => {
                              // Use the price from orderItem if available, otherwise 0
                              const itemPrice = item.price || 0;
                              const itemQuantity = item.quantity || 0;
                              return sum + (itemPrice * itemQuantity);
                            }, 0);
                            
                            return (
                              <div key={order.id} className="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
                                {/* Order Header */}
                                <div className="bg-green-50 p-4 border-b border-green-100">
                                  <div className="flex justify-between items-start">
                                    <div>
                                      <h3 className="font-semibold text-gray-800">Order #{order.id}</h3>
                                      <p className="text-sm text-gray-600">
                                        {new Date(order.createdAt).toLocaleDateString('en-US', {
                                          year: 'numeric',
                                          month: 'short',
                                          day: 'numeric',
                                          hour: '2-digit',
                                          minute: '2-digit'
                                        })}
                                      </p>
                                    </div>
                                    <div className="text-right">
                                      <div className="text-lg font-semibold text-green-600">
                                        €{orderTotal.toFixed(2)}
                                      </div>
                                      <div className="text-sm text-gray-600">
                                        {items.length} item{items.length !== 1 ? 's' : ''}
                                      </div>
                                      <span className={`inline-block px-2 py-1 rounded-full text-xs font-medium ${
                                        order.status === 'pending' ? 'bg-yellow-100 text-yellow-800' :
                                        order.status === 'completed' ? 'bg-green-100 text-green-800' :
                                        'bg-gray-100 text-gray-800'
                                      }`}>
                                        {order.status}
                                      </span>
                                    </div>
                                  </div>
                                </div>
                                
                                {/* Order Items */}
                                <div className="p-4">
                                  <div className="space-y-3">
                                    {items.map((orderItem) => (
                                      <div key={orderItem.id} className="flex items-center justify-between py-2">
                                        <div className="flex items-center space-x-3">
                                          <div className="w-12 h-12 bg-gray-100 rounded-lg overflow-hidden flex items-center justify-center">
                                            <span className="text-lg">🍽</span>
                                          </div>
                                          <div>
                                            <h4 className="font-medium text-gray-800">Product ID: {orderItem.productId}</h4>
                                            <p className="text-sm text-gray-600">€{(orderItem.price || 0).toFixed(2)}</p>
                                          </div>
                                        </div>
                                        <div className="text-right">
                                          <div className="font-medium text-gray-800">
                                            Quantity: {orderItem.quantity}
                                          </div>
                                        </div>
                                      </div>
                                    ))}
                                  </div>
                                </div>
                              </div>
                            );
                          })
                      )}
                    </div>
                  )}
                </div>

                {/* Bottom Navigation - Fixed at bottom */}
                <div className="fixed bottom-0 left-0 right-0">
                  <BottomNavigation />
                </div>

              </div>
            </div>
          );
        }