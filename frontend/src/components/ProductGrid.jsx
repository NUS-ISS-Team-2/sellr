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
          const inStock = Number(product?.stock ?? 0) > 0; // <-- define it here

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

              {/* Cart Controls */}
                {/* <div className="mt-5 flex items-center gap-2">
                  {quantity === 0 ? (
                    <button
                      onClick={() => addToCart(userId, product)}
                      disabled={!inStock}
                      className={`w-full py-2 rounded text-white ${inStock ? "bg-blue-600 hover:bg-blue-700" : "bg-gray-400 cursor-not-allowed"
                        }`}
                    >
                      Add to Cart
                    </button>
                  ) : (
                    <div className="flex items-center space-x-2">
                      
                      <button
                        onClick={() => updateCart(userId, product.id, 1)}
                        disabled={!inStock || quantity >= product.stock}
                        className={`px-2 py-1 rounded text-white ${inStock && quantity < product.stock
                            ? "bg-green-500 hover:bg-green-600"
                            : "bg-gray-400 cursor-not-allowed"
                          }`}
                      >
                        +
                      </button>
                      <span>{quantity}</span>
                      <button
                        onClick={() => updateCart(userId, product.id, -1)}
                        disabled={quantity <= 0}
                        className={`px-2 py-1 rounded text-white ${quantity > 0 ? "bg-red-500 hover:bg-red-600" : "bg-gray-400 cursor-not-allowed"
                          }`}
                      >
                        -
                      </button>
                    </div>
                  )}
                </div> */}
            </div>
          );
        })}
      </div>
    </div>
  );
}
