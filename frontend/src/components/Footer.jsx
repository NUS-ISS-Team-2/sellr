import React from "react";
import { Link } from "react-router-dom"; // if you are using react-router

export default function Footer() {
  return (
    <footer className="bg-white shadow-inner mt-10 text-center py-4 text-gray-500 text-sm">
      <div>
        © 2025 sellr. All rights reserved.
      </div>
      <div>
        <Link to="/contact" className="text-blue-500 hover:underline">
          Contact Us
        </Link>
      </div>
    </footer>
  );
}
