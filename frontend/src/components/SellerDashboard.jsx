import { useEffect, useState, useContext } from "react";
import axios from "axios";
import { UserContext } from "../context/UserContext";
import { API_BASE_URL } from "../config";

export default function SellerDashboard() {
  const { userId, role } = useContext(UserContext);
  const [loading, setLoading] = useState(true);
  const [outstandingOrders, setOutstandingOrders] = useState(0);
  const [lowStockCount, setLowStockCount] = useState(0);

  useEffect(() => {
    if (role !== "SELLER" || !userId) return;
    fetchDashboardData();
  }, [role, userId]);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      // Fetch seller orders
      const ordersRes = await axios.get(`${API_BASE_URL}/orders/seller`, {
        params: { sellerId: userId },
      });

      const orders = ordersRes.data || [];

      // Count outstanding order items (PENDING)
      const outstandingCount = orders.reduce((count, order) => {
        const pendingItems = order.items?.filter(
          (item) => item.sellerId === userId && item.status === "PENDING"
        ).length;
        return count + (pendingItems || 0);
      }, 0);
      setOutstandingOrders(outstandingCount);

      // Fetch seller products
      const productsRes = await axios.get(
        `${API_BASE_URL}/products/my-products?sellerId=${userId}`
      );
      const products = productsRes.data || [];

      const lowStock = products.filter((p) => p.stock < 10).length;
      setLowStockCount(lowStock);
    } catch (err) {
      console.error("Failed to load seller dashboard:", err);
    } finally {
      setLoading(false);
    }
  };

  if (role !== "SELLER") return null;

  return (
    <div className="bg-white border border-gray-200 rounded-xl shadow-sm p-6">
      <h2 className="text-2xl font-bold mb-6 text-gray-800">
        Your Summary
      </h2>

      {loading ? (
        <div className="flex justify-center py-10">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
        </div>
      ) : (
        <div className="grid sm:grid-cols-2 lg:grid-cols-2 gap-6">
          {/* Outstanding Orders */}
          <div className="p-4 border rounded-lg text-center">
            <h3 className="text-lg font-semibold text-gray-700">
              Outstanding Orders
            </h3>
            <p
              className={`text-3xl font-bold mt-2 ${
                outstandingOrders > 0 ? "text-red-600" : "text-green-600"
              }`}
            >
              {outstandingOrders}
            </p>
            <p className="text-sm text-gray-500 mt-1">
              Pending shipments
            </p>
          </div>

          {/* Low Stock Products */}
          <div className="p-4 border rounded-lg text-center">
            <h3 className="text-lg font-semibold text-gray-700">
              Low Stock Products
            </h3>
            <p
              className={`text-3xl font-bold mt-2 ${
                lowStockCount > 0 ? "text-yellow-500" : "text-green-600"
              }`}
            >
              {lowStockCount}
            </p>
            <p className="text-sm text-gray-500 mt-1">
              Stock below 10 units
            </p>
          </div>
        </div>
      )}
    </div>
  );
}
