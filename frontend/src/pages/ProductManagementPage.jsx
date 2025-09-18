import React, { useEffect, useState, useContext } from "react";
import { UserContext } from "../context/UserContext";
import Header from "../components/Header";
import ProductForm from "../components/ProductForm";
import ConfirmDeleteModal from "../components/ConfirmDeleteModal";

export default function ProductManagementPage() {
    const [products, setProducts] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [confirmDelete, setConfirmDelete] = useState(null);
    const [viewProduct, setViewProduct] = useState(null);

    const { role } = useContext(UserContext);

    const API_URL = "http://localhost:8080/api/products";

    // Fetch products from backend
    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            const res = await fetch(API_URL);
            const data = await res.json();
            setProducts(data);
        } catch (err) {
            console.error("Failed to fetch products:", err);
        }
    };

    // Add or update product in state
    const handleProductAdded = async (newProduct) => {
        const exists = products.find((p) => p.id === newProduct.id);
        setProducts((prev) =>
            exists ? prev.map((p) => (p.id === newProduct.id ? newProduct : p)) : [...prev, newProduct]
        );
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
        <>
            <Header />
            <div className="container mx-auto p-6">
                <h1 className="text-2xl font-bold mb-4">Product Management</h1>

                <button
                    onClick={() => {
                        setEditingProduct(null);
                        setShowForm(true);
                    }}
                    className="mb-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                    Add Product
                </button>

                {/* Product Table */}
                <table className="w-full table-auto border-collapse border border-gray-300">
                    <thead className="bg-gray-100">
                        <tr>
                            <th className="border px-4 py-2">Name</th>
                            <th className="border px-4 py-2">Category</th>
                            <th className="border px-4 py-2">Price</th>
                            <th className="border px-4 py-2">Stock</th>
                            <th className="border px-4 py-2">Stock Status</th>
                            <th className="border px-4 py-2">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.length === 0 ? (
                            <tr>
                                <td className="border px-4 py-2 text-center" colSpan="6">
                                    No products created. Please create one!
                                </td>
                            </tr>
                        ) : (
                            products.map((p) => {
                                const isOutOfStock = p.stock <= 0;
                                const isLowStock = !isOutOfStock && p.stock <= 20; // example low stock threshold

                                return (
                                    <tr key={p.id} className="border-t">
                                        <td className="border px-4 py-2">{p.name}</td>
                                        <td className="border px-4 py-2">{p.category}</td>
                                        <td className="border px-4 py-2">${p.price.toFixed(2)}</td>
                                        <td className="border px-4 py-2">{p.stock}</td>
                                        <td className="border px-4 py-2">
                                            {isOutOfStock ? (
                                                <span className="text-red-600 font-semibold">Out of Stock</span>
                                            ) : isLowStock ? (
                                                <span className="text-yellow-600 font-semibold">⚠️ Low</span>
                                            ) : (
                                                ""
                                            )}
                                        </td>
                                        <td className="border px-4 py-2 space-x-2">
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

                {/* Product Form Modal */}
                {showForm && (
                    <div className="mt-6 p-4 border rounded shadow bg-white">
                        <ProductForm
                            initialData={editingProduct}
                            onProductAdded={handleProductAdded}
                            onClose={() => {
                                setShowForm(false);
                                setEditingProduct(null);
                            }}
                        />
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
                        <div className="bg-white p-6 rounded shadow max-w-md w-full">
                            <h2 className="text-xl font-bold mb-2">{viewProduct.name}</h2>
                            <p className="mb-2">{viewProduct.description}</p>
                            <p className="mb-2">Category: {viewProduct.category}</p>
                            <p className="mb-2">Price: ${viewProduct.price.toFixed(2)}</p>
                            <p className="mb-2">Stock: {viewProduct.stock}</p>
                            <img src={viewProduct.imageUrl} alt={viewProduct.name} className="mb-2 w-full h-40 object-cover" />
                            <button
                                onClick={() => setViewProduct(null)}
                                className="mt-2 px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
                            >
                                Close
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
}
