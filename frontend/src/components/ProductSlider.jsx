import React, { useRef, useEffect } from "react";

export default function ProductSlider({ products, scrollSpeed = 1 }) {
  const sliderRef = useRef(null);

  // Truncate helper
  const truncate = (text, maxLength) => {
    if (!text) return "";
    return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
  };

  useEffect(() => {
    const slider = sliderRef.current;
    if (!slider) return;

    const interval = setInterval(() => {
      slider.scrollLeft += scrollSpeed;

      // Reset scrollLeft to start when reaching half the scroll width
      if (slider.scrollLeft >= slider.scrollWidth / 2) {
        slider.scrollLeft = 0;
      }
    }, 20);

    return () => clearInterval(interval);
  }, [scrollSpeed]);

  // Duplicate products array for seamless looping
  const loopProducts = [...products, ...products];

  return (
    <div className="w-full">
      <h2 className="text-2xl font-bold mb-4">Featured Products</h2>

      {/* Horizontal scroll container */}
      <div
        ref={sliderRef}
        className="flex space-x-4 overflow-x-auto scrollbar-hide pb-2"
      >
        {loopProducts.map((product, index) => (
          <div
            key={index}
            className="min-w-[250px] bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:shadow-lg transition"
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
          </div>
        ))}
      </div>
    </div>
  );
}
