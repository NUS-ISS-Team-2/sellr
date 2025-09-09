// ProductGrid.jsx
import { Link } from "react-router-dom";

export default function ProductGrid({ products = [] }) {
  const truncate = (text, maxLength) =>
    !text ? "" : text.length > maxLength ? text.substring(0, maxLength) + "..." : text;

  return (
    <div className="w-full">
      <h2 className="text-2xl font-bold mb-4">Browse Products</h2>

      {/* Responsive grid that wraps into multiple rows */}
      <div className="grid gap-4 grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
        {products.map((product, idx) => (
          <Link
            key={product.id ?? idx}
            to={`/products/${product.id}`}  // 👈 route to ProductDetail
            className="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:shadow-lg transition block"
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
                ${Number(product.price ?? 0).toLocaleString()}
              </p>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
