import { Tag } from "@/lib/interfaces/tag";

// Simple API configuration
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// Simple API functions
export const fetchTags = async (): Promise<Tag[]> => {
  const response = await fetch(`${API_BASE_URL}/tags`);
  if (!response.ok) {
    throw new Error('Failed to fetch tags');
  }
  return response.json();
};

export const createTag = async (data: { name: string; svgIcon: string }) => {
  const response = await fetch(`${API_BASE_URL}/tags`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return response.json();
};

export const deleteTag = async (id: number) => {
  const response = await fetch(`${API_BASE_URL}/tags/${id}`, {
    method: 'DELETE',
  });
  
  if (!response.ok) {
    throw new Error('Failed to delete tag');
  }
  
  return response.json();
};

