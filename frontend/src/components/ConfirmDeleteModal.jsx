// ConfirmDeleteModal.jsx
export default function ConfirmDeleteModal({ isOpen, itemName, onCancel, onConfirm }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-30">
      <div className="bg-white p-6 rounded shadow-lg w-full max-w-sm text-center relative">
        <h3 className="text-lg font-bold mb-4">Delete Product</h3>
        <p className="text-gray-700 mb-6">
          Are you sure you want to delete{" "}
          <span className="font-semibold">{itemName}</span>?
        </p>
        <div className="flex justify-center space-x-4">
          <button
            className="px-4 py-2 rounded bg-gray-200 hover:bg-gray-300"
            onClick={onCancel}
          >
            Cancel
          </button>
          <button
            className="px-4 py-2 rounded bg-red-600 hover:bg-red-700 text-white"
            onClick={onConfirm}
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
}
