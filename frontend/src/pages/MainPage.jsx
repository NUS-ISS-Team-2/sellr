import React, { useEffect, useState } from "react";
import axios from "axios";
import Hero from "../components/Hero";
import ProductCard from "../components/ProductCard";
import Footer from "../components/Footer";
import Header from "../components/Header";
import ProductForm from "../components/ProductForm";

export default function MainPage() {
  const [products, setProducts] = useState([]);
  const [showForm, setShowForm] = useState(false);

  // Fetch products from backend
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/products")
      .then((res) => {
        setProducts(res.data);
      })
      .catch((err) => {
        console.error("Error fetching products:", err);
      });
  }, []);

  // Callback after creating a new product
  const handleProductAdded = (newProduct) => {
    setProducts((prev) => [...prev, newProduct]);
    setShowForm(false);
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />
      <Hero />

      <main className="flex-1 container mx-auto px-6 py-10">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-2xl font-bold">Featured Products</h3>
          <button
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            onClick={() => setShowForm(true)}
          >
            Create Product
          </button>
        </div>

        {/* Modal Form */}
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-20">
            <div className="bg-white p-6 rounded shadow-lg w-full max-w-md relative">
              <button
                className="absolute top-2 right-2 text-gray-600 hover:text-gray-900"
                onClick={() => setShowForm(false)}
              >
                ✕
              </button>
              <ProductForm onProductAdded={handleProductAdded} />
            </div>
          </div>
        )}

        <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 mt-6">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </main>

      <Footer />
    </div>
  );
}
