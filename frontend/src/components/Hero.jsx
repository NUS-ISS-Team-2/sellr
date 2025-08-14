import React from "react";

export default function Hero() {
  return (
    <section className="bg-blue-600 text-white text-center py-20 px-4">
      <h2 className="text-5xl font-bold mb-4">Welcome</h2>
      <p className="text-lg mb-6">Shop the latest products at unbeatable prices.</p>
      <a
        href="/shop"
        className="bg-orange-500 hover:bg-orange-600 px-6 py-3 rounded-lg font-semibold transition"
      >
        Shop Now
      </a>
    </section>
  );
}