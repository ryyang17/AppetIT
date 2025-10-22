"use server";

import { API_BASE_URL } from "@/lib/config";
import { Order } from "@/lib/interfaces/order";
import { revalidatePath } from "next/cache";

export const fetchOrders = async (): Promise<Order[]> => {
  const response = await fetch(`${API_BASE_URL}/orders`);
  if (!response.ok) {
    throw new Error('Failed to fetch orders');
  }
  return response.json();
};

export const createOrder = async (orderData: { status: string }): Promise<Order> => {
  const response = await fetch(`${API_BASE_URL}/orders`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(orderData),
  });
  
  if (!response.ok) {
    throw new Error('Failed to create order');
  }

  const result = <Order>await response.json();

  revalidatePath("/")
  
  return result
};