"use server";

import { Tag } from "@/lib/interfaces/tag";
import { API_BASE_URL } from "@/lib/config";

// Simple API functions
export const fetchTags = async (): Promise<Tag[]> => {
  try {
    const url = `${API_BASE_URL}/tags`;
    console.log('Fetching tags from:', url);
    
    const response = await fetch(url, {
      cache: 'no-store', // Ensure fresh data in production
      headers: {
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      console.error('Failed to fetch tags:', response.status, response.statusText);
      throw new Error(`Failed to fetch tags: ${response.status} ${response.statusText}`);
    }
    
    const data = await response.json();
    console.log('Tags fetched successfully:', data.length);
    return data;
  } catch (error) {
    console.error('Error fetching tags:', error);
    throw error;
  }
};

export const createTag = async (data: { name: string; svgIcon: string }) => {
  const response = await fetch(`${API_BASE_URL}/tags`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    throw new Error('Failed to create tag');
  }
  return response.json();
};

export const deleteTag = async (id: number) => {
  const response = await fetch(`${API_BASE_URL}/tags/${id}`, {
    method: 'DELETE',
  });
  
  if (!response.ok) {
    throw new Error('Failed to delete tag');
  }
  
  // Handle empty response
  if (response.status === 200 && response.headers.get('content-length') === '0') {
    return { success: true };
  }
  
  return response.json();
};

