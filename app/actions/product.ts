"use server";

import { API_BASE_URL } from "@/lib/config";
import { Product } from "@/lib/interfaces/product";
import { revalidatePath } from "next/cache";

// Simple API functions
export const fetchProducts = async (): Promise<Product[]> => {
  const response = await fetch(`${API_BASE_URL}/products`);
  if (!response.ok) {
    throw new Error('Failed to fetch products');
  }
  return response.json();
};

export const fetchProduct = async (id: string): Promise<Product> => {
  const response = await fetch(`${API_BASE_URL}/products/${id}`);
  if (!response.ok) {
    throw new Error('Failed to fetch product');
  }
  return response.json();
};

export const createProduct = async (data: Product) => {
  const response = await fetch(`${API_BASE_URL}/products`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });

  const result = await response.json()

  revalidatePath("/")

  return result
};

export const deleteProduct = async (id: number) => {
  const response = await fetch(`${API_BASE_URL}/products/${id}`, {
    method: 'DELETE',
  });
  
  if (!response.ok) {
    throw new Error('Failed to delete product');
  }
  
  // Handle empty response
  if (response.status === 200 && response.headers.get('content-length') === '0') {
    revalidatePath("/")
    return { success: true };
  }
  
  return response.json();
};