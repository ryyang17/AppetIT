import { useState, useEffect } from 'react';
import { fetchOrders } from '@/app/actions/order'
import { fetchOrderItems } from '@/app/actions/orderItem';
import { Order, OrderItem } from '@/lib/interfaces/order';

export function useOrders() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [orderItems, setOrderItems] = useState<OrderItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadOrders = async () => {
    try {
      setLoading(true);
      // Backend now returns product data with order items - no need for extra product fetch
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