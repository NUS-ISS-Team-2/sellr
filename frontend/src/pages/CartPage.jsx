import React, { useState, useContext } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useCart } from "../context/CartContext";
import { UserContext } from "../context/UserContext";
import { useNavigate } from "react-router-dom";
import AddressForm from "../components/AddressForm";
import PaymentForm from "../components/PaymentForm";

export default function CartPage() {
  const { cartItems, updateCart, removeFromCart } = useCart();
  const { userId } = useContext(UserContext);
  const navigate = useNavigate();

  // Form states
  const [address, setAddress] = useState({
    fullName: "",
    street: "",
    city: "",
    stateZipCountry: "",
  });
  const [paymentMethod, setPaymentMethod] = useState("PayNow");
  const [paymentDetails, setPaymentDetails] = useState({});

  if (!cartItems) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!cartItems.length) {
    return (
      <div className="flex flex-col min-h-screen bg-gray-50">
        <Header />
        <main className="flex-1 container mx-auto px-6 py-10">
          <h2 className="text-3xl font-bold mb-6">Your Cart</h2>
          <p>Your cart is empty.</p>
        </main>
        <Footer />
      </div>
    );
  }

  const CartItem = ({ item }) => (
    <div className="flex items-center bg-white p-4 rounded shadow">
      <img
        src={item.imageUrl}
        alt={item.name}
        className="w-20 h-20 object-cover rounded"
      />
      <div className="ml-4 flex-1">
        <h3 className="font-semibold">{item.name}</h3>
        <p>${item.price}</p>
        <div className="flex items-center mt-2 space-x-2">
          <button
            onClick={() => updateCart(userId, item.productId, 1)}
            className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300"
          >
            +
          </button>
          <span>{item.quantity}</span>
          <button
            onClick={() => updateCart(userId, item.productId, -1)}
            className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300"
          >
            -
          </button>
          <button
            onClick={() => removeFromCart(userId, item.productId)}
            className="ml-4 text-red-600 hover:underline"
          >
            Remove
          </button>
        </div>
      </div>
    </div>
  );

  const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
  const subtotal = cartItems.reduce(
    (sum, item) => sum + (item.price || 0) * item.quantity,
    0
  );

  const handleCheckout = () => {
    if (
      !address.fullName ||
      !address.street ||
      !address.city ||
      !address.stateZipCountry
    ) {
      alert("Please fill in all address fields.");
      return;
    }

    const validPayment = {
      "Credit Card":
        paymentDetails.cardNumber &&
        paymentDetails.cardName &&
        paymentDetails.expiry &&
        paymentDetails.cvv,
      PayPal: paymentDetails.paypalEmail,
      "PayNow":
        paymentDetails.referenceNumber
    };

    if (!validPayment[paymentMethod]) {
      alert("Please fill in all payment details.");
      return;
    }

    navigate("/checkout", {
      state: {
        address,
        paymentMethod,
        paymentDetails,
        cartItems,
        subtotal,
        userId,
      },
    });
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />
      <main className="flex-1 container mx-auto px-6 py-10">
        <h2 className="text-3xl font-bold mb-6">Your Cart</h2>
        <div className="grid gap-6 md:grid-cols-12 items-start">
          <div className="md:col-span-8 space-y-4">
            {cartItems.map((item) => (
              <CartItem key={item.productId || item.id} item={item} />
            ))}
          </div>

          <div className="md:col-span-4 bg-white p-6 rounded shadow h-fit">
            <h3 className="text-xl font-bold mb-4">Summary</h3>
            <p className="mb-2">Items: {totalItems}</p>
            <p className="mb-2">
              Shipping Address:{" "}
              {address
                ? `${address.fullName}, ${address.street}, ${address.city}, ${address.stateZipCountry}`
                : ""}
            </p>
            <p className="mb-4 font-semibold">Subtotal: ${subtotal.toFixed(2)}</p>

            <form
              className="space-y-4"
              onSubmit={(e) => {
                e.preventDefault();
                handleCheckout();
              }}
            >
              <div>
                <label className="block mb-1 font-medium">Shipping Address</label>
                <AddressForm address={address} setAddress={setAddress} />
              </div>

              <div>
                <label className="block mb-1 font-medium">Payment Details</label>
                <PaymentForm
                  paymentMethod={paymentMethod}
                  setPaymentMethod={setPaymentMethod}
                  paymentDetails={paymentDetails}
                  setPaymentDetails={setPaymentDetails}
                />
              </div>

              <button
                type="submit"
                className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
              >
                Proceed to Checkout
              </button>
            </form>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
