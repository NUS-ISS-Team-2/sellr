import { useEffect, useState, useContext } from "react";
import Header from "../components/Header";
import ProductForm from "../components/ProductForm";
import ConfirmDeleteModal from "../components/ConfirmDeleteModal";
import { UserContext } from "../context/UserContext";
import { API_BASE_URL } from "../config";
const API_URL = `${API_BASE_URL}/products`;

export default function ProductManagementPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null);
  const [viewProduct, setViewProduct] = useState(null);

  const { userId, role } = useContext(UserContext);

  useEffect(() => {
    const fetchProducts = async () => {
      if (!role) return; // wait until role is available

      try {
        setLoading(true);

        let url = `${API_URL}`;
        if (role === "SELLER" && userId) {
          url += `/my-products?sellerId=${userId}`;
        }

        const res = await fetch(url);
        if (!res.ok) throw new Error("Failed to fetch products");

        const data = await res.json();
        setProducts(data);
      } catch (err) {
        console.error("Failed to fetch products:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, [role, userId]);


  const handleProductAdded = (newProduct) => {
    setProducts((prev) => {
      const exists = prev.find((p) => p.id === newProduct.id);
      return exists
        ? prev.map((p) => (p.id === newProduct.id ? newProduct : p))
        : [...prev, newProduct];
    });
    setShowForm(false);
    setEditingProduct(null);
  };

  const handleDeleteProduct = (id) => {
    setConfirmDelete(products.find((p) => p.id === id));
  };

  const confirmDeleteProduct = async () => {
    if (!confirmDelete) return;
    try {
      await fetch(`${API_URL}/${confirmDelete.id}`, { method: "DELETE" });
      setProducts((prev) => prev.filter((p) => p.id !== confirmDelete.id));
    } catch (err) {
      console.error("Failed to delete product:", err);
    } finally {
      setConfirmDelete(null);
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />

      <main className="flex-1 container mx-auto px-6 py-10">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold">Product Management</h1>
          <button
            onClick={() => {
              setEditingProduct(null);
              setShowForm(true);
            }}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Add Product
          </button>
        </div>

        <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr className="text-left text-sm font-semibold text-gray-700">
                  <th className="px-4 py-3">Name</th>
                  <th className="px-4 py-3">Category</th>
                  <th className="px-4 py-3">Price</th>
                  <th className="px-4 py-3">Stock</th>
                  <th className="px-4 py-3">Status</th>
                  <th className="px-4 py-3 text-right">Actions</th>
                </tr>
              </thead>

              <tbody className="divide-y divide-gray-100">
                {loading ? (
                  <tr>
                    <td colSpan="6" className="px-4 py-10 text-center">
                      <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
                    </td>
                  </tr>
                ) : products.length === 0 ? (
                  <tr>
                    <td colSpan="6" className="px-4 py-10 text-center text-gray-500">
                      No products found. Please create one!
                    </td>
                  </tr>
                ) : (
                  products.map((p) => {
                    const isOutOfStock = p.stock <= 0;
                    const isLowStock = !isOutOfStock && p.stock <= 20;

                    return (
                      <tr key={p.id} className="text-sm">
                        <td className="px-4 py-4">{p.name}</td>
                        <td className="px-4 py-4">{p.category}</td>
                        <td className="px-4 py-4">${p.price.toFixed(2)}</td>
                        <td className="px-4 py-4">{p.stock}</td>
                        <td className="px-4 py-4">
                          {isOutOfStock ? (
                            <span className="text-red-600 font-semibold">Out of Stock</span>
                          ) : isLowStock ? (
                            <span className="text-yellow-600 font-semibold">⚠️ Low</span>
                          ) : (
                            <span className="text-green-600 font-semibold">In Stock</span>
                          )}
                        </td>
                        <td className="px-4 py-4 text-right space-x-2">
                          <button
                            onClick={() => setViewProduct(p)}
                            className="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                          >
                            View
                          </button>
                          <button
                            onClick={() => {
                              setEditingProduct(p);
                              setShowForm(true);
                            }}
                            className="px-2 py-1 bg-yellow-400 rounded hover:bg-yellow-500"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => handleDeleteProduct(p.id)}
                            className="px-2 py-1 bg-red-500 text-white rounded hover:bg-red-600"
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Product Form Modal */}
        {showForm && (
          <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
            <div className="bg-white p-6 rounded-xl shadow max-w-lg w-full">
              <ProductForm
                initialData={editingProduct}
                onProductAdded={handleProductAdded}
                onClose={() => {
                  setShowForm(false);
                  setEditingProduct(null);
                }}
              />
            </div>
          </div>
        )}

        {/* Confirm Delete Modal */}
        {confirmDelete && (
          <ConfirmDeleteModal
            isOpen={!!confirmDelete}
            itemName={confirmDelete.name}
            onCancel={() => setConfirmDelete(null)}
            onConfirm={confirmDeleteProduct}
          />
        )}

        {/* View Product Modal */}
        {viewProduct && (
          <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
            <div className="bg-white p-6 rounded-xl shadow max-w-md w-full">
              <h2 className="text-xl font-bold mb-2">{viewProduct.name}</h2>
              <p className="mb-2">{viewProduct.description}</p>
              <p className="mb-2">Category: {viewProduct.category}</p>
              <p className="mb-2">Price: ${viewProduct.price.toFixed(2)}</p>
              <p className="mb-2">Stock: {viewProduct.stock}</p>
              <img
                src={viewProduct.imageUrl || "/placeholder.png"}
                alt={viewProduct.name}
                className="mb-2 w-full h-40 object-cover rounded"
              />
              <button
                onClick={() => setViewProduct(null)}
                className="mt-2 px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
              >
                Close
              </button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
