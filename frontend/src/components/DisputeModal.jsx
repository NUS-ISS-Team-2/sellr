import React, { useState } from "react";
import axios from "axios";
import { API_BASE_URL } from "../config";
import StatusModal from "./StatusModal";

const API_URL = `${API_BASE_URL}/orders`;

export default function DisputeModal({ 
  isOpen, 
  onClose, 
  orderId, 
  product, 
  onDisputeRaised 
}) {
  const [reason, setReason] = useState("");
  const [description, setDescription] = useState("");
  
  // ✅ modal state
  const [modalOpen, setModalOpen] = useState(false);
  const [modalTitle, setModalTitle] = useState("");
  const [modalMessage, setModalMessage] = useState("");

  if (!isOpen || !product) return null;

  const openStatusModal = (title, message) => {
    setModalTitle(title);
    setModalMessage(message);
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    if (!reason.trim()) {
      openStatusModal("Error", "Reason is required");
      return;
    }

    try {
      await axios.post(`${API_URL}/dispute`, {
        orderId,
        productId: product.productId,
        reason,
        description,
      });

      const res = await axios.get(`${API_URL}/${orderId}`);
      onDisputeRaised(res.data);
      setReason("");
      setDescription("");
      onClose();
    } catch (error) {
      console.error(error);
      openStatusModal("Error", "Failed to raise dispute. Try again.");
    }
  };

  return (
    <>
      <StatusModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        title={modalTitle}
        message={modalMessage}
      />

      <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
        <div className="bg-white p-6 rounded shadow-lg w-96">
          <h3 className="text-lg font-semibold mb-4">
            Raise Dispute for {product.productName}
          </h3>

          <label className="block mb-2 font-medium">Reason*</label>
          <input
            type="text"
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            className="w-full border px-2 py-1 mb-4 rounded"
            placeholder="e.g., Item damaged"
          />

          <label className="block mb-2 font-medium">Description</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full border px-2 py-1 mb-4 rounded"
            placeholder="Additional details (optional)"
          />

          <div className="flex justify-end gap-2">
            <button
              onClick={onClose}
              className="px-3 py-1 rounded bg-gray-300 hover:bg-gray-400"
            >
              Cancel
            </button>
            <button
              onClick={handleSubmit}
              className="px-3 py-1 rounded bg-red-600 text-white hover:bg-red-700"
            >
              Submit
            </button>
          </div>
        </div>
      </div>
    </>
  );
}
