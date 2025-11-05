import { useState, useEffect } from 'react';
import { fetchOrders } from '@/app/actions/order'
import { fetchOrderItems } from '@/app/actions/orderItem';
import { Order } from '@/lib/interfaces/order';
import { OrderItem } from '@/lib/interfaces/order';
import { Product } from '@/lib/interfaces/product';
import { fetchProducts } from '@/app/actions/product';

export function useOrders() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [orderItems, setOrderItems] = useState<OrderItem[]>([]);
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
      
      // Enrich order items with product data
      const enrichedOrderItems = orderItemsData.map(orderItem => {
        const product = productsData.find(p => p.id === orderItem.productId);
        return {
          ...orderItem,
          product: product || undefined,
          // Use product price if orderItem price is missing or 0
          price: orderItem.price && orderItem.price > 0 ? orderItem.price : product?.price || 0
        } as OrderItem & { product?: Product };
      });
      
      setOrders(ordersData);
      setOrderItems(enrichedOrderItems);
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
    loading,
    error,
    refreshOrders: loadOrders
  };
}