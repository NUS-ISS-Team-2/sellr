import React, { useState } from "react";
import Footer from "../components/Footer";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";

export default function RegisterPage() {

  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [activeTab, setActiveTab] = useState("buyer");

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      alert("Passwords do not match!");
      return;
    }
    try {
      await axios.post("http://localhost:8080/api/users", {
        username: username,
        email: email,
        password: password,
        role: activeTab.toUpperCase()
      })
      navigate("/");
    } catch (error) {
      console.log(error)
      if (error.response.data.error) {
        alert(error.response.data.error);
      } else {
        alert("Registration failed. Please try again.");
      }
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-blue-600">
      <main className="flex-1 flex items-center justify-center">
        <div className="w-full max-w-md bg-white p-8 rounded-lg shadow-md text-gray-800">
          <h2 className="text-2xl font-bold mb-6 text-center">Register for sellr</h2>

          {/* Tabs */}
          <div className="flex mb-6 border-b">
            <button
              className={`flex-1 py-2 text-center ${activeTab === "buyer"
                  ? "border-b-2 border-blue-600 font-bold"
                  : "text-gray-500"
                }`}
              onClick={() => setActiveTab("buyer")}
            >
              Buyer
            </button>
            <button
              className={`flex-1 py-2 text-center ${activeTab === "seller"
                  ? "border-b-2 border-blue-600 font-bold"
                  : "text-gray-500"
                }`}
              onClick={() => setActiveTab("seller")}
            >
              Seller
            </button>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block mb-2">Username</label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block mb-2">Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block mb-2">Password</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block mb-2">Confirm Password</label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition-colors"
            >
              Register
            </button>
          </form>

          <p className="text-center text-sm text-gray-500 mt-4">
            Already have an account?{" "}
            <Link to="/login" className="text-blue-600 hover:underline">Login</Link>
          </p>
        </div>
      </main>
      <Footer />
    </div>
  );
}