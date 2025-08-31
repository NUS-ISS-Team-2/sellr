import React from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";

export default function NotFoundPage() {
  return (
    <div className="flex flex-col min-h-screen bg-blue-600">
      <Header />

      <main className="flex-1 container mx-auto px-6 py-10 flex flex-col items-center justify-center text-center">
        <h1 className="text-6xl font-bold text-white mb-4">404</h1>
        <h2 className="text-2xl font-semibold text-white mb-6">
          Oops! Page not found.
        </h2>
        <p className="text-white mb-6">
          The page you are looking for does not exist or has been moved.
        </p>
      </main>

      <Footer />
    </div>
  );
}
