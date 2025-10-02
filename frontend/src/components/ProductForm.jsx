import React, { useState, useContext, useEffect } from "react";
import { UserContext } from "../context/UserContext";
import axios from "axios";

export default function ProductForm({ onProductAdded, initialData, onClose }) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [category, setCategory] = useState("");
  const [stock, setStock] = useState(0);
  const { userId } = useContext(UserContext);

  const API_URL = "http://localhost:8080/api/products";

  // When initialData changes (edit vs create), reset form fields accordingly
  useEffect(() => {
    if (initialData) {
      setName(initialData.name || "");
      setDescription(initialData.description || "");
      setPrice(initialData.price || "");
      setImageUrl(initialData.imageUrl || "");
      setCategory(initialData.category || "");
      setStock(initialData.stock || 0);
    } else {
      setName("");
      setDescription("");
      setPrice("");
      setImageUrl("");
      setCategory("");
      setStock(0);
    }
  }, [initialData]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!imageUrl) return alert("Please provide an image URL");
    if (stock < 0) return alert("Stock cannot be negative");

    const productData = {
      name,
      description,
      price: Number(price),
      imageUrl,
      category,
      sellerId: userId,
      stock: Number(stock),
    };

    try {
      let res;
      if (initialData?.id) {
        // Update product
        res = await axios.put(`${API_URL}/${initialData.id}`, productData);
      } else {
        // Create product
        res = await axios.post(API_URL, productData);
      }

      onProductAdded(res.data);

      if (!initialData?.id) {
        // Reset form after creating new product
        setName("");
        setDescription("");
        setPrice("");
        setImageUrl("");
        setStock(0);
        setCategory("");
      }

      onClose?.();
    } catch (err) {
      console.error("Failed to save product:", err);
      alert("Failed to save product. See console for details.");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col space-y-4">
      {/* Title */}
      <h2 className="text-xl font-bold mb-2">
        {initialData?.id ? "Update Product" : "Create Product"}
      </h2>

      {/* Name */}
      <div>
        <label className="block text-sm font-medium mb-1">Product Name</label>
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
          className="w-full p-2 border rounded"
        />
      </div>

      {/* Description */}
      <div>
        <label className="block text-sm font-medium mb-1">Description</label>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          required
          className="w-full p-2 border rounded resize-none"
          rows={3}
        />
      </div>

      {/* Category */}
      <div>
        <label className="block text-sm font-medium mb-1">Category</label>
        <input
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          required
          className="w-full p-2 border rounded"
        />
      </div>

      {/* Price */}
      <div>
        <label className="block text-sm font-medium mb-1">Price ($)</label>
        <input
          type="number"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          required
          min={0}
          step={0.01}
          className="w-full p-2 border rounded"
        />
      </div>

      {/* Stock */}
      <div>
        <label className="block text-sm font-medium mb-1">Stock Quantity</label>
        <input
          type="number"
          value={stock}
          onChange={(e) => setStock(Number(e.target.value))}
          required
          min={0}
          className="w-full p-2 border rounded"
        />
      </div>

      {/* Image URL */}
      <div>
        <label className="block text-sm font-medium mb-1">Image URL</label>
        <input
          value={imageUrl}
          onChange={(e) => setImageUrl(e.target.value)}
          required
          className="w-full p-2 border rounded"
        />
      </div>

      {/* Buttons */}
      <div className="flex space-x-2">
        <button
          type="submit"
          className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
        >
          {initialData?.id ? "Update Product" : "Create Product"}
        </button>
        <button
          type="button"
          onClick={onClose}
          className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
        >
          Cancel
        </button>
      </div>
    </form>
  );
}
