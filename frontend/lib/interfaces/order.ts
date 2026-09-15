import { Product } from "./product";

export interface Table {
  id: number;
  restaurantId: number;
  tableNumber: number;
  capacity?: number;
  isActive: boolean;
}

export interface Order {
  id: number;
  tableId?: number;
  restaurantId?: number;
  staffId?: number;
  status: string;
  createdAt: string;
  totalAmount?: number;
  table?: Table;
}

export interface OrderItem {
  id: number;
  orderId: number;
  productId: number;
  quantity: number;
  price: number;
  status: string;
  comment?: string;
  product?: Product;
}