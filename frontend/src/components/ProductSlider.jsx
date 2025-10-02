import React, { useRef, useEffect } from "react";
import { Link } from "react-router-dom";

export default function ProductSlider({ products, scrollSpeed = 1 }) {
  const sliderRef = useRef(null);

  // Helper to truncate text
  const truncate = (text, maxLength) => {
    if (!text) return "";
    return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
  };

  // Duplicate products for seamless scrolling if more than 1
  const loopProducts = products.length >= 4 ? [...products, ...products] : products;

  useEffect(() => {
    const slider = sliderRef.current;

    // Only start auto-scroll if there are 4 or more products
    if (!slider || products.length < 4) return;

    const interval = setInterval(() => {
      slider.scrollLeft += scrollSpeed;

      // Reset scrollLeft to start when reaching half the scroll width
      if (slider.scrollLeft >= slider.scrollWidth / 2) {
        slider.scrollLeft = 0;
      }
    }, 20);

    return () => clearInterval(interval);
  }, [scrollSpeed, products.length]);

  return (
    <div className="w-full">
      <h2 className="text-2xl font-bold mb-4">Featured Products</h2>

      {/* Horizontal scroll container */}
      <div
        ref={sliderRef}
        className="flex space-x-4 overflow-x-auto scrollbar-hide pb-2"
      >
        {loopProducts.map((product, index) => (
          <Link
            key={product.id ?? index}
            to={`/products/${product.id}`}
            className="shrink-0 min-w-[250px] bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:shadow-lg transition block no-underline text-inherit focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500"
            title={product.name}
          >
            <img
              src={product.imageUrl}
              alt={product.name}
              className="w-full h-48 object-cover"
            />
            <div className="p-4">
              <h4 className="font-semibold text-lg">{product.name}</h4>
              <p className="text-gray-600">{truncate(product.description, 80)}</p>
              <p className="text-gray-800 font-bold mt-2">
                ${product.price.toLocaleString()}
              </p>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
