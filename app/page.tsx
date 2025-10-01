import Image from "next/image";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { BottomNavigation } from "@/components/ui/bottom-navigation";
import { Search, Mic } from "lucide-react";

export default function Home() {
  const foodItems = [
    {
      id: 1,
      name: "Veg Mo:mo",
      price: "Rs. 250",
      image: "/placeholder-momo.jpg",
      category: "Mo:mo"
    },
    {
      id: 2,
      name: "Mo:mo",
      price: "Rs. 250",
      image: "/placeholder-momo.jpg",
      category: "Mo:mo"
    },
    {
      id: 3,
      name: "Vegi pizza",
      price: "Rs. 250",
      image: "/placeholder-pizza.jpg",
      category: "Pizza"
    },
    {
      id: 4,
      name: "Pepperoni Pizza",
      price: "Rs. 250",
      image: "/placeholder-pizza.jpg",
      category: "Pizza"
    }
  ];

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col max-w-sm mx-auto">
      {/* Header */}
      <div className="bg-white p-4 shadow-sm">
        <h1 className="text-xl font-semibold text-gray-800 mb-3">All view Food</h1>
        
        {/* Search Bar */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <input
            type="text"
            placeholder="Search for food, restaurants..."
            className="w-full pl-10 pr-10 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <Mic className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 p-4 space-y-6 pb-20">
        {/* Mo:mo Section */}
        <div>
          <h2 className="text-lg font-semibold text-gray-800 mb-3">Voorgerechten</h2>
          <div className="grid grid-cols-2 gap-3">
            {foodItems.filter(item => item.category === "voorgerecht").map((item) => (
              <Card key={item.id} className="overflow-hidden">
                <CardContent className="p-0">
                  <div className="aspect-square bg-gray-200 relative">
                    {/* Placeholder for food image */}
                    <div className="w-full h-full bg-gradient-to-br from-orange-200 to-orange-300 flex items-center justify-center">
                      <span className="text-orange-600 font-medium">🥟</span>
                    </div>
                  </div>
                  <div className="p-3">
                    <h3 className="font-medium text-gray-800">{item.name}</h3>
                    <p className="text-gray-600 text-sm">{item.price}</p>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>

        {/* Pizza Section */}
        <div>
          <h2 className="text-lg font-semibold text-gray-800 mb-3">Pizza</h2>
          <div className="grid grid-cols-2 gap-3">
            {foodItems.filter(item => item.category === "Pizza").map((item) => (
              <Card key={item.id} className="overflow-hidden">
                <CardContent className="p-0">
                  <div className="aspect-square bg-gray-200 relative">
                    {/* Placeholder for pizza image */}
                    <div className="w-full h-full bg-gradient-to-br from-red-200 to-yellow-300 flex items-center justify-center">
                      <span className="text-red-600 font-medium text-2xl">🍕</span>
                    </div>
                  </div>
                  <div className="p-3">
                    <h3 className="font-medium text-gray-800">{item.name}</h3>
                    <p className="text-gray-600 text-sm">{item.price}</p>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>

        {/* Burger Section */}
        <div>
          <h2 className="text-lg font-semibold text-gray-800 mb-3">Burger</h2>
          <div className="grid grid-cols-2 gap-3">
            <Card className="overflow-hidden">
              <CardContent className="p-0">
                <div className="aspect-square bg-gray-200 relative">
                  <div className="w-full h-full bg-gradient-to-br from-yellow-200 to-orange-300 flex items-center justify-center">
                    <span className="text-orange-600 font-medium text-2xl">🍔</span>
                  </div>
                </div>
                <div className="p-3">
                  <h3 className="font-medium text-gray-800">Burger</h3>
                  <p className="text-gray-600 text-sm">Rs. 300</p>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      {/* Bottom Navigation */}
      <BottomNavigation />
    </div>
  );
}
