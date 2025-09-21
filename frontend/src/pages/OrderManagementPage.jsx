import { useEffect, useState } from "react";
import axios from "axios";
import Header from "../components/Header";

export default function OrderManagementPage() {
  const [orders, setOrders] = useState([]);
  const [viewOrder, setViewOrder] = useState(null);

  const API_URL = "http://localhost:8080/api/orders"; // base URL

  useEffect(() => {
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    try {
      const sellerId = "68a460b575cab07c0cf94894"; // replace as needed
      const res = await axios.get(`${API_URL}/seller`, {
        params: { sellerId },
      });
      setOrders(res.data);
    } catch (err) {
      console.error("Failed to fetch orders:", err);
    }
  };

  const handleMarkAsShipped = async (orderId, productId) => {
    try {
      const sellerId = "68a460b575cab07c0cf94894"; // replace with actual sellerId
      await axios.put(`${API_URL}/seller/status`, {
        orderId,
        productId,
        sellerId,
        status: "SHIPPED",
      });

      fetchOrders(); // refresh orders
    } catch (err) {
      console.error("Failed to update order item status:", err);
    }
  };

  return (
    <>
      <Header />
      <div className="container mx-auto p-6">
        <h1 className="text-2xl font-bold mb-4">Order Management</h1>

        <table className="w-full table-auto border-collapse border border-gray-300">
          <thead className="bg-gray-100">
            <tr>
              <th className="border px-4 py-2">Order ID</th>
              <th className="border px-4 py-2">User ID</th>
              <th className="border px-4 py-2">Date</th>
              <th className="border px-4 py-2">Total</th>
              <th className="border px-4 py-2">Status</th>
              <th className="border px-4 py-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.length === 0 ? (
              <tr>
                <td colSpan="6" className="border px-4 py-2 text-center">
                  No orders found
                </td>
              </tr>
            ) : (
              orders.map((o) => (
                <tr key={o.orderId} className="border-t">
                  <td className="border px-4 py-2">{o.orderId}</td>
                  <td className="border px-4 py-2">{o.userId}</td>
                  <td className="border px-4 py-2">
                    {new Date(o.createdAt).toLocaleString()}
                  </td>
                  <td className="border px-4 py-2">${o.orderPrice.toFixed(2)}</td>
                  <td className="border px-4 py-2">{o.overallStatus}</td>
                  <td className="border px-4 py-2 flex space-x-2">
                    <button
                      onClick={() => setViewOrder(o)}
                      className="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                    >
                      View
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>

        {/* Order Details Modal */}
        {viewOrder && (
          <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
            <div className="bg-white p-6 rounded shadow max-w-lg w-full">
              <h2 className="text-xl font-bold mb-2">Order {viewOrder.orderId}</h2>
              <p>User: {viewOrder.userId}</p>
              <p>Status: {viewOrder.overallStatus}</p>
              <p>Total: ${viewOrder.orderPrice.toFixed(2)}</p>
              <p>Date: {new Date(viewOrder.createdAt).toLocaleString()}</p>

              <h3 className="mt-4 font-semibold">Items:</h3>
              <ul className="space-y-2">
                {viewOrder.items.map((item) => (
                  <li
                    key={item.productId}
                    className="border p-2 rounded flex items-center justify-between"
                  >
                    <div className="flex items-center space-x-2">
                      <img
                        src={item.imageUrl}
                        alt={item.productName}
                        className="w-16 h-16 object-cover"
                      />
                      <div>
                        <p>{item.productName}</p>
                        <p>Qty: {item.quantity}</p>
                        <p>Status: {item.status}</p>
                        {item.review && <p>Review: {item.review}</p>}
                      </div>
                    </div>

                    {item.status === "PENDING" && (
                      <button
                        onClick={() =>
                          handleMarkAsShipped(viewOrder.orderId, item.productId)
                        }
                        className="px-2 py-1 bg-green-500 text-white rounded hover:bg-green-600"
                      >
                        Mark as Shipped
                      </button>
                    )}
                  </li>
                ))}
              </ul>

              <button
                onClick={() => setViewOrder(null)}
                className="mt-4 px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
              >
                Close
              </button>
            </div>
          </div>
        )}
      </div>
    </>
  );
}
