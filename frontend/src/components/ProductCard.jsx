import React from "react";


export default function ProductCard({ product, onView, onEdit, onDelete }) {
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
        <p className="text-gray-600">{truncate(product.description, 100)}</p>
        <p className="text-gray-600 font-bold mt-1">
          ${product.price.toLocaleString()}
        </p>

        {/* Action buttons */}
        <div className="mt-4 space-y-2">
          <button
            onClick={() => onView(product)}
            className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800 py-2 rounded-lg transition"
          >
            View Details
          </button>
          <button
            onClick={() => onEdit(product)}
            className="w-full bg-yellow-500 hover:bg-yellow-600 text-white py-2 rounded-lg transition"
          >
            Edit
          </button>
          <button
            onClick={() => onDelete(product.id)}
            className="w-full bg-red-600 hover:bg-red-700 text-white py-2 rounded-lg transition"
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
}
