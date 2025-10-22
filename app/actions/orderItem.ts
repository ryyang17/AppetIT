"use server";

import { API_BASE_URL } from "@/lib/config";
import { OrderItem } from "@/lib/interfaces/order";
import { revalidatePath } from "next/cache";

export const fetchOrderItems = async (): Promise<OrderItem[]> => {
  const response = await fetch(`${API_BASE_URL}/order-items`);
  if (!response.ok) {
    throw new Error('Failed to fetch order items');
  }
  return response.json();
};

export const createOrderItem = async (orderItemData: {
  orderId: number;
  productId: number;
  quantity: number;
  price: number;
  status: string;
}): Promise<OrderItem> => {
  const response = await fetch(`${API_BASE_URL}/order-items`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(orderItemData),
  });
  
  if (!response.ok) {
    throw new Error('Failed to create order item');
  }

  const result = await response.json()

  revalidatePath("/")
  
  return result
};