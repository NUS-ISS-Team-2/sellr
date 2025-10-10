import React, { useContext, useState, useRef, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { UserContext } from "../context/UserContext";
import { useCart } from "../context/CartContext";
import CartButton from "./CartButton";
import axios from "axios";
import { API_BASE_URL } from "../config";
import BellButton from "./BellButton";

export default function Header() {
  const { username, logout, role, userId } = useContext(UserContext);
  const [openDropdown, setOpenDropdown] = useState(null);
  const [outstandingOrders, setOutstandingOrders] = useState(0);
  const wrapperRef = useRef(null);
  const navigate = useNavigate();
  const { clearCart } = useCart();

  // 🧠 Fetch outstanding orders for seller/admin
  useEffect(() => {
    const fetchOutstandingOrders = async () => {
      if ((role === "SELLER" || role === "ADMIN") && userId) {
        try {
          const res = await axios.get(`${API_BASE_URL}/orders/seller`, {
            params: { sellerId: userId },
          });

          const orders = res.data || [];
          const count = orders.reduce((acc, order) => {
            const pendingItems = order.items?.filter(
              (item) => item.sellerId === userId && item.status === "PENDING"
            ).length;
            return acc + (pendingItems || 0);
          }, 0);

          setOutstandingOrders(count);
        } catch (err) {
          console.error("Failed to fetch outstanding orders:", err);
        }
      }
    };

    fetchOutstandingOrders();
  }, [role, userId]);

  // 🧱 Close dropdowns when clicking outside
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
        {/* Logo */}
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

          {/* Admin Dropdown */}
          {role === "ADMIN" && (
            <div className="relative">
              <button
                onClick={() =>
                  setOpenDropdown(openDropdown === "ADMIN" ? null : "ADMIN")
                }
                className="font-medium hover:underline"
              >
                Admin Console
              </button>

              {openDropdown === "ADMIN" && (
                <div className="absolute right-0 top-full mt-2 w-40 bg-white text-black rounded shadow-lg z-10">
                  <button
                    onClick={() => {
                      navigate("/users");
                      setOpenDropdown(null);
                    }}
                    className="w-full text-left px-4 py-2 hover:bg-gray-200"
                  >
                    All Users
                  </button>
                  <button
                    onClick={() => {
                      navigate("/product-management");
                      setOpenDropdown(null);
                    }}
                    className="w-full text-left px-4 py-2 hover:bg-gray-200"
                  >
                    All Products
                  </button>
                  <button
                    onClick={() => {
                      navigate("/manageorders");
                      setOpenDropdown(null);
                    }}
                    className="w-full text-left px-4 py-2 hover:bg-gray-200"
                  >
                    All Orders
                  </button>
                </div>
              )}
            </div>
          )}

          {/* Seller Dropdown */}
          {(role === "SELLER") && (
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
          )}

          {/* User Dropdown + Notifications/Cart */}
          {username ? (
            <div className="flex items-center">
              {/* User dropdown */}
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

              {/* 🔔 Bell for Seller/Admin */}
              {(role === "SELLER" || role === "ADMIN") ? (
                <div className="ml-4 relative">
                  <button
                    onClick={() => navigate("/manageorders")}
                    className="relative p-2 hover:bg-blue-700 rounded-full transition"
                    title="View Orders"
                  >
                    <BellButton count={outstandingOrders} to="/manageorders" />
                  </button>
                </div>
              ) : (
                <div className="ml-4">
                  <CartButton />
                </div>
              )}
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
