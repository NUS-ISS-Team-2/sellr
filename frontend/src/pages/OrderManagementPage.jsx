import { useState, useContext, useEffect } from "react";
import axios from "axios";
import { UserContext } from "../context/UserContext";
import Header from "../components/Header";

export default function OrderManagementPage() {
  const [orders, setOrders] = useState([]);
  const [viewOrder, setViewOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [deliveryDates, setDeliveryDates] = useState({});

  const API_URL = "http://localhost:8080/api/orders";
  const { userId } = useContext(UserContext);

  useEffect(() => {
    fetchOrders();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const res = await axios.get(`${API_URL}/seller`, {
        params: { sellerId: userId },
      });
      setOrders(res.data);
    } catch (err) {
      console.error("Failed to fetch orders:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsShipped = async (orderId, productId, deliveryDate) => {
    try {
      await axios.put(`${API_URL}/seller/status`, {
        orderId,
        productId,
        sellerId: userId,
        status: "SHIPPED",
        deliveryDate: new Date(deliveryDate).toISOString(),
      });

      // Fetch updated order for this seller only
      const res = await axios.get(`${API_URL}/${orderId}`, {
        params: { sellerId: userId },
      });
      const updatedOrder = res.data;

      // Update orders list
      setOrders((prevOrders) =>
        prevOrders.map((order) =>
          order.orderId === orderId ? updatedOrder : order
        )
      );

      // Update modal if open
      if (viewOrder?.orderId === orderId) {
        setViewOrder(updatedOrder);
      }
    } catch (err) {
      console.error("Failed to update order item status:", err);
    }
  };

  const handleDateChange = (productId, date) => {
    setDeliveryDates((prev) => ({ ...prev, [productId]: date }));
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />

      <main className="flex-1 container mx-auto px-6 py-10">
        <h1 className="text-3xl font-bold mb-8">Order Management</h1>

        <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr className="text-left text-sm font-semibold text-gray-700">
                  <th className="px-4 py-3">Order ID</th>
                  <th className="px-4 py-3">User ID</th>
                  <th className="px-4 py-3">Date</th>
                  <th className="px-4 py-3">Total</th>
                  <th className="px-4 py-3 relative text-left text-sm font-semibold text-gray-700">
                    Status
                    <span className="ml-1 relative group cursor-pointer">
                      ℹ️
                      <span className="absolute left-1/2 -translate-x-1/2 top-full mt-2 w-48 bg-gray-800 text-white text-xs rounded px-2 py-1 opacity-0 group-hover:opacity-100 transition-opacity z-50">
                        Status of the order may still be PENDING if they have orders pending from other sellers.
                      </span>
                    </span>
                  </th>
                  <th className="px-4 py-3">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {loading ? (
                  <tr>
                    <td colSpan={6} className="px-4 py-10 text-center">
                      <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
                    </td>
                  </tr>
                ) : orders.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="px-4 py-10 text-center text-gray-500">
                      No orders found.
                    </td>
                  </tr>
                ) : (
                  orders.map((order) => (
                    <tr key={order.orderId} className="text-sm">
                      <td className="px-4 py-4">{order.orderId}</td>
                      <td className="px-4 py-4">{order.userId}</td>
                      <td className="px-4 py-4">{new Date(order.createdAt).toLocaleString()}</td>
                      <td className="px-4 py-4">${order.orderPrice.toFixed(2)}</td>
                      <td className="px-4 py-4">{order.overallStatus}</td>
                      <td className="px-4 py-4">
                        <button
                          onClick={() => setViewOrder(order)}
                          className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                        >
                          View
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Order Details Modal */}
        {viewOrder && (
          <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
            <div className="bg-white p-6 rounded-xl shadow max-w-2xl w-full">
              <h2 className="text-xl font-bold mb-2">Order {viewOrder.orderId}</h2>
              <p>User: {viewOrder.userId}</p>
              <p>Status: {viewOrder.overallStatus}</p>
              <p>Total: ${viewOrder.orderPrice.toFixed(2)}</p>
              <p>Date: {new Date(viewOrder.createdAt).toLocaleString()}</p>

              <h3 className="mt-4 font-semibold">Items:</h3>
              <ul className="space-y-3">
                {viewOrder.items.map((item) => {
                  const defaultDate = deliveryDates[item.productId] || "";

                  return (
                    <li
                      key={item.productId}
                      className="border p-3 rounded flex flex-col sm:flex-row sm:items-center justify-between space-y-2 sm:space-y-0 sm:space-x-2"
                    >
                      <div className="flex items-center space-x-3">
                        <img
                          src={item.imageUrl || "/placeholder.png"}
                          alt={item.productName}
                          className="w-16 h-16 object-cover rounded-md border border-gray-200"
                        />
                        <div>
                          <p className="font-medium">{item.productName}</p>
                          <p>Qty: {item.quantity}</p>
                          <p>Status: {item.status}</p>
                          {item.deliveryDate ? (
                            <p>
                              Estimated Delivery:{" "}
                              {new Date(item.deliveryDate).toLocaleString(undefined, {
                                year: "numeric",
                                month: "short",
                                day: "numeric",
                                hour: "numeric",
                                minute: "2-digit",
                              })}
                            </p>
                          ) : (
                            <p>Estimated Delivery: —</p>
                          )}
                        </div>
                      </div>

                      {item.status === "PENDING" && (
                        <div className="flex flex-col sm:flex-row sm:items-center space-y-2 sm:space-y-0 sm:space-x-2">
                          <input
                            type="date"
                            value={defaultDate}
                            onChange={(e) => handleDateChange(item.productId, e.target.value)}
                            className="border rounded px-2 py-1"
                          />
                          <button
                            onClick={() =>
                              handleMarkAsShipped(viewOrder.orderId, item.productId, defaultDate)
                            }
                            disabled={!defaultDate}
                            className={`px-3 py-1 rounded text-white ${defaultDate ? "bg-green-500 hover:bg-green-600" : "bg-gray-400 cursor-not-allowed"}`}
                          >
                            Mark as Shipped
                          </button>
                        </div>
                      )}
                    </li>
                  );
                })}
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
      </main>
    </div>
  );
}
