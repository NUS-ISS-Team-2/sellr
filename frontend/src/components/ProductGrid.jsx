// ProductGrid.jsx
import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { UserContext } from "../context/UserContext";
import { useContext, useEffect } from "react";

export default function ProductGrid({ products = [] }) {
  const { cartItems, updateCart, addToCart } = useCart();
  const { userId } = useContext(UserContext);

  useEffect(() => {
    console.log("Cart Items:", cartItems);
  }, [cartItems]);

  const truncate = (text, maxLength) =>
    !text ? "" : text.length > maxLength ? text.substring(0, maxLength) + "..." : text;

  return (
    <div className="w-full">
      <h2 className="text-2xl font-bold mb-4">Browse Products</h2>
      <div className="grid gap-4 grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
        {products.map((product) => {
          const cartItem = cartItems.find((item) => item.productId === product.id);
          const quantity = cartItem?.quantity || 0;

          return (
            <div
              key={product.id}
              className="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:shadow-lg transition flex flex-col"
            >
              <Link
                to={`/products/${product.id}`}
                className="block flex-1"
              >
                <img
                  src={product.imageUrl}
                  alt={product.name}
                  className="w-full h-48 object-cover"
                />
                <div className="p-4">
                  <h4 className="font-semibold text-lg">{product.name}</h4>
                  <p className="text-gray-600">{truncate(product.description, 80)}</p>
                  <p className="text-gray-800 font-bold mt-2">
                    ${Number(product.price ?? 0).toLocaleString()}
                  </p>
                </div>
              </Link>

              <div className="px-4 pb-4">
                {quantity === 0 ? (
                  <button
                    onClick={() => addToCart(userId, product)}
                    className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
                  >
                    Add to Cart
                  </button>
                ) : (
                  <div className="flex items-center space-x-2">
                    <span>{quantity}</span>
                    <button
                      onClick={() => updateCart(userId, product.id, 1)}
                      className="bg-green-500 text-white px-2 py-1 rounded hover:bg-green-600 transition"
                    >
                      +
                    </button>
                    <button
                      onClick={() => updateCart(userId, product.id, -1)}
                      className="bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600 transition"
                    >
                      -
                    </button>
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
