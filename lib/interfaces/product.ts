import { Tag } from './tag';

export interface Product {
  id: number;
  name: string;
  price: number;
  description: string;
  imageUrl: string;
  available: boolean;
  categoryId: number | null;
  tags?: Tag[];
  createdAt: string;
  updatedAt: string;
}