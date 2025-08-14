import React from "react";

export default function Header() {
  return (
    <header className="bg-blue-600 text-white">
      <div className="container mx-auto flex items-center justify-between px-6 py-4">
        <h1 className="text-2xl font-bold">sellr</h1>
        <nav className="space-x-6 font-medium">
          <a href="/" className="hover:text-gray-200">Home</a>
          <a href="/shop" className="hover:text-gray-200">Shop</a>
          <a href="/login" className="hover:text-gray-200">Login</a>
          <a href="/contact" className="hover:text-gray-200">Contact</a>
        </nav>
        <div className="relative">
          <button className="relative">
            🛒
            <span className="absolute -top-2 -right-2 bg-red-500 text-xs rounded-full px-1">3</span>
          </button>
        </div>
      </div>
    </header>
  );
}