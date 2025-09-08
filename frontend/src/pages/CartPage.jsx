import React, { useEffect, useState, useContext, useCallback } from "react";
import axios from "axios";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { UserContext } from "../context/UserContext";

export default function CartPage() {
  const { userId } = useContext(UserContext);

  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [updatingItem, setUpdatingItem] = useState(null);

  const fetchCart = useCallback(async () => {
    if (!userId) return;
    setLoading(true);
    setError(null);

    try {
      const res = await axios.get(`http://localhost:8080/api/cart?userId=${userId}`);
      setCart({
        ...res.data,
        items: res.data.items.filter((item) => item.quantity > 0),
      });
    } catch (err) {
      console.error("Failed to fetch cart:", err);
      setError("Failed to load cart. Please try again.");
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  // Update quantity (optimistic)
  const updateQuantity = async (productId, quantity) => {
    setUpdatingItem(productId);

    if (quantity < 1) {
      return removeItem(productId);
    }

    // Optimistic UI update
    setCart((prev) => ({
      ...prev,
      items: prev.items.map((item) =>
        item.productId === productId ? { ...item, quantity } : item
      ),
    }));

    try {
      const res = await axios.put("http://localhost:8080/api/cart/update", {
        userId,
        productId,
        quantity,
      });
      setCart({
        ...res.data,
        items: res.data.items.filter((item) => item.quantity > 0),
      });
    } catch (err) {
      console.error("Failed to update quantity:", err);
      setError("Failed to update item quantity.");
      fetchCart(); // revert to server state
    } finally {
      setUpdatingItem(null);
    }
  };

  // Remove item from cart
  const removeItem = async (productId) => {
    setUpdatingItem(productId);

    // Optimistic UI update
    setCart((prev) => ({
      ...prev,
      items: prev.items.filter((item) => item.productId !== productId),
    }));

    try {
      const res = await axios.delete("http://localhost:8080/api/cart/remove", {
        data: { userId, productId },
      });
      setCart({
        ...res.data,
        items: res.data.items.filter((item) => item.quantity > 0),
      });
    } catch (err) {
      console.error("Failed to remove item:", err);
      setError("Failed to remove item from cart.");
      fetchCart(); // revert to server state
    } finally {
      setUpdatingItem(null);
    }
  };

  // Totals
  const subtotal = cart?.items?.reduce((sum, i) => sum + i.price * i.quantity, 0) || 0;
  const totalItems = cart?.items?.reduce((sum, i) => sum + i.quantity, 0) || 0;

  // Loading state
  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />

      <main className="flex-1 container mx-auto px-6 py-10">
        <h2 className="text-3xl font-bold mb-6">Your Cart</h2>

        {error && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>
        )}

        {!cart?.items?.length ? (
          <p>Your cart is empty.</p>
        ) : (
          <div className="grid gap-6 md:grid-cols-12 items-start">
            {/* Cart Items */}
            <div className="md:col-span-8 space-y-4">
              {cart.items.map((item) => (
                <div
                  key={item.productId}
                  className="flex items-center bg-white p-4 rounded shadow"
                >
                  <img
                    src={item.imageUrl}
                    alt={item.name}
                    className="w-20 h-20 object-cover rounded"
                  />
                  <div className="ml-4 flex-1">
                    <h3 className="font-semibold">{item.name}</h3>
                    <p>${item.price.toFixed(2)}</p>

                    <div className="flex items-center mt-2 space-x-2">
                      <button
                        onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                        className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300"
                        disabled={updatingItem === item.productId}
                      >
                        -
                      </button>
                      <span>{item.quantity}</span>
                      <button
                        onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                        className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300"
                        disabled={updatingItem === item.productId}
                      >
                        +
                      </button>

                      <button
                        onClick={() => removeItem(item.productId)}
                        className="ml-4 text-red-600 hover:underline"
                        disabled={updatingItem === item.productId}
                      >
                        Remove
                      </button>
                    </div>
                  </div>
                </div>
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
        )}
      </main>

      <Footer />
    </div>
  );
}
