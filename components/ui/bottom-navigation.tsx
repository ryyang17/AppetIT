"use client";

import { Button } from "@/components/ui/button";
import { Search, Home, ShoppingCart } from "lucide-react";
import { cn } from "@/lib/utils";

interface BottomNavigationItem {
  icon: React.ReactNode;
  label: string;
  active?: boolean;
  badge?: number;
  onClick?: () => void;
}

interface BottomNavigationProps {
  items?: BottomNavigationItem[];
  className?: string;
}

const defaultItems: BottomNavigationItem[] = [
  {
    icon: <Home className="h-5 w-5" />,
    label: "Home",
    active: true,
  },
  {
    icon: <Search className="h-5 w-5" />,
    label: "Search",
  },
  {
    icon: <ShoppingCart className="h-5 w-5" />,
    label: "Cart",
    badge: 2,
  },
];

export function BottomNavigation({ 
  items = defaultItems, 
  className 
}: BottomNavigationProps) {
  return (
    <div className={cn(
      "w-full bg-white border-t border-gray-200",
      className
    )}>
      <div className="flex justify-around py-2">
        {items.map((item, index) => (
          <Button
            key={index}
            variant="ghost"
            className={cn(
              "flex flex-col items-center py-2 px-4 relative gap-1",
              item.active 
                ? "text-green-600" 
                : "text-gray-600 hover:text-gray-800"
            )}
            onClick={item.onClick}
          >
            {item.icon}
            <span className="text-xs">{item.label}</span>
            {item.badge && (
              <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                {item.badge}
              </span>
            )}
          </Button>
        ))}
      </div>
    </div>
  );
}