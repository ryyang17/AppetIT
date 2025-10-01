"use client";

import { useState, useEffect } from 'react';
import { fetchProducts, createProduct, deleteProduct, Product } from '@/lib/api';

export function useProducts() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadProducts = async () => {
    try {
      setLoading(true);
      const data = await fetchProducts();
      setProducts(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch products');
    } finally {
      setLoading(false);
    }
  };

  const addProduct = async (data: any) => {
    const newProduct = await createProduct(data);
    setProducts(prev => [...prev, newProduct]);
  };

  const removeProduct = async (id: number) => {
    await deleteProduct(id);
    setProducts(prev => prev.filter(product => product.id !== id));
  };

  useEffect(() => {
    loadProducts();
  }, []);

  return { products, loading, error, addProduct, removeProduct, refreshProducts: loadProducts };
}