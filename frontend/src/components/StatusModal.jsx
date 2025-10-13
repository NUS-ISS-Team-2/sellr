import React from "react";

export default function StatusModal({ isOpen, onClose, title, message }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/40 z-50">
      <div className="bg-white rounded-2xl shadow-xl p-6 max-w-sm w-full text-center">
        <h2 className="text-xl font-semibold mb-3">{title}</h2>
        <p className="text-gray-700 mb-5">{message}</p>

        <button
          onClick={onClose}
          className="bg-blue-600 text-white px-5 py-2 rounded hover:bg-blue-700 transition"
        >
          OK
        </button>
      </div>
    </div>
  );
}
