"use server";

import { Category } from "@/lib/interfaces/category";
import { API_BASE_URL } from "@/lib/config";

export const fetchCategories = async (): Promise<Category[]> => {
  const response = await fetch(`${API_BASE_URL}/categories`);
  if (!response.ok) {
    throw new Error('Failed to fetch categories');
  }
  return response.json();
};