import { useCart } from "../context/CartContext";
import { UserContext } from "../context/UserContext";
import axios from "axios";
import { useContext } from "react";

export default function ProductGrid({ products = [] }) {
  const { cartItems, addToCart, decreaseFromCart } = useCart(); // use decrease instead of remove
  const { userId } = useContext(UserContext);

  const truncate = (text, maxLength) =>
    !text ? "" : text.length > maxLength ? text.substring(0, maxLength) + "..." : text;

  const updateCart = async (product, qtyChange) => {
    try {
      const res = await axios.post("http://localhost:8080/api/cart/add", {
        userId,
        productId: product.id,
        quantity: qtyChange, // +1 or -1
      });

      if (res.status === 200) {
        if (qtyChange > 0) {
          addToCart({ ...product, quantity: qtyChange });
        } else {
          decreaseFromCart(product.id);
        }
      }
    } catch (err) {
      console.error("Failed to update cart:", err);
    }
  };

  return (
    <div className="w-full">
      <h2 className="text-2xl font-bold mb-4">Browse Products</h2>

      <div className="grid gap-4 grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
        {products.map((product, idx) => {
          const cartItem = cartItems.find((item) => item.id === product.id);

          return (
            <div
              key={product.id ?? idx}
              className="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:shadow-lg transition"
            >
              <img
                src={product.imageUrl}
                alt={product.name}
                className="w-full h-48 object-cover"
              />
              <div className="p-4 flex flex-col justify-between h-56">
                <div>
                  <h4 className="font-semibold text-lg">{product.name}</h4>
                  <p className="text-gray-600">{truncate(product.description, 80)}</p>
                </div>

                <div className="mt-2">
                  <p className="text-gray-800 font-bold mb-2">
                    ${Number(product.price ?? 0).toLocaleString()}
                  </p>

                  {!cartItem ? (
                    <button
                      onClick={() => updateCart(product, 1)}
                      className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
                    >
                      Add to Cart
                    </button>
                  ) : (
                    <div className="flex items-center space-x-2">
                      <span>{cartItem.quantity}</span>
                      <button
                        onClick={() => updateCart(product, 1)}
                        className="bg-green-500 text-white px-2 py-1 rounded hover:bg-green-600 transition"
                      >
                        +
                      </button>
                      <button
                        onClick={() => updateCart(product, -1)}
                        className="bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600 transition"
                      >
                        -
                      </button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
