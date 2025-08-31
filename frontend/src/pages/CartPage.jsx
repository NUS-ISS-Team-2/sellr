import React, { useEffect, useState, useContext } from "react";
import axios from "axios";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { UserContext } from "../context/UserContext";

export default function CartPage() {
  const { userId } = useContext(UserContext);
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);

  // Fetch cart
  useEffect(() => {

    console.log("Fetching cart for userId:", userId);
    if (!userId) return;
    axios
      .get(`http://localhost:8080/api/cart?userId=${userId}`)
      .then((res) => setCart(res.data))
      .catch((err) => console.error("Failed to fetch cart:", err))
      .finally(() => setLoading(false));
  }, [userId]);

  // Update quantity
  const updateQuantity = async (productId, quantity) => {
    try {
      const res = await axios.put("http://localhost:8080/api/cart/update", {
        userId,
        productId,
        quantity,
      });
      setCart(res.data);
    } catch (err) {
      console.error("Failed to update quantity:", err);
    }
  };

  // Remove item
  const removeItem = async (productId) => {
    try {
      const res = await axios.delete("http://localhost:8080/api/cart/remove", {
        data: { userId, productId },
      });
      console.log(res.data)

      setCart(res.data); 
    } catch (err) {
      console.error("Failed to remove item:", err);
    }
  };

  // Compute subtotal
  const subtotal = cart?.items?.reduce(
    (acc, item) => acc + item.price * item.quantity,
    0
  );

  if (loading) return <div>Loading cart...</div>;

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />

      <main className="flex-1 container mx-auto px-6 py-10">
        <h2 className="text-3xl font-bold mb-6">Your Cart</h2>

        {!cart?.items?.length ? (
          <p>Your cart is empty.</p>
        ) : (
          <div className="grid gap-6 md:grid-cols-12 items-start">
            {/* Cart items list */}
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
                        onClick={() =>
                          updateQuantity(item.productId, item.quantity - 1)
                        }
                        className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300"
                        disabled={item.quantity <= 1}
                      >
                        -
                      </button>
                      <span>{item.quantity}</span>
                      <button
                        onClick={() =>
                          updateQuantity(item.productId, item.quantity + 1)
                        }
                        className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300"
                      >
                        +
                      </button>

                      <button onClick={() => removeItem(item.productId)}>Remove</button>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Cart summary */}
            <div className="md:col-span-4 bg-white p-6 rounded shadow h-fit">
              <h3 className="text-xl font-bold mb-4">Summary</h3>
              <p className="mb-2">
                Items: {cart.items.length}
              </p>
              <p className="mb-4 font-semibold">
                Subtotal: ${subtotal.toFixed(2)}
              </p>

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
