import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";
import axios from "axios";
import { useCart } from "../context/CartContext";
import { API_BASE_URL } from "../config";

export default function CheckoutPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { clearCart } = useCart();

  const { address, paymentMethod, paymentDetails, cartItems, subtotal, userId } =
    location.state || {};

  const handlePlaceOrder = async () => {
    try {
      const res = await axios.post(`${API_BASE_URL}/orders/checkout`, {
        userId,
        address,
        paymentMethod,
        paymentDetails,
        items: cartItems,
        subtotal,
      });
      clearCart();
      navigate("/orderCreated", { state: { orderId: res.data.orderId } });
    } catch (err) {
      console.error("Error placing order:", err);
      alert("Order failed. Please try again.");
    }
  };

  if (!cartItems) return <div>Missing cart data.</div>;

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />
      <main className="flex-1 container mx-auto px-6 py-10">
        <h2 className="text-3xl font-bold mb-6">Checkout</h2>
        <div className="bg-white p-6 rounded shadow max-w-lg mx-auto">
          <h3 className="text-xl font-semibold mb-4">Shipping Address</h3>
          <p className="mb-4">
            {address
              ? `${address.fullName}, ${address.street}, ${address.city}, ${address.stateZipCountry}`
              : ""}
          </p>

          <h3 className="text-xl font-semibold mb-4">Payment Method</h3>
          <p className="mb-4">{paymentMethod}</p>

          <h3 className="text-xl font-semibold mb-4">Payment Details</h3>
          <p className="mb-4">
            {paymentMethod === "Credit Card" && paymentDetails
              ? `Card: ${paymentDetails.cardNumber}, Name: ${paymentDetails.cardName}, Exp: ${paymentDetails.expiry}`
              : ""}
            {paymentMethod === "PayPal" && paymentDetails
              ? `PayPal Email: ${paymentDetails.paypalEmail}`
              : ""}
            {paymentMethod === "Bank Transfer" && paymentDetails
              ? `Bank: ${paymentDetails.bankName}, Account #: ${paymentDetails.accountNumber}, Holder: ${paymentDetails.accountHolder}`
              : ""}
          </p>

          <h3 className="text-xl font-semibold mb-4">Order Summary</h3>
          <ul className="mb-4">
            {cartItems.map((item) => (
              <li key={item.productId} className="mb-2">
                {item.name} × {item.quantity} (${item.price})
              </li>
            ))}
          </ul>
          <p className="font-bold mb-4">Subtotal: ${subtotal.toFixed(2)}</p>

          <button
            className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700"
            onClick={handlePlaceOrder}
          >
            Place Order
          </button>
        </div>
      </main>
      <Footer />
    </div>
  );
}
