import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useCart } from "../context/CartContext";
import { useContext } from "react";
import { UserContext } from "../context/UserContext";

export default function CartPage() {
  const { cartItems, updateCart, removeFromCart } = useCart();
  const { userId } = useContext(UserContext);

  // Loading state if cartItems is undefined
  if (!cartItems) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  // Empty cart state
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

  // Cart Item component
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

  // Cart totals
  const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
  const subtotal = cartItems.reduce(
    (sum, item) => sum + (item.price || 0) * item.quantity,
    0
  );

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />

      <main className="flex-1 container mx-auto px-6 py-10">
        <h2 className="text-3xl font-bold mb-6">Your Cart</h2>

        <div className="grid gap-6 md:grid-cols-12 items-start">
          {/* Cart Items */}
          <div className="md:col-span-8 space-y-4">
            {cartItems.map((item) => (
              <CartItem key={item.id} item={item} />
            ))}
          </div>

          {/* Summary */}
          <div className="md:col-span-4 bg-white p-6 rounded shadow h-fit">
            <h3 className="text-xl font-bold mb-4">Summary</h3>
            <p className="mb-2">Items: {totalItems}</p>
            <p className="mb-4 font-semibold">Subtotal: ${subtotal.toFixed(2)}</p>

            <button className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700">
              Proceed to Checkout
            </button>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}
