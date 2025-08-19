import React, { useState } from "react";
import axios from "axios";

export default function ProductForm({ onProductAdded, initialData }) {
  const [name, setName] = useState(initialData?.name || "");
  const [description, setDescription] = useState(initialData?.description || "");
  const [price, setPrice] = useState(initialData?.price || "");
  const [imageUrl, setImageUrl] = useState(initialData?.imageUrl || "");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!imageUrl) return alert("Please provide an image URL");

    const productData = {
      name,
      description,
      price: Number(price),
      imageUrl,
    };

    try {
      let res;
      if (initialData?.id) {
        // Update existing product
        res = await axios.put(
          `http://localhost:8080/api/products/${initialData.id}`,
          productData
        );
      } else {
        // Create new product
        res = await axios.post("http://localhost:8080/api/products", productData);
      }

      onProductAdded(res.data);

      // Reset form only if creating new product
      if (!initialData?.id) {
        setName("");
        setDescription("");
        setPrice("");
        setImageUrl("");
      }
    } catch (err) {
      console.error("Failed to save product", err);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col space-y-4">
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
          className="w-full p-2 border rounded"
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          required
          className="w-full p-2 border rounded resize-none"
          rows={3}
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Price ($)</label>
        <input
          type="number"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          required
          className="w-full p-2 border rounded"
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Image URL</label>
        <input
          type="text"
          value={imageUrl}
          onChange={(e) => setImageUrl(e.target.value)}
          required
          className="w-full p-2 border rounded"
        />
      </div>

      <button
        type="submit"
        className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
      >
        {initialData?.id ? "Update Product" : "Create Product"}
      </button>
    </form>
  );
}
