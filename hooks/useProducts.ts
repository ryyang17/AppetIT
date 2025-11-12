"use client";

import { useState, useEffect } from 'react';
import { fetchProducts, createProduct, deleteProduct} from '@/app/actions/product';
import { fetchCategories } from '@/app/actions/category';
import { Product } from '@/lib/interfaces/product';
import { Category } from '@/lib/interfaces/category';

export function useProducts() {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadProducts = async (excludeTagIds?: number[]) => {
    try {
      setLoading(true);
      const [productsData, categoriesData] = await Promise.all([
        fetchProducts(excludeTagIds),
        fetchCategories()
      ]);
      setProducts(productsData);
      setCategories(categoriesData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch products');
    } finally {
      setLoading(false);
    }
  };

  const addProduct = async (data: Product) => {
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

  return { products, categories, loading, error, addProduct, removeProduct, refreshProducts: loadProducts, loadProducts };
}