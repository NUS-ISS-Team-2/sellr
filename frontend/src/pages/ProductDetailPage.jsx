// src/pages/ProductDetail.jsx
import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { getProductById, getRelatedProducts, addToCart } from "../services/productService";
import Header from "../components/Header";


const currency = new Intl.NumberFormat("en-SG", {
  style: "currency",
  currency: "SGD",
});

const PLACEHOLDER_IMG =
  "data:image/svg+xml;utf8," +
  encodeURIComponent(
    `<svg xmlns='http://www.w3.org/2000/svg' width='800' height='600'>
      <rect width='100%' height='100%' fill='#f3f4f6'/>
      <text x='50%' y='50%' dominant-baseline='middle' text-anchor='middle'
        fill='#9ca3af' font-family='Arial' font-size='20'>
        No Image
      </text>
    </svg>`
  );

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [reloading, setReloading] = useState(false);
  const [error, setError] = useState("");
  const [product, setProduct] = useState(null);
  const [qty, setQty] = useState(1);
  const [related, setRelated] = useState([]);
  const [userId, setUserId] = useState(() => sessionStorage.getItem("userId"));


  const inStock = useMemo(() => Number(product?.stock ?? 0) > 0, [product]);

  useEffect(() => {
    let ignore = false;
    async function load() {
      setLoading(true);
      setError("");
      try {
        const p = await getProductById(id);
        if (ignore) return;
        setProduct(p);
        setLoading(false);
        if (p?.category) {
          getRelatedProducts(p.category, id, 8)
            .then((r) => !ignore && setRelated(r))
            .catch(() => {});
        } else {
          setRelated([]);
        }
      } catch (e) {
        if (ignore) return;
        setError(e?.response?.data?.message || "Failed to load product.");
        setLoading(false);
      }
    }
    load();
    return () => {
      ignore = true;
    };
  }, [id]);

  const reload = async () => {
    setReloading(true);
    try {
      const p = await getProductById(id);
      setProduct(p);
      setError("");
    } catch (e) {
      setError(e?.response?.data?.message || "Failed to reload product.");
    } finally {
      setReloading(false);
    }
  };

  const clampQty = (v) => {
    const n = Number(v);
    if (!Number.isFinite(n) || n < 1) return 1;
    if (product?.stock && n > product.stock) return product.stock;
    return n;
  };

  const addToCartClick = async () => {
    if (!userId) {
      alert("You need to be logged in to add items to your cart.");
      return;
    }

    try {
      const cart = await addToCart({
        userId,
        productId: product.id,
        quantity: qty,
      });
      console.log("Cart updated:", cart);
      alert(`Added ${qty} × ${product.name} to cart!`);
    } catch (err) {
      console.error("Failed to add to cart:", err);
      alert("Could not add product to cart. Try again.");
    }
  };

  return (
    <>
    <Header />
    <div className="max-w-6xl mx-auto px-3 py-4">
      {/* Breadcrumb + Back */}
      <div className="flex items-center gap-2 mb-3">
        <button
          onClick={() => navigate(-1)}
          className="inline-flex items-center px-3 py-1.5 rounded-lg border border-gray-200 hover:bg-gray-50"
        >
          ← Back
        </button>
        <nav className="text-sm text-gray-500">
          <Link to="/" className="hover:text-gray-700">Home</Link>
          <span className="mx-1">/</span>
          <Link to="/products" className="hover:text-gray-700">Products</Link>
          {product?.category && (
            <>
              <span className="mx-1">/</span>
              <Link
                to={`/products?category=${encodeURIComponent(product.category)}`}
                className="hover:text-gray-700"
              >
                {product.category}
              </Link>
            </>
          )}
          <span className="mx-1">/</span>
          <span className="text-gray-700">{loading ? "…" : (product?.name ?? "Product")}</span>
        </nav>
      </div>

      {/* Error */}
      {error && (
        <div className="mb-3 rounded-lg border border-red-200 bg-red-50 text-red-700 p-3 flex items-center justify-between">
          <span>{error}</span>
          <button
            onClick={reload}
            className="px-3 py-1.5 rounded-md border border-red-300 hover:bg-red-100 disabled:opacity-50"
            disabled={reloading}
          >
            {reloading ? "Reloading…" : "Reload"}
          </button>
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Image */}
        <div>
          {loading ? (
            <div className="h-[480px] w-full rounded-2xl bg-gray-100 animate-pulse" />
          ) : (
            <div className="rounded-2xl overflow-hidden shadow">
              <img
                src={product?.imageUrl || PLACEHOLDER_IMG}
                alt={product?.name || "Product image"}
                className="w-full max-h-[520px] object-cover bg-white"
              />
            </div>
          )}
        </div>

        {/* Info */}
        <div>
          {loading ? (
            <div className="space-y-3">
              <div className="h-9 w-2/3 bg-gray-100 rounded animate-pulse" />
              <div className="h-6 w-1/3 bg-gray-100 rounded animate-pulse" />
              <div className="h-4 w-11/12 bg-gray-100 rounded animate-pulse" />
              <div className="h-4 w-10/12 bg-gray-100 rounded animate-pulse" />
              <div className="h-12 w-1/2 bg-gray-100 rounded animate-pulse" />
            </div>
          ) : (
            <>
              <h1 className="text-3xl font-semibold">{product?.name}</h1>

              <div className="mt-2 flex flex-wrap items-center gap-2">
                {product?.category && (
                  <span className="inline-block text-sm px-2 py-1 rounded-full border border-gray-300">
                    {product.category}
                  </span>
                )}
                <span
                  className={[
                    "inline-block text-sm px-2 py-1 rounded-full",
                    inStock
                      ? "bg-green-100 text-green-700"
                      : "bg-gray-100 text-gray-500",
                  ].join(" ")}
                >
                  {inStock ? `In Stock: ${product?.stock}` : "Out of Stock"}
                </span>
              </div>

              <div className="mt-4 text-2xl font-bold">
                {currency.format(product?.price ?? 0)}
              </div>

              <p className="mt-3 text-gray-700 whitespace-pre-wrap">
                {product?.description || "No description provided."}
              </p>

              {/* Qty + Add to cart */}
              <div className="mt-5 flex flex-col sm:flex-row gap-3 items-stretch sm:items-center">
                <div className="flex items-center gap-2">
                  <label className="text-sm text-gray-600">Qty</label>
                  <input
                    type="number"
                    min={1}
                    max={product?.stock ?? undefined}
                    value={qty}
                    onChange={(e) => setQty(clampQty(e.target.value))}
                    className="w-28 rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                    disabled={!inStock}
                  />
                </div>

                <button
                  onClick={addToCartClick}
                  disabled={!inStock || reloading}
                  className="inline-flex justify-center items-center gap-2 rounded-2xl px-5 py-3 font-medium text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-50 shadow"
                >
                  Add to Cart
                </button>
              </div>

              <div className="mt-4 flex gap-2">
                <Link to="/products" className="text-blue-600 hover:underline">
                  Continue Shopping
                </Link>
                {product?.category && (
                  <Link
                    to={`/products?category=${encodeURIComponent(product.category)}`}
                    className="text-blue-600 hover:underline"
                  >
                    More in {product.category}
                  </Link>
                )}
              </div>
            </>
          )}
        </div>
      </div>

      {/* Related */}
      {!loading && related.length > 0 && (
        <div className="mt-10">
          <h2 className="text-lg font-semibold mb-3">You might also like</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
            {related.map((p) => (
              <Link
                key={p.id}
                to={`/products/${p.id}`}
                className="rounded-2xl overflow-hidden border border-gray-200 hover:shadow transition-shadow"
                title={p.name}
              >
                <img
                  src={p.imageUrl || PLACEHOLDER_IMG}
                  alt={p.name}
                  className="h-40 w-full object-cover bg-white"
                />
                <div className="p-3">
                  <div className="font-medium truncate">{p.name}</div>
                  <div className="text-sm text-gray-600">
                    {currency.format(p.price ?? 0)}
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </div>
      )}
    </div>
    </>
  );
}
