import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";

export default function LogoutPage() {
  const navigate = useNavigate();
  const [countdown, setCountdown] = useState(3); // 3 seconds

  useEffect(() => {
    // Countdown interval
    const interval = setInterval(() => {
      setCountdown((prev) => prev - 1);
    }, 1000);

    // Redirect when countdown reaches 0
    if (countdown <= 0) {
      navigate("/");
    }

    return () => clearInterval(interval);
  }, [countdown, navigate]);

  return (
    <div className="flex flex-col min-h-screen bg-blue-600">
      <Header />

      <main className="flex-1 container mx-auto px-6 py-10 flex flex-col items-center justify-center text-center">
        <h1 className="text-6xl font-bold text-white mb-4">Logged Out</h1>
        <h2 className="text-2xl font-semibold text-white mb-6">
          You have been logged out successfully. See you again! 
        </h2>
        <p className="text-white mb-6">
          Redirecting back to the main page in <strong>{countdown}</strong>{" "}
          second{countdown !== 1 ? "s" : ""}...
        </p>
      </main>

      <Footer />
    </div>
  );
}
