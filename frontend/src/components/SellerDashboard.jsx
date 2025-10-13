import { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import { UserContext } from "../context/UserContext";
import { API_BASE_URL } from "../config";

export default function SellerDashboard({ products }) {
  const { userId, role } = useContext(UserContext);
  const [loading, setLoading] = useState(true);
  const [outstandingOrders, setOutstandingOrders] = useState(0);
  const [isTableOpen, setIsTableOpen] = useState(false);
  const [disputedOrders, setDisputedOrders] = useState(0);
  useEffect(() => {
    if (role !== "SELLER" || !userId) return;

    const fetchOutstandingOrders = async () => {
      setLoading(true);
      try {
        const ordersRes = await axios.get(`${API_BASE_URL}/orders/seller`, {
          params: { sellerId: userId },
        });

        const orders = ordersRes.data || [];

        // Count pending shipments
        const outstandingCount = orders.reduce((count, order) => {
          const pendingItems = order.items?.filter(
            (item) => item.sellerId === userId && item.status === "PENDING"
          ).length;
          return count + (pendingItems || 0);
        }, 0);

        // Count disputed items
        const disputedCount = orders.reduce((count, order) => {
          const disputedItems = order.items?.filter(
            (item) => item.sellerId === userId && item.status === "DISPUTING"
          ).length;
          return count + (disputedItems || 0);
        }, 0);

        setOutstandingOrders(outstandingCount);
        setDisputedOrders(disputedCount); // new state
      } catch (err) {
        console.error("Failed to load outstanding orders:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchOutstandingOrders();
  }, [role, userId]);


  if (role !== "SELLER") return null;

  const lowStockProducts = products?.filter((p) => p.stock < 10) || [];
  const lowStockCount = lowStockProducts.length;

  return (
    <div className="bg-white border border-gray-200 rounded-xl shadow-sm p-6">
      <h2 className="text-2xl font-bold mb-6 text-gray-800">Your Summary</h2>

      {loading ? (
        <div className="flex justify-center py-10">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
        </div>
      ) : (
        <div className="grid sm:grid-cols-3 gap-6">
          {/* Outstanding Orders */}
          <Link
            to="/manageorders"
            className="block p-4 border rounded-lg text-center hover:bg-blue-50 transition"
          >
            <h3 className="text-lg font-semibold text-gray-700">
              Outstanding Orders
            </h3>
            <p
              className={`text-3xl font-bold mt-2 ${outstandingOrders > 0 ? "text-red-600" : "text-green-600"
                }`}
            >
              {outstandingOrders}
            </p>
            <p className="text-sm text-gray-500 mt-1">Pending shipments</p>
          </Link>

          {/* Low Stock Products */}
          <Link
            to="/product-management"
            className="block p-4 border rounded-lg text-center hover:bg-yellow-50 transition"
          >
            <h3 className="text-lg font-semibold text-gray-700">
              Low Stock Products
            </h3>
            <p
              className={`text-3xl font-bold mt-2 ${lowStockCount > 0 ? "text-yellow-500" : "text-green-600"
                }`}
            >
              {lowStockCount}
            </p>
            <p className="text-sm text-gray-500 mt-1">Stock below 10 units</p>
          </Link>
          <Link
            to="/disputes"
            className="block p-4 border rounded-lg text-center hover:bg-red-50 transition"
          >
            <h3 className="text-lg font-semibold text-gray-700">
              Disputed Orders
            </h3>
            <p
              className={`text-3xl font-bold mt-2 ${disputedOrders > 0 ? "text-red-600" : "text-green-600"
                }`}
            >
              {disputedOrders}
            </p>
            <p className="text-sm text-gray-500 mt-1">Orders currently in dispute</p>
          </Link>
        </div>
      )}

      {/* Low Stock Details (Collapsible) */}
      {lowStockProducts.length > 0 && (
        <div className="mt-8">
          <button
            onClick={() => setIsTableOpen(!isTableOpen)}
            className="flex items-center justify-between w-full text-left"
          >
            <h3 className="text-lg font-semibold text-gray-800">
              Products Low in Stock
            </h3>
            <span className="text-gray-600 text-lg">
              {isTableOpen ? "▲" : "▼"}
            </span>
          </button>

          {isTableOpen && (
            <div className="overflow-x-auto border rounded-lg mt-3 transition-all duration-300">
              <table className="min-w-full text-left text-sm">
                <thead className="bg-gray-100 text-gray-600 uppercase">
                  <tr>
                    <th className="px-4 py-2 font-medium">Name</th>
                    <th className="px-4 py-2 font-medium text-right">Stock</th>
                  </tr>
                </thead>
                <tbody>
                  {lowStockProducts.map((p) => (
                    <tr
                      key={p.id}
                      className="border-t hover:bg-gray-50 transition-colors"
                    >
                      <td className="px-4 py-2">{p.name}</td>
                      <td className="px-4 py-2 text-right font-semibold text-yellow-600">
                        {p.stock}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
