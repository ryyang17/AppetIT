"use client";

import { Button } from "@/components/ui/button";
import { Home, ShoppingCart } from "lucide-react";
import { cn } from "@/lib/utils";
import Link from "next/link";
import { usePathname, useParams } from "next/navigation";
import { useCart } from "@/contexts/CartContext";
import { getDictionarySync } from "@/app/locales";
import { type Locale } from "@/lib/i18n/config";

interface BottomNavigationItem {
  icon: React.ReactNode;
  label: string;
  href?: string;
  active?: boolean;
  badge?: number;
  onClick?: () => void;
}

interface BottomNavigationProps {
  className?: string;
}

export function BottomNavigation({ 
  className 
}: BottomNavigationProps) {
  const pathname = usePathname();
  const params = useParams();
  const lang = (params.lang as Locale) || 'en';
  const dict = getDictionarySync(lang);
  const { getTotalItems } = useCart();
  const totalCartItems = getTotalItems();
  
  const items: BottomNavigationItem[] = [
    {
      icon: <Home className="h-5 w-5" />,
      label: dict.navigation.home,
      href: `/${lang}`,
      active: pathname === `/${lang}`,
    },
    {
      icon: <ShoppingCart className="h-5 w-5" />,
      label: dict.navigation.cart,
      href: `/${lang}/cart`,
      active: pathname === `/${lang}/cart`,
      badge: totalCartItems > 0 ? totalCartItems : undefined,
    },
  ];
  return (
    <div className={cn(
      "w-full bg-white border-t border-gray-200",
      className
    )}>
      <div className="flex justify-around py-2">
        {items.map((item, index) => {
          const ButtonComponent = (
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
          );

          return item.href ? (
            <Link key={index} href={item.href}>
              {ButtonComponent}
            </Link>
          ) : (
            ButtonComponent
          );
        })}
      </div>
    </div>
  );
}