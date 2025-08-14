import React from "react";
import Header from "../components/Header";
import Hero from "../components/Hero";
import ProductCard from "../components/ProductCard";
import Footer from "../components/Footer";

// Sample products
const products = [
  { id: 1, name: "Sneakers", price: "$59.99", image: "https://via.placeholder.com/300" },
  { id: 2, name: "Backpack", price: "$39.99", image: "https://via.placeholder.com/300" },
  { id: 3, name: "Watch", price: "$129.99", image: "https://via.placeholder.com/300" },
  { id: 4, name: "Sunglasses", price: "$19.99", image: "https://via.placeholder.com/300" },
];

export default function HomePage() {
  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />
      <Hero />
      <main className="flex-1 container mx-auto px-6 py-10">
        <h3 className="text-2xl font-bold mb-6">Featured Products</h3>
        <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </main>
      <Footer />
    </div>
  );
}
