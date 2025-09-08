import { useCart } from "../context/CartContext";
import { Link

 } from "react-router-dom";
export default function CartButton() {
  const { cartItems } = useCart();

  return (
    <Link to="/cart" className="relative">
      <button>
        🛒
        {cartItems.length > 0 && (
          <span className="absolute -top-2 -right-2 bg-red-500 text-xs rounded-full px-1">
            {cartItems.length}
          </span>
        )}
      </button>
    </Link>
  );
}
