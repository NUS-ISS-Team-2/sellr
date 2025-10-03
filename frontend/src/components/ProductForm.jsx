// src/components/ProductForm.jsx
import React, { useState, useContext, useEffect } from "react";
import { UserContext } from "../context/UserContext";
import axios from "axios";
import { API_BASE_URL } from "../config";

const CLOUD_NAME = "dmtwftous";
const UPLOAD_PRESET = "sellr_upload";

export default function ProductForm({ onProductAdded, initialData, onClose }) {
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [imageUrl, setImageUrl] = useState(""); // Cloudinary URL
  const [category, setCategory] = useState("");
  const [stock, setStock] = useState(0);

  const [categories, setCategories] = useState([]);
  const [filteredCategories, setFilteredCategories] = useState([]);
  const [showDropdown, setShowDropdown] = useState(false);

  const [uploading, setUploading] = useState(false); // for upload spinner

  const { userId } = useContext(UserContext);
  const API_URL = `${API_BASE_URL}/products`;

  // Reset form when editing/creating
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

  // Fetch categories from backend
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const res = await axios.get(`${API_BASE_URL}/products/categories`);
        setCategories(res.data || []);
      } catch (err) {
        console.error("Failed to fetch categories:", err);
      }
    };
    fetchCategories();
  }, []);

  // Filter categories on typing
  useEffect(() => {
    if (category.trim()) {
      setFilteredCategories(
        categories.filter((c) =>
          c.toLowerCase().includes(category.toLowerCase())
        )
      );
    } else {
      setFilteredCategories(categories);
    }
  }, [category, categories]);

  // Upload file to Cloudinary
  const handleFileUpload = async (file) => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", UPLOAD_PRESET);

    setUploading(true);
    try {
      const res = await fetch(
        `https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`,
        {
          method: "POST",
          body: formData,
        }
      );
      const data = await res.json();
      setImageUrl(data.secure_url);
    } catch (err) {
      console.error("Image upload failed:", err);
      alert("Failed to upload image.");
    } finally {
      setUploading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!imageUrl) return alert("Please upload an image first");
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
        res = await axios.put(`${API_URL}/${initialData.id}`, productData);
      } else {
        res = await axios.post(API_URL, productData);
      }

      onProductAdded(res.data);

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
    <form onSubmit={handleSubmit} className="flex flex-col space-y-4 relative">
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

      {/* Category with search dropdown */}
      <div className="relative">
        <label className="block text-sm font-medium mb-1">Category</label>
        <input
          value={category}
          onChange={(e) => {
            setCategory(e.target.value);
            setShowDropdown(true);
          }}
          onFocus={() => setShowDropdown(true)}
          onBlur={() => setTimeout(() => setShowDropdown(false), 150)}
          required
          className="w-full p-2 border rounded"
          placeholder="Type or select a category"
        />
        {showDropdown && filteredCategories.length > 0 && (
          <ul className="absolute z-10 mt-1 w-full bg-white border rounded shadow max-h-40 overflow-y-auto">
            {filteredCategories.map((cat) => (
              <li
                key={cat}
                onMouseDown={() => setCategory(cat)}
                className="px-3 py-2 cursor-pointer hover:bg-blue-100"
              >
                {cat}
              </li>
            ))}
          </ul>
        )}
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

      {/* Image Upload / URL */}
      <div>
        <label className="block text-sm font-medium mb-1">Product Image</label>

        {/* File Upload */}
        <input
          type="file"
          accept="image/*"
          onChange={(e) => handleFileUpload(e.target.files[0])}
          className="w-full p-2 border rounded mb-2"
        />
        {uploading && <p className="text-sm text-gray-500 mt-1">Uploading...</p>}

        {/* Or enter URL */}
        <input
          type="url"
          value={imageUrl}
          onChange={(e) => setImageUrl(e.target.value)}
          placeholder="Or paste an image URL"
          className="w-full p-2 border rounded"
        />

        {/* Preview */}
        {imageUrl && (
          <img
            src={imageUrl}
            alt="Preview"
            className="mt-2 w-32 h-32 object-cover rounded"
          />
        )}
      </div>

      {/* Buttons */}
      <div className="flex space-x-2">
        <button
          type="submit"
          disabled={uploading}
          className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 disabled:opacity-50"
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
