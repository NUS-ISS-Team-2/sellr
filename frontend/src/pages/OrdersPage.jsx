import React, { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";
import axios from "axios";
import { UserContext } from "../context/UserContext";
import { API_BASE_URL } from "../config";

export default function OrdersPage() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { userId } = useContext(UserContext);

    useEffect(() => {
        async function fetchOrders() {
            try {
                const res = await axios.get(`${API_BASE_URL}/orders/user/${userId}`); // adjust endpoint if needed
                setOrders(res.data);
            } catch (err) {
                console.error("Error fetching orders:", err);
                setError("Failed to load orders.");
            } finally {
                setLoading(false);
            }
        }

        fetchOrders();
    }, [userId]);

    if (loading) {
        return (
            <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex flex-col min-h-screen bg-gray-50">
                <Header />
                <main className="flex-1 container mx-auto px-6 py-10">
                    <p className="text-red-600">{error}</p>
                </main>
                <Footer />
            </div>
        );
    }

    return (
        <div className="flex flex-col min-h-screen bg-gray-50">
            <Header />

            <main className="flex-1 container mx-auto px-6 py-10">
                <h2 className="text-3xl font-bold mb-6">My Orders</h2>

                {orders.length === 0 ? (
                    <p>
                        You have no orders yet.{" "}
                        <Link to="/products" className="text-blue-600 hover:underline">
                            Browse Products
                        </Link>
                    </p>
                ) : (
                    <div className="space-y-4">
                        {orders.map((order) => (
                            <div
                                key={order.orderId}
                                className="bg-white p-4 rounded shadow flex justify-between items-center"
                            >
                                <div>
                                    <p>
                                        <strong>Order ID:</strong> {order.orderId}
                                    </p>
                                    <p>
                                        <strong>Date:</strong> {new Date(order.createdAt).toLocaleDateString()}
                                    </p>
                                    <p>
                                        <strong>Status:</strong>{" "}
                                        <span
                                            className={`font-medium ${order.overallStatus === "COMPLETED" ? "text-green-600" : "text-yellow-600"
                                                }`}
                                        >
                                            {order.overallStatus}
                                        </span>
                                    </p>

                                    <p>
                                        <strong>Items:</strong> {order.items.length}
                                    </p>
                                </div>
                                <Link
                                    to={`/order-created/`}
                                    state={{ orderId: order.orderId }}
                                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                                >
                                    View Details
                                </Link>
                            </div>
                        ))}
                    </div>
                )}
            </main>

            <Footer />
        </div>
    );
}
