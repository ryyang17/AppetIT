import { useState, useEffect } from 'react';
import { fetchOrders, fetchOrderItems, Order, OrderItem } from '@/lib/api';

export function useOrders() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [orderItems, setOrderItems] = useState<OrderItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadOrders = async () => {
    try {
      setLoading(true);
      const [ordersData, orderItemsData] = await Promise.all([
        fetchOrders(),
        fetchOrderItems()
      ]);
      setOrders(ordersData);
      setOrderItems(orderItemsData);
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