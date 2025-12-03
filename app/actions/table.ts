"use server";

import { API_BASE_URL } from "@/lib/config";

export type Table = {
  id: number;
  restaurantId: number;
  tableNumber: number;
  capacity: number | null;
  isActive: boolean;
};

export const fetchTables = async (): Promise<Table[]> => {
  const response = await fetch(`${API_BASE_URL}/tables`, {
    cache: 'no-store',
  });
  if (!response.ok) {
    throw new Error('Failed to fetch tables');
  }
  return response.json();
};

export const createTable = async (tableData: {
  restaurantId: number;
  tableNumber: number;
  capacity?: number;
  isActive?: boolean;
}): Promise<Table> => {
  const response = await fetch(`${API_BASE_URL}/tables`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      restaurantId: tableData.restaurantId,
      tableNumber: tableData.tableNumber,
      capacity: tableData.capacity ?? 4,
      isActive: tableData.isActive ?? true,
    }),
  });
  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Failed to create table: ${response.status} ${response.statusText}. ${errorText}`);
  }
  return response.json();
};

export const fetchFirstActiveTable = async (): Promise<Table | null> => {
  try {
    const tables = await fetchTables();
    
    // First, try to find an active table
    const activeTable = tables.find(table => table.isActive);
    if (activeTable) {
      return activeTable;
    }
    
    // If no active tables but tables exist, try to activate the first one
    if (tables.length > 0) {
      const firstTable = tables[0];
      // Try to update it to active
      try {
        const response = await fetch(`${API_BASE_URL}/tables/${firstTable.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            restaurantId: firstTable.restaurantId,
            tableNumber: firstTable.tableNumber,
            capacity: firstTable.capacity ?? 4,
            isActive: true,
          }),
        });
        if (response.ok) {
          return await response.json();
        }
      } catch (updateError) {
        console.error('Failed to activate table:', updateError);
      }
      
      // If update failed, just return the first table anyway
      // The API will validate and give a proper error if needed
      return firstTable;
    }
    
    // If no tables exist at all, try to create one with a default restaurantId
    // The data seeder should create restaurant with id=1, so we'll try that
    try {
      const newTable = await createTable({
        restaurantId: 1, // Default restaurant ID from seeder
        tableNumber: 1,
        capacity: 4,
        isActive: true,
      });
      return newTable;
    } catch (createError) {
      console.error('Failed to create default table:', createError);
      // If that fails, return null
      return null;
    }
  } catch (error) {
    console.error('Failed to fetch or create active table:', error);
    return null;
  }
};

