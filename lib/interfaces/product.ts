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