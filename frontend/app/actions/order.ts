"use server";

import { API_BASE_URL } from "@/lib/config";
import { Order } from "@/lib/interfaces/order";
import { revalidatePath } from "next/cache";
import { fetchFirstActiveTable } from "./table";

export const fetchOrders = async (): Promise<Order[]> => {
  const response = await fetch(`${API_BASE_URL}/orders`);
  if (!response.ok) {
    throw new Error('Failed to fetch orders');
  }
  return response.json();
};

export const createOrder = async (orderData: { status: string; tableId?: number }): Promise<Order> => {
  // Fetch an available active table if tableId is not provided
  let tableId = orderData.tableId;
  if (!tableId) {
    const activeTable = await fetchFirstActiveTable();
    if (!activeTable) {
      throw new Error('No active tables available. Please contact the restaurant.');
    }
    tableId = activeTable.id;
  }

  const requestData = {
    tableId: tableId,
    status: orderData.status,
    restaurantId: null,
    staffId: null,
    claimedByStaffId: null,
    preparedByStaffId: null,
    totalAmount: null,
  };

  const response = await fetch(`${API_BASE_URL}/orders`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestData),
  });
  
  if (!response.ok) {
    const errorText = await response.text();
    console.error('Order creation failed:', {
      status: response.status,
      statusText: response.statusText,
      body: errorText,
    });
    throw new Error(`Failed to create order: ${response.status} ${response.statusText}. ${errorText}`);
  }

  const result = <Order>await response.json();

  revalidatePath("/")
  
  return result
};