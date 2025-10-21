import { useState, useEffect } from 'react';
import { fetchOrders, fetchOrderItems, fetchProducts, Order, OrderItem, Product } from '@/lib/api';

export function useOrders() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [orderItems, setOrderItems] = useState<OrderItem[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadOrders = async () => {
    try {
      setLoading(true);
      const [ordersData, orderItemsData, productsData] = await Promise.all([
        fetchOrders(),
        fetchOrderItems(),
        fetchProducts()
      ]);
      
      // Map product data to order items
      const enrichedOrderItems = orderItemsData.map(orderItem => {
        const product = productsData.find(p => p.id === orderItem.productId);
        return {
          ...orderItem,
          product: product || {
            id: orderItem.productId,
            name: `Product ID: ${orderItem.productId}`,
            price: orderItem.price,
            description: '',
            imageUrl: '',
            available: true,
            categoryId: null,
            createdAt: '',
            updatedAt: ''
          }
        };
      });
      
      setOrders(ordersData);
      setOrderItems(enrichedOrderItems);
      setProducts(productsData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  return {
    orders,
    orderItems,
    products,
    loading,
    error,
    refreshOrders: loadOrders
  };
}