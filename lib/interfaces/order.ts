import { Product } from "./product";

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