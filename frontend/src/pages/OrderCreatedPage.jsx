import React, { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";
import axios from "axios";

export default function OrderCreatedPage() {
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const location = useLocation();
  const orderId = location.state?.orderId;

  useEffect(() => {
    async function fetchOrder() {
      try {
        const res = await axios.get(
          `http://localhost:8080/api/orders/${orderId}`
        );
        setOrder(res.data);
      } catch (error) {
        console.error("Error fetching order:", error);
      } finally {
        setLoading(false);
      }
    }

    fetchOrder();
  }, [orderId]);

  if (loading)
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );

  if (!order)
    return (
      <div className="flex flex-col min-h-screen bg-gray-50">
        <Header />
        <main className="flex-1 container mx-auto px-6 py-10">
          <h2 className="text-3xl font-bold mb-4">Order Not Found</h2>
          <Link to="/cart" className="text-blue-600 hover:underline">
            Back to Cart
          </Link>
        </main>
        <Footer />
      </div>
    );

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />
      <main className="flex-1 container mx-auto px-6 py-10">
        <h2 className="text-3xl font-bold mb-6">Order Created</h2>
        <p className="mb-4">Your order has been created successfully.</p>
        <p className="mb-6">
          <strong>Order ID:</strong> {order.orderId}
        </p>

        <h3 className="text-xl font-semibold mb-4">Items Ordered</h3>
        <div className="space-y-4">
          {order.items.map((item) => (
            <div
              key={item.productId}
              className="flex items-center bg-white p-4 rounded shadow"
            >
              <img
                src={item.imageUrl}
                alt={item.name}
                className="w-16 h-16 object-cover rounded"
              />
              <div className="ml-4 flex-1">
                <h4 className="font-semibold">{item.name}</h4>
                <p>Quantity: {item.quantity}</p>
                <p>
                  Status:
                  <span className="ml-2 font-medium text-yellow-600">
                    {item.status}
                  </span>
                </p>
              </div>
            </div>
          ))}
        </div>

        <div className="mt-6">
          <Link
            to="/myorders"
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          >
            View All Orders
          </Link>
        </div>
      </main>
      <Footer />
    </div>
  );
}
