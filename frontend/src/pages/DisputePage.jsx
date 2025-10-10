// src/pages/DisputeManagementPage.jsx
import { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import { UserContext } from "../context/UserContext";
import Header from "../components/Header";
import { API_BASE_URL } from "../config";

export default function DisputeManagementPage() {
    const { userId, role } = useContext(UserContext);
    const [loading, setLoading] = useState(true);
    const [disputedItems, setDisputedItems] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        if (!userId || role !== "SELLER") return;

        const fetchDisputedOrders = async () => {
            setLoading(true);
            setError("");
            try {
                const res = await axios.get(`${API_BASE_URL}/orders/seller`, {
                    params: { sellerId: userId },
                });

                const orders = res.data || [];

                // Flatten all disputed items for this seller
                const disputes = orders.flatMap((order) =>
                    order.items
                        ?.filter((item) => item.sellerId === userId && (item.status === "DISPUTING" || item.status === "RESOLVED"))
                        .map((item) => ({
                            ...item,
                            orderId: order.orderId,
                            userId: order.userId,
                            orderDate: order.createdAt,
                        }))
                );
                console.log(disputes);
                setDisputedItems(disputes);
            } catch (err) {
                console.error("Failed to fetch disputed orders:", err);
                setError("Failed to fetch disputed orders.");
            } finally {
                setLoading(false);
            }
        };

        fetchDisputedOrders();
    }, [userId, role]);


    const handleContactBuyer = (buyerId) => {
        alert("Feature not implemented")
        console.log("Contact buyer:", buyerId);
    };


    const handleResolved = async (orderId, productId) => {
        try {

            console.log(orderId, productId);
            await axios.put(`${API_BASE_URL}/orders/item/resolve`, {
                orderId,
                productId
            });
            // Refresh the list of disputed items
            setDisputedItems((prev) => prev.filter(item => !(item.orderId === orderId && item.productId === productId)));
            alert("Dispute marked as resolved.");
        } catch (err) {
            console.error("Failed to resolve dispute:", err);
            alert("Failed to resolve dispute.");
        }
    };

    return (
        <div className="flex flex-col min-h-screen bg-gray-50">
            <Header />

            <main className="flex-1 container mx-auto px-6 py-10">
                <h1 className="text-3xl font-bold mb-6">Dispute Management</h1>

                {loading ? (
                    <div className="flex justify-center py-10">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
                    </div>
                ) : error ? (
                    <div className="text-red-600 text-center py-4">{error}</div>
                ) : disputedItems.length === 0 ? (
                    <div className="text-gray-500 text-center py-4">No disputed items found.</div>
                ) : (
                    <div className="overflow-x-auto border rounded-lg">
                        <table className="min-w-full text-left text-sm">
                            <thead className="bg-gray-100 text-gray-600 uppercase">
                                <tr>
                                    <th className="px-4 py-2 font-medium">Order ID</th>
                                    <th className="px-4 py-2 font-medium">User ID</th>
                                    <th className="px-4 py-2 font-medium">Product</th>
                                    <th className="px-4 py-2 font-medium">Quantity</th>
                                    <th className="px-4 py-2 font-medium">Price</th>
                                    <th className="px-4 py-2 font-medium">Status</th>
                                    <th className="px-4 py-2 font-medium">Dispute Reason</th>
                                    <th className="px-4 py-2 font-medium">Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                {disputedItems.map((item) => (
                                    <tr
                                        key={`${item.orderId}-${item.productId}`}
                                        className="border-t hover:bg-gray-50 transition-colors"
                                    >
                                        <td className="px-4 py-2">{item.orderId}</td>
                                        <td className="px-4 py-2">{item.userId}</td>
                                        <td className="px-4 py-2">{item.productName}</td>
                                        <td className="px-4 py-2">{item.quantity}</td>
                                        <td className="px-4 py-2">${item.price.toFixed(2)}</td>
                                        <td
                                            className={`px-4 py-2 font-semibold ${item.status === "DISPUTING"
                                                    ? "text-red-600"
                                                    : item.status === "RESOLVED"
                                                        ? "text-green-600"
                                                        : "text-gray-800"
                                                }`}
                                        >
                                            {item.status}
                                        </td>                                        
                                        <td className="px-4 py-2">{item.disputeDescription || "—"}</td>
                                        <td className="px-4 py-2">{new Date(item.disputeRaisedAt).toLocaleString()}</td>
                                        <td className="px-4 py-2">
                                            <button
                                                onClick={() => handleContactBuyer(item.userId)}
                                                className="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700"
                                            >
                                                Contact Buyer
                                            </button>
                                        </td>
                                        <td className="px-4 py-2">
                                            <button
                                                onClick={() => handleResolved(item.orderId, item.productId)}
                                                className="px-3 py-1 bg-green-600 text-white rounded hover:bg-blue-700"
                                            >
                                                Resolved
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                <div className="mt-6">
                    <Link
                        to="/seller-dashboard"
                        className="inline-block px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Back to Dashboard
                    </Link>
                </div>
            </main>
        </div>
    );
}
