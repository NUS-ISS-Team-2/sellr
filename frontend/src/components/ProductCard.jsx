import React from "react";

export default function ProductCard({ product }) {
  // Truncate long text
  const truncate = (text, maxLength) => {
    if (!text) return "";
    return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
  };

  return (
    <div className="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:shadow-lg transition">
      <img
        src={product.imageUrl}
        alt={product.name}
        className="w-full h-48 object-cover"
      />
      <div className="p-4">
        <h4 className="font-semibold text-lg">{product.name}</h4>
        <p className="text-gray-600">{truncate(product.description, 100)}</p> {/* truncated description */}
        <p className="text-gray-600 font-bold mt-1">${product.price.toLocaleString()}</p>
        <button className="mt-3 w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg transition">
          Add to Cart
        </button>
      </div>
    </div>
  );
}
