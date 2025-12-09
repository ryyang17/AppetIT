"use client";

import { BottomNavigation } from "@/components/ui/bottom-navigation";
import { ShoppingCart, ArrowLeft, Trash2 } from "lucide-react";
import Link from "next/link";
import { useParams } from "next/navigation";
import { getDictionarySync } from "@/app/locales";
import { type Locale } from "@/lib/i18n/config";
import { useCart } from "@/contexts/CartContext";
import { useTable } from "@/contexts/TableContext";
import { useOrders } from "@/hooks/useOrders";
import { createOrder } from "@/app/actions/order";
import { createOrderItem } from "@/app/actions/orderItem";
import { useState } from "react";
import { OrderItem } from "@/lib/interfaces/order";
import Image from "next/image";
import { useDatabaseTranslations } from "@/hooks/useDatabaseTranslations";

export default function CartPage() {
  const params = useParams();
  const lang = (params.lang as Locale) || 'en';
  const dict = getDictionarySync(lang);

  const { getProductTranslation } = useDatabaseTranslations(lang);
  
  const { cartItems, updateQuantity, updateComment, removeFromCart, getSubtotal, clearCart } = useCart();
  const { selectedTable } = useTable();
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
      // Use the selected table ID
      const order = await createOrder({ 
        status: 'pending',
        tableId: selectedTable?.id 
      });
      
      for (const cartItem of cartItems) {
        await createOrderItem({
          orderId: order.id,
          productId: cartItem.product.id,
          quantity: cartItem.quantity,
          price: cartItem.product.price,
          status: 'pending',
          comment: cartItem.comment || undefined
        });
      }
      
      clearCart();
      await refreshOrders();
      console.log('Order completed successfully');
    } catch (error) {
      console.error('Failed to create order:', error);
    }
  };

  const subtotal = getSubtotal();
  const total = subtotal;

  const groupedOrderItems = orderItems.reduce((acc, item) => {
    if (!acc[item.orderId]) {
      acc[item.orderId] = [];
    }
    acc[item.orderId].push(item);
    return acc;
  }, {} as Record<number, typeof orderItems>);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="flex flex-col min-h-screen bg-gray-50">
        
        {/* Header */}
        <div className="bg-white p-4 sm:p-6 shadow-sm sticky top-0 z-10">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center">
              <Link href={`/${lang}`}>
                <ArrowLeft className="h-6 w-6 text-gray-600 mr-3" />
              </Link>
              <h1 className="text-xl sm:text-2xl font-semibold text-gray-800">
                {viewMode === 'cart' ? dict.cart.title : dict.cart.ordersTitle}
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
              {dict.cart.cartTab}
            </button>
            <button
              className={`flex-1 py-2 px-4 rounded-md text-sm font-medium transition-colors ${
                viewMode === 'orders'
                  ? 'bg-white text-gray-900 shadow-sm'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
              onClick={() => setViewMode('orders')}
            >
              {dict.cart.ordersTab}
            </button>
          </div>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 space-y-4 pb-20 max-w-4xl mx-auto w-full">
                  
          {viewMode === 'cart' ? (
            // Cart View
            <>
              {cartItems.length === 0 ? (
                <div className="text-center py-16 text-gray-500">
                  <ShoppingCart className="h-16 w-16 mx-auto mb-4 text-gray-300" />
                  <h3 className="text-lg font-medium mb-2">{dict.cart.emptyCart}</h3>
                  <p>{dict.cart.emptyCartDesc}</p>
                  <Link 
                    href={`/${lang}`}
                    className="mt-4 inline-block bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition-colors"
                  >
                    {dict.cart.browseMenu}
                  </Link>
                </div>
              ) : (
                <div className="space-y-3">
                  {cartItems.map((item, index) => (
                    <div key={`cartItem-${index}`} className="bg-white rounded-lg p-4 shadow-sm border border-gray-100">
                      <div className="flex items-center space-x-3">
                        <div className="relative w-16 h-16 bg-gray-100 rounded-lg overflow-hidden flex items-center justify-center">
                          {item.product.imageUrl ? (
                            <Image 
                              src={item.product.imageUrl} 
                              alt={getProductTranslation(item.product.id, item.product.name)} 
                              width={64}
                              height={64}
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
                        
                        <div className="flex-1">
                          <h3 className="font-medium text-gray-800">{getProductTranslation(item.product.id, item.product.name)}</h3>
                          <p className="text-green-600 font-semibold">€{item.product.price.toFixed(2)}</p>
                        </div>
                        
                        <div className="flex flex-col items-center space-y-2">
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
                          
                          <button
                            className="w-8 h-8 rounded-full border border-red-300 bg-red-50 flex items-center justify-center text-red-600 hover:bg-red-100 transition-colors"
                            onClick={() => handleRemoveItem(item.product.id)}
                            title="Remove item"
                          >
                            <Trash2 className="h-4 w-4" />
                          </button>
                        </div>
                      </div>
                      
                      {/* Comment input */}
                      <div className="mt-3">
                        <input
                          type="text"
                          placeholder={dict.cart.commentPlaceholder || "Add a note (e.g., no onions, extra sauce)..."}
                          value={item.comment || ''}
                          onChange={(e) => updateComment(item.product.id, e.target.value)}
                          className="w-full px-3 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
                        />
                      </div>
                    </div>
                  ))}
                </div>
              )}

              {cartItems.length > 0 && (
                <div className="bg-white rounded-lg p-4 shadow-sm border border-gray-100 mt-6">
                  <h3 className="font-semibold text-gray-800 mb-3">{dict.cart.orderSummary}</h3>
                  
                  <div className="space-y-2 text-sm">
                    <div className="flex justify-between">
                      <span className="text-gray-600">{dict.cart.subtotal}</span>
                      <span className="text-gray-800">€{subtotal.toFixed(2)}</span>
                    </div>
                    <div className="border-t border-gray-200 pt-2 mt-2">
                      <div className="flex justify-between font-semibold text-lg">
                        <span>{dict.cart.total}</span>
                        <span className="text-green-600">€{total.toFixed(2)}</span>
                      </div>
                    </div>
                  </div>

                  <button 
                    className="w-full bg-green-600 text-white py-3 rounded-lg font-medium mt-4 hover:bg-green-700 transition-colors"
                    onClick={handleProceedToCheckout}
                  >
                    {dict.cart.proceedToCheckout}
                  </button>
                </div>
              )}
            </>
          ) : (
            // Orders View
            <div className="space-y-4">
              {orders.length === 0 ? (
                <div className="text-center py-16 text-gray-500">
                  <h3 className="text-lg font-medium mb-2">{dict.cart.noOrders}</h3>
                  <p>{dict.cart.noOrdersDesc}</p>
                </div>
              ) : (
                orders
                  .sort((a, b) => b.id - a.id)
                  .map((order, orderIndex) => {
                    const items = groupedOrderItems[order.id] || [];
                    const orderTotal = items.reduce((sum, item) => {
                      const itemPrice = item.price || 0;
                      const itemQuantity = item.quantity || 0;
                      return sum + (itemPrice * itemQuantity);
                    }, 0);
                    
                    return (
                      <div key={`orderIdx-${orderIndex}`} className="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
                        <div className="bg-green-50 p-4 border-b border-green-100">
                          <div className="flex justify-between items-start">
                            <div>
                              <h3 className="font-semibold text-gray-800">
                                {dict.cart.orderNumber.replace('{{number}}', order.id.toString())}
                              </h3>
                              <p className="text-sm text-gray-600">
                                {new Date(order.createdAt).toLocaleDateString(lang === 'nl' ? 'nl-NL' : 'en-US', {
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
                                {items.length > 1 
                                  ? dict.cart.items_plural.replace('{{count}}', items.length.toString())
                                  : dict.cart.items.replace('{{count}}', items.length.toString())
                                }
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
                        
                        <div className="p-4">
                          <div className="space-y-3">
                            {items.map((orderItem, itemIndex) => (
                              <div key={`orderIdx-${orderIndex}-itemIdx-${itemIndex}`} className="py-2">
                                <div className="flex items-center justify-between">
                                  <div className="flex items-center space-x-3">
                                    <div className="relative w-16 h-16 bg-gray-100 rounded-lg overflow-hidden flex items-center justify-center">
                                      {(orderItem as OrderItem).product?.imageUrl ? (
                                        <Image 
                                          src={(orderItem as OrderItem).product?.imageUrl || ''} 
                                          alt={getProductTranslation(orderItem.productId, (orderItem as OrderItem).product?.name || 'Product')}
                                          width={64}
                                          height={64}
                                          className="w-full h-full object-cover"
                                          onError={(e) => {
                                            const target = e.target as HTMLImageElement;
                                            target.style.display = 'none';
                                            target.nextElementSibling?.classList.remove('hidden');
                                          }}
                                        />
                                      ) : null}
                                      <span className={`text-lg ${(orderItem as OrderItem).product?.imageUrl ? 'hidden' : ''}`}>🍽</span>
                                    </div>
                                    <div>
                                      <h4 className="font-medium text-gray-800">
                                        {getProductTranslation(orderItem.productId, (orderItem as OrderItem).product?.name || `Product ID: ${orderItem.productId}`)}
                                      </h4>
                                      <p className="text-sm text-gray-600">€{(orderItem.price || 0).toFixed(2)}</p>
                                    </div>
                                  </div>
                                  <div className="text-right">
                                    <div className="font-medium text-gray-800">
                                      {dict.cart.quantity.replace('{{quantity}}', orderItem.quantity.toString())}
                                    </div>
                                  </div>
                                </div>
                                {(orderItem as OrderItem).comment && (
                                  <div className="mt-2 ml-19 pl-3 border-l-2 border-gray-200">
                                    <p className="text-sm text-gray-500 italic">{(orderItem as OrderItem).comment}</p>
                                  </div>
                                )}
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

        {/* Bottom Navigation */}
        <div className="fixed bottom-0 left-0 right-0">
          <BottomNavigation />
        </div>
      </div>
    </div>
  );
}
