"use client";

import { useState, useEffect } from "react";
import { Order, fetchOrders } from "@/lib/api";

export function useOrders() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadOrders = async () => {
      try {
        setLoading(true);
        const data = await fetchOrders();
        setOrders(data);
        setError(null);
      } catch (err) {
        console.error('Error fetching orders:', err);
        setError(err instanceof Error ? err.message : 'Unknown error occurred');
      } finally {
        setLoading(false);
      }
    };

    loadOrders();
  }, []);

  const refetchOrders = async () => {
    try {
      setError(null);
      const data = await fetchOrders();
      setOrders(data);
    } catch (err) {
      console.error('Error refetching orders:', err);
      setError(err instanceof Error ? err.message : 'Unknown error occurred');
    }
  };

  return { 
    orders, 
    loading, 
    error, 
    refetchOrders 
  };
}