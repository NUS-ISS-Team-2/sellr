import React, { useEffect, useState, useContext, useCallback } from "react";
import axios from "axios";
import Hero from "../components/Hero";
import ProductCard from "../components/ProductCard";
import Footer from "../components/Footer";
import Header from "../components/Header";
import ProductForm from "../components/ProductForm";
import ProductView from "../components/ProductView";
import ConfirmDeleteModal from "../components/ConfirmDeleteModal";
import { UserContext } from "../context/UserContext";
import ProductSlider from "../components/ProductSlider";
import { API_BASE_URL } from "../config";
import SellerDashboard from "../components/SellerDashboard";

export default function MainPage() {
  const [products, setProducts] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [viewingProduct, setViewingProduct] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const { role, userId } = useContext(UserContext);

  const fetchProducts = useCallback(async () => {
    try {
      let url = `${API_BASE_URL}/products`;

      if (role === "SELLER") {
        url += `/my-products?sellerId=${userId}`;
      }

      const res = await axios.get(url);
      setProducts(res.data);
    } catch (err) {
      console.error("Error fetching products:", err);
    }
  }, [role, userId]); // <-- dependencies

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  // After create/update
  const handleProductAdded = (newProduct) => {
    setProducts((prev) => {
      const exists = prev.find((p) => p.id === newProduct.id);
      return exists
        ? prev.map((p) => (p.id === newProduct.id ? newProduct : p))
        : [...prev, newProduct];
    });
    setShowForm(false);
    setEditingProduct(null);
    fetchProducts();
  };

  // Delete confirmed
  const confirmDeleteProduct = async () => {
    if (!confirmDelete) return;
    try {
      await axios.delete(`${API_BASE_URL}/products/${confirmDelete.id}`);
      setProducts((prev) => prev.filter((p) => p.id !== confirmDelete.id));
    } catch (err) {
      console.error("Failed to delete product:", err);
    } finally {
      setConfirmDelete(null); // Close modal
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />
      <Hero />

      <main className="flex-1 container mx-auto px-6 py-10">
        {(role === "ADMIN") && (
          <div className="flex justify-between items-center mb-6">
            <h3 className="text-2xl font-bold">All Products</h3>
          </div>
        )}


        {/* Modal: Create/Edit */}
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-20">
            <div className="bg-white p-6 rounded shadow-lg w-full max-w-md relative">
              <button
                className="absolute top-2 right-2 text-gray-600 hover:text-gray-900"
                onClick={() => {
                  setShowForm(false);
                  setEditingProduct(null);
                }}
              >
                ✕
              </button>
              <ProductForm
                onProductAdded={handleProductAdded}
                initialData={editingProduct}
              />
            </div>
          </div>
        )}

        {/* Modal: View Details */}
        {viewingProduct && (
          <ProductView
            product={viewingProduct}
            onClose={() => setViewingProduct(null)}
          />
        )}

        {/* Modal: Confirm Delete */}
        <ConfirmDeleteModal
          isOpen={!!confirmDelete}
          itemName={confirmDelete?.name}
          onCancel={() => setConfirmDelete(null)}
          onConfirm={confirmDeleteProduct}
        />

        {role === "SELLER" || role === "ADMIN" ? (
          <div className="space-y-8 mt-6">
            {/* 🧭 Seller Dashboard summary */}
            <SellerDashboard products={products} />

            {/* 🛍️ Seller's product grid */}
            <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4">
              {products.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  onView={setViewingProduct}
                  onEdit={(p) => {
                    setEditingProduct(p);
                    setShowForm(true);
                  }}
                  onDelete={(id) => {
                    const prod = products.find((p) => p.id === id);
                    setConfirmDelete(prod);
                  }}
                />
              ))}
            </div>
          </div>
        ) : (
          // 🛒 Buyer view
          <ProductSlider products={products} />
        )}


      </main>

      <Footer />
    </div>
  );
}
