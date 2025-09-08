import React, { useContext, useState, useRef, useEffect } from "react";
import { Link, useNavigate  } from "react-router-dom";
import { UserContext } from "../context/UserContext";
import CartButton from "./CartButton";

export default function Header() {
  const { username, logout } = useContext(UserContext);
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate(); 

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <header className="bg-blue-600 text-white">
      <div className="container mx-auto flex items-center justify-between px-6 py-4">
        <h1 className="text-2xl font-bold"><Link to="/">sellr</Link></h1>

        <nav className="space-x-6 font-medium">
          <Link to="/products" className="hover:text-gray-200">Shop</Link>
          <Link to="/contact" className="hover:text-gray-200">Contact</Link>
        </nav>
        <div className="relative flex items-center space-x-4">
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
                      onClick={() => logout(navigate)}
                      className="w-full text-left px-4 py-2 hover:bg-gray-200"
                    >
                      Logout
                    </button>
                  </div>
                )}
              </div>

              {/* Cart only if logged in */}
              <CartButton/>
            </>
          ) : (
            // If not logged in, show Login
            <div className="flex space-x-4">
              <button
                onClick={() => {

                }}
                className="font-medium hover:underline"
              >
                <Link to="/login">Login</Link>
              </button>
              <button
                onClick={() => {

                }}
                className="font-medium hover:underline"
              >
                <Link to="/register">Register</Link>
              </button>
            </div>
          )}
        </div>

      </div>
    </header>
  );
}
