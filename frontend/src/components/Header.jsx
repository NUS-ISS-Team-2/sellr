import React, { useContext, useState, useRef, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { UserContext } from "../context/UserContext";
import { useCart } from "../context/CartContext";
import CartButton from "./CartButton";

export default function Header() {
  const { username, logout, role } = useContext(UserContext);
  const [openDropdown, setOpenDropdown] = useState(null); // "USER", "SELLER", or null
  const wrapperRef = useRef(null); // Ref for detecting outside clicks
  const navigate = useNavigate();
  const { clearCart } = useCart();

  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setOpenDropdown(null);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    logout(navigate);
    clearCart();
    setOpenDropdown(null);
  };

  const handleViewOrders = () => {
    navigate("/myorders");
    setOpenDropdown(null);
  };

  const handleManageProducts = () => {
    navigate("/product-management");
    setOpenDropdown(null);
  };

  const handleManageOrders = () => {
    navigate("/manageorders");
    setOpenDropdown(null);
  };

  const handleMyWishlist = () => {
      navigate("/wishlist");
      setOpenDropdown(null);
  };  

  return (
    <header className="bg-blue-600 text-white">
      <div className="container mx-auto flex items-center justify-between px-6 py-4">
        <h1 className="text-2xl font-bold">
          <Link to="/">sellr</Link>
        </h1>

        <div className="flex items-center space-x-4" ref={wrapperRef}>
          {/* Shop Link */}
          <nav className="space-x-4 font-medium">
            <Link to="/products" className="hover:text-gray-200">
              Shop
            </Link>
          </nav>

          {/* Seller Dropdown */}
          {role === "SELLER" || role === "ADMIN" ? (
            <div className="relative">
              <button
                onClick={() =>
                  setOpenDropdown(openDropdown === "SELLER" ? null : "SELLER")
                }
                className="font-medium hover:underline"
              >
                Seller Console
              </button>

              {openDropdown === "SELLER" && (
                <div className="absolute right-0 top-full mt-2 w-40 bg-white text-black rounded shadow-lg z-10">
                  <button
                    onClick={handleManageProducts}
                    className="w-full text-left px-4 py-2 hover:bg-gray-200"
                  >
                    Manage Products
                  </button>
                  <button
                    onClick={handleManageOrders}
                    className="w-full text-left px-4 py-2 hover:bg-gray-200"
                  >
                    Manage Orders
                  </button>
                </div>
              )}
            </div>
          ) : null}

          {/* User Dropdown + Cart */}
          {username ? (
            <div className="flex items-center">
              <div className="relative">
                <button
                  onClick={() =>
                    setOpenDropdown(openDropdown === "USER" ? null : "USER")
                  }
                  className="font-medium hover:underline"
                >
                  Hello, {username}
                </button>

                {openDropdown === "USER" && (
                  <div className="absolute right-0 top-full mt-2 w-32 bg-white text-black rounded shadow-lg z-10">
                    <button
                      onClick={handleViewOrders}
                      className="w-full text-left px-4 py-2 hover:bg-gray-200"
                    >
                      View Orders
                    </button>
                    <button
                      onClick={handleMyWishlist}
                      className="w-full text-left px-4 py-2 hover:bg-gray-200"
                    >
                      My Wishlist
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

              <div className="ml-4">
                <CartButton />
              </div>
            </div>
          ) : (
            <div className="flex space-x-4">
              <Link to="/login" className="font-medium hover:underline">
                Login
              </Link>
              <Link to="/register" className="font-medium hover:underline">
                Register
              </Link>
              <Link to="/contact" className="hover:text-gray-200">
                Help
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
