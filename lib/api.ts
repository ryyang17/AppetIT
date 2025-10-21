// Simple API configuration
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

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

export const fetchCategories = async (): Promise<Category[]> => {
  const response = await fetch(`${API_BASE_URL}/categories`);
  if (!response.ok) {
    throw new Error('Failed to fetch categories');
  }
  return response.json();
};

export const fetchOrders = async (): Promise<Order[]> => {
  const response = await fetch(`${API_BASE_URL}/orders`);
  if (!response.ok) {
    throw new Error('Failed to fetch orders');
  }
  return response.json();
};

export const fetchOrderItems = async (): Promise<OrderItem[]> => {
  const response = await fetch(`${API_BASE_URL}/order-items`);
  if (!response.ok) {
    throw new Error('Failed to fetch order items');
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
  
  return response.json();
};

export const createProduct = async (data: any) => {
  const response = await fetch(`${API_BASE_URL}/products`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return response.json();
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
    return { success: true };
  }
  
  return response.json();
};

// Simple types
export interface Product {
  id: number;
  name: string;
  price: number;
  description: string;
  imageUrl: string;
  available: boolean;
  categoryId: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface Category {
  id: number;
  name: string;
  parentId?: number;
}

export interface Order {
  id: number;
  status: string;
  createdAt: string;
  totalAmount?: number;
}

export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  quantity: number;
  price: number;
  status: string;
  product: Product;
}
