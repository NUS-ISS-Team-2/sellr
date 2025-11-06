import { useEffect, useState, useContext, useCallback } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import PropTypes from "prop-types";
import { UserContext } from "../context/UserContext";
import { API_BASE_URL } from "../config";

export default function SellerDashboard({ products }) {
  const { userId, role } = useContext(UserContext);
  const [loading, setLoading] = useState(true);
  const [outstandingOrders, setOutstandingOrders] = useState(0);
  const [disputedOrders, setDisputedOrders] = useState(0);
  const [isTableOpen, setIsTableOpen] = useState(false);

  // --- Helper Functions ---
  const countItemsByStatus = useCallback((orders, status) => {
    return orders.reduce((count, order) => {
      const items = order.items?.filter(
        (item) => item.sellerId === userId && item.status === status
      );
      return count + (items?.length || 0);
    }, 0);
  }, [userId]);

  const fetchOutstandingOrders = useCallback(async () => {
    if (role !== "SELLER" || !userId) return;
    setLoading(true);

    try {
      const { data } = await axios.get(`${API_BASE_URL}/orders/seller`, {
        params: { sellerId: userId },
      });
      const orders = data || [];

      const outstandingCount = countItemsByStatus(orders, "PENDING");
      const disputedCount = countItemsByStatus(orders, "DISPUTING");

      setOutstandingOrders(outstandingCount);
      setDisputedOrders(disputedCount);
    } catch (err) {
      console.error("Failed to load outstanding orders:", err);
    } finally {
      setLoading(false);
    }
  }, [role, userId, countItemsByStatus]);

  // --- Lifecycle ---
  useEffect(() => {
    fetchOutstandingOrders();
  }, [fetchOutstandingOrders]);

  // --- Derived Values ---
  if (role !== "SELLER") return null;
  const lowStockProducts = products?.filter((p) => p.stock < 10) || [];
  const lowStockCount = lowStockProducts.length;

  // --- Render ---
  return (
    <div className="bg-white border border-gray-200 rounded-xl shadow-sm p-6">
      <h2 className="text-2xl font-bold mb-6 text-gray-800">Your Summary</h2>

      {loading ? (
        <div className="flex justify-center py-10">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
        </div>
      ) : (
        <div className="grid sm:grid-cols-3 gap-6">
          <DashboardCard
            title="Outstanding Orders"
            count={outstandingOrders}
            to="/manageorders"
            colorClass={
              outstandingOrders > 0 ? "text-red-600" : "text-green-600"
            }
            description="Pending shipments"
          />
          <DashboardCard
            title="Low Stock Products"
            count={lowStockCount}
            to="/product-management"
            colorClass={
              lowStockCount > 0 ? "text-yellow-500" : "text-green-600"
            }
            description="Stock below 10 units"
          />
          <DashboardCard
            title="Disputed Orders"
            count={disputedOrders}
            to="/disputes"
            colorClass={
              disputedOrders > 0 ? "text-red-600" : "text-green-600"
            }
            description="Orders currently in dispute"
          />
        </div>
      )}

      {lowStockProducts.length > 0 && (
        <LowStockTable
          isTableOpen={isTableOpen}
          setIsTableOpen={setIsTableOpen}
          products={lowStockProducts}
        />
      )}
    </div>
  );
}

function DashboardCard({ title, count, to, colorClass, description }) {
  return (
    <Link
      to={to}
      className="block p-4 border rounded-lg text-center hover:bg-blue-50 transition"
    >
      <h3 className="text-lg font-semibold text-gray-700">{title}</h3>
      <p className={`text-3xl font-bold mt-2 ${colorClass}`}>{count}</p>
      <p className="text-sm text-gray-500 mt-1">{description}</p>
    </Link>
  );
}

DashboardCard.propTypes = {
  title: PropTypes.string.isRequired,
  count: PropTypes.number.isRequired,
  to: PropTypes.string.isRequired,
  colorClass: PropTypes.string.isRequired,
  description: PropTypes.string.isRequired,
};

function LowStockTable({ isTableOpen, setIsTableOpen, products }) {
  return (
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
              {products.map((p) => (
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
  );
}

LowStockTable.propTypes = {
  isTableOpen: PropTypes.bool.isRequired,
  setIsTableOpen: PropTypes.func.isRequired,
  products: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.any.isRequired,
      name: PropTypes.string.isRequired,
      stock: PropTypes.number.isRequired,
    })
  ).isRequired,
};

SellerDashboard.propTypes = {
  products: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.any.isRequired,
      name: PropTypes.string,
      stock: PropTypes.number,
    })
  ),
};
