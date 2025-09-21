import React, { useContext, useState, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import { UserContext } from "../context/UserContext";
import { useCart } from "../context/CartContext";
import CartButton from "./CartButton";

export default function Header() {
  const { username, logout, role } = useContext(UserContext); // include role
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();
  const { clearCart } = useCart();

  const handleLogout = () => {
    logout(navigate);
    clearCart();
  };

  const handleViewOrder = () => {
    navigate("/myorders");
    setIsOpen(false);
  };

  return (
    <header className="bg-blue-600 text-white">
      <div className="container mx-auto flex items-center justify-between px-6 py-4">
        <h1 className="text-2xl font-bold"><Link to="/">sellr</Link></h1>

        <div className="relative flex items-center space-x-4">
          <nav className="space-x-4 font-medium">
            <Link to="/products" className="hover:text-gray-200">Shop</Link>
            {role === "SELLER" || role === "ADMIN" ? (
              <Link to="/product-management" className="hover:text-gray-200">
                Product Management
              </Link>
            ) : null}
          </nav>

          {username ? (
            <>
              {/* User dropdown */}
              <div className="relative" ref={dropdownRef}>
                <button
                  onClick={() => setIsOpen(!isOpen)}
                  className="font-medium hover:underline"
                >
                  Hello, {username}
                </button>

                {isOpen && (
                  <div className="absolute right-0 mt-2 w-32 bg-white text-black rounded shadow-lg z-10">
                    <button
                      onClick={handleViewOrder}
                      className="w-full text-left px-4 py-2 hover:bg-gray-200"
                    >
                      View Orders
                    </button>
                    <button
                      onClick={handleLogout}
                      className="w-full text-left px-4 py-2 hover:bg-gray-200"
                    >
                      Logout
                    </button>
                  </div>
                )}
              </div>

              <CartButton />
            </>
          ) : (
            <div className="flex space-x-4">
              <Link to="/login" className="font-medium hover:underline">Login</Link>
              <Link to="/register" className="font-medium hover:underline">Register</Link>
              <Link to="/contact" className="hover:text-gray-200">Help</Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
