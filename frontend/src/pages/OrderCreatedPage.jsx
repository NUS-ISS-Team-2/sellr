import React, { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import Header from "../components/Header";
import Footer from "../components/Footer";
import DisputeModal from "../components/DisputeModal";
import { API_BASE_URL } from "../config";

const API_URL = `${API_BASE_URL}/orders`;

export default function OrderCreatedPage() {
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [disputeModalOpen, setDisputeModalOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);

  const location = useLocation();
  const orderId = location.state?.orderId;
  const navigate = useNavigate();

  // Fetch order details
  useEffect(() => {
    async function fetchOrder() {
      try {
        const res = await axios.get(`${API_URL}/${orderId}`);
        setOrder(res.data);
        //console.log(res.data)
      } catch (error) {
        console.error("Error fetching order:", error);
      } finally {
        setLoading(false);
      }
    }
    if (orderId) fetchOrder();
  }, [orderId]);

  // Mark item as delivered
  const handleMarkAsDelivered = async (productId) => {
    try {
      await axios.put(`${API_URL}/buyer/status`, {
        orderId,
        productId,
        status: "DELIVERED",
      });

      const res = await axios.get(`${API_URL}/${orderId}`);
      setOrder(res.data);
    } catch (error) {
      console.error("Error marking as delivered:", error);
      alert("Failed to mark item as delivered. Please try again.");
    }
  };

  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>
  );

  if (!order) return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />
      <main className="flex-1 container mx-auto px-6 py-10">
        <h2 className="text-3xl font-bold mb-4">Order Not Found</h2>
        <Link to="/cart" className="text-blue-600 hover:underline">Back to Cart</Link>
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
        <p className="mb-6"><strong>Order ID:</strong> {order.orderId}</p>

        <h3 className="text-xl font-semibold mb-4">Items Ordered</h3>
        <div className="space-y-4">
          {order.items.map((item) => (
            <div key={item.productId} className="flex items-center bg-white p-4 rounded shadow">
              <img
                src={item.imageUrl}
                alt={item.productName}
                className="w-16 h-16 object-cover rounded"
              />
              <div className="ml-4 flex-1">
                <h4 className="font-semibold">{item.productName}</h4>
                <p>Quantity: {item.quantity}</p>
                <p>Sold By: {item.sellerName}</p>

                {/* Status with color */}
                <p>
                  Status:{" "}
                  <span
                    className={`ml-2 font-medium ${item.status === "PENDING"
                        ? "text-red-600"
                        : item.status === "SHIPPED"
                          ? "text-yellow-600"
                          : item.status === "DELIVERED"
                            ? "text-green-600"
                            : item.status === "DISPUTING"
                              ? "text-purple-600"
                              : "text-gray-600"
                      }`}
                  >
                    {item.status}
                  </span>
                </p>

                {/* Delivery info */}
                <p>
                  {item.status === "SHIPPED" && (
                    <>
                      Estimated Delivery Date:{" "}
                      {new Date(item.deliveryDate).toLocaleDateString("en-GB", {
                        day: "numeric",
                        month: "short",
                        year: "numeric",
                      })}
                    </>
                  )}
                  {item.status === "DELIVERED" && (
                    <>
                      Delivered On:{" "}
                      {new Date(item.deliveryDate).toLocaleDateString("en-GB", {
                        day: "numeric",
                        month: "short",
                        year: "numeric",
                      })}
                    </>
                  )}
                </p>

                {/* Disputing message */}
                {item.status === "DISPUTING" && (
                  <p className="mt-2 text-sm text-purple-700 font-medium">
                    The seller will contact you shortly regarding your dispute. If the seller has not responded within 3 days, please contact support.
                  </p>
                )}

                {/* Action buttons */}
                <div className="flex flex-wrap gap-2 mt-4">
                  {item.status === "SHIPPED" && (
                    <button
                      onClick={() => handleMarkAsDelivered(item.productId)}
                      className="bg-green-600 text-white px-5 py-2 rounded hover:bg-green-700 transition"
                    >
                      Mark as Delivered
                    </button>
                  )}

                  {(item.status === "SHIPPED" || item.status === "DELIVERED") && (
                    <button
                      onClick={() => {
                        setSelectedProduct(item);
                        setDisputeModalOpen(true);
                      }}
                      className="bg-red-600 text-white px-5 py-2 rounded hover:bg-red-700 transition"
                    >
                      Raise Dispute
                    </button>
                  )}

                  {item.status === "DELIVERED" && (
                    <button
                      onClick={() =>
                        navigate(`/products/${item.productId}/review`, {
                          state: { productName: item.name },
                        })
                      }
                      className="bg-yellow-600 text-white px-5 py-2 rounded hover:bg-yellow-700 transition"
                      title={`Add a review for ${item.name}`}
                    >
                      Add Review
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>


        <div className="mt-6">
          <Link to="/myorders" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">View All Orders</Link>
        </div>
      </main>

      <DisputeModal
        isOpen={disputeModalOpen}
        onClose={() => setDisputeModalOpen(false)}
        orderId={order.orderId}
        product={selectedProduct}
        onDisputeRaised={(updatedOrder) => setOrder(updatedOrder)}
      />

      <Footer />
    </div>
  );
}
