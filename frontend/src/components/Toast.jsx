// src/components/Toast.jsx
import { useEffect } from "react";

// src/components/Toast.jsx
export default function Toast({ type = "success", message, duration = 5000, onClose }) {
  // Auto-dismiss after duration
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose?.();
    }, duration);
    return () => clearTimeout(timer);
  }, [duration, onClose]);

  const typeStyles = {
    success: "bg-green-100 border border-green-300 text-green-800",
    error: "bg-red-100 border border-red-300 text-red-800",
    warning: "bg-yellow-100 border border-yellow-300 text-yellow-800",
  };

  return (
    <div
      className={`text-center py-2 px-3 rounded-md mx-3 mb-4 ${typeStyles[type] || typeStyles.success}`}
    >
      {message}
    </div>
  );
}
