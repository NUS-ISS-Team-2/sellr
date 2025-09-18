import React, { useState, useContext } from "react";
import { UserContext } from "../context/UserContext";
import axios from "axios";

export default function ProductForm({ onProductAdded, initialData, onClose }) {
  const [name, setName] = useState(initialData?.name || "");
  const [description, setDescription] = useState(initialData?.description || "");
  const [price, setPrice] = useState(initialData?.price || "");
  const [imageUrl, setImageUrl] = useState(initialData?.imageUrl || "");
  const [category, setCategory] = useState(initialData?.category || "");
  const [stock, setStock] = useState(initialData?.stock || 0);
  const { userId } = useContext(UserContext);

  const API_URL = "http://localhost:8080/api/products";

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

      // Reset form if creating new product
      if (!initialData?.id) {
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
      <input
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="Name"
        required
        className="w-full p-2 border rounded"
      />
      <textarea
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        placeholder="Description"
        required
        className="w-full p-2 border rounded resize-none"
        rows={3}
      />
      <input
        value={category}
        onChange={(e) => setCategory(e.target.value)}
        placeholder="Category"
        required
        className="w-full p-2 border rounded"
      />
      <input
        type="number"
        value={price}
        onChange={(e) => setPrice(e.target.value)}
        placeholder="Price"
        required
        min={0}
        step={0.01}
        className="w-full p-2 border rounded"
      />
      <input
        type="number"
        value={stock}
        onChange={(e) => setStock(Number(e.target.value))}
        placeholder="Stock"
        required
        min={0}
        className="w-full p-2 border rounded"
      />
      <input
        value={imageUrl}
        onChange={(e) => setImageUrl(e.target.value)}
        placeholder="Image URL"
        required
        className="w-full p-2 border rounded"
      />
      <div className="flex space-x-2">
        <button type="submit" className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700">
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
