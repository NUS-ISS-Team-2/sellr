// src/pages/ProductDetail.jsx
import { useEffect, useState, useContext } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import Header from "../components/Header";
import { UserContext } from "../context/UserContext";
import { useCart } from "../context/CartContext";
import { getProductById, getRelatedProducts } from "../services/productService";
import { addToWishlist, fetchWishlist } from "../services/wishlistService";
import ProductReviews from "../components/ProductReviews";
import Toast from "../components/Toast";


const PLACEHOLDER_IMG =
  "data:image/svg+xml;utf8," +
  encodeURIComponent(
    `<svg xmlns='http://www.w3.org/2000/svg' width='800' height='600'>
      <rect width='100%' height='100%' fill='#f3f4f6'/>
      <text x='50%' y='50%' dominant-baseline='middle' text-anchor='middle'
        fill='#9ca3af' font-family='Arial' font-size='20'>No Image</text>
    </svg>`
  );

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { userId, role } = useContext(UserContext);
  const { cartItems, addToCart, updateCart } = useCart();

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [related, setRelated] = useState([]);
  const [inWishlist, setInWishlist] = useState(false);
  const isStaff = role === "SELLER" || role === "ADMIN";
  const [toast, setToast] = useState(null);

  async function handleAddToWishlist() {
    try {
      await addToWishlist(userId, product.id);
      setInWishlist(true); // mark as added
      setToast({ type: "success", message: "Added to wishlist!" });
    } catch (e) {
      console.error(e);
      setToast({ type: "error", message: "Item already in wishlist." });
    }
  }

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
            .catch(() => setRelated([]));
        }
      } catch (e) {
        if (ignore) return;
        setError(e?.response?.data?.message || "Failed to load product.");
        setLoading(false);
      }
    }
    load();
    return () => { ignore = true; };
  }, [id]);

  useEffect(() => {
    if (!userId || !product) return;
    let ignore = false;
    async function checkWishlist() {
      try {
        const wishlist = await fetchWishlist(userId);
        if (ignore) return;
        const exists = wishlist.items.some(item => item.productId === product.id);
        setInWishlist(exists);
      } catch (e) {
        console.error("Failed to fetch wishlist:", e);
      }
    }
    checkWishlist();
    return () => { ignore = true; };
  }, [userId, product]);

  const cartItem = cartItems.find((item) => item.productId === product?.id);
  const quantity = cartItem?.quantity || 0;
  const inStock = Number(product?.stock ?? 0) > 0;

  return (
    <>
      <Header />
      <div className="max-w-6xl mx-auto px-3 py-4">
        {/* Breadcrumb */}
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

        {(role === "SELLER" || role === "ADMIN") && (
          <div className="bg-yellow-100 border border-yellow-300 text-yellow-800 text-center py-2 rounded-md mx-3 mt-3 mb-10">
            Sellers and administrators do not have purchasing permissions.
          </div>
        )}

        {toast && (
          <Toast
            type={toast.type}
            message={toast.message}
            onClose={() => setToast(null)}
            duration={5000}
          />
        )}

        {/* Error */}
        {error && (
          <div className="mb-3 rounded-lg border border-red-200 bg-red-50 text-red-700 p-3 flex items-center justify-between">
            <span>{error}</span>
            <button
              onClick={() => window.location.reload()}
              className="px-3 py-1.5 rounded-md border border-red-300 hover:bg-red-100"
            >
              Reload
            </button>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Image */}
          <div>
            {loading ? (
              <div className="h-[480px] w-full rounded-2xl bg-gray-100 animate-pulse" />
            ) : (
              <img
                src={product?.imageUrl || PLACEHOLDER_IMG}
                alt={product?.name || "Product image"}
                className="w-full max-h-[520px] object-cover rounded-2xl shadow"
              />
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
              </div>
            ) : (
              <>
                <h1 className="text-3xl font-semibold">{product?.name}</h1>
                <p className="text-gray-700 mt-3">{product?.description || "No description provided."}</p>
                <p className="text-2xl font-bold mt-4">${Number(product?.price ?? 0).toLocaleString()}</p>


                {/* Stock Info */}
                <div className="mt-2 flex items-center gap-2">
                  {inStock ? (
                    <span className="inline-block text-sm px-2 py-1 rounded-full bg-green-100 text-green-700">
                      Stock: {product.lowStock && "⚠️ Low"}
                    </span>
                  ) : (
                    <span className="inline-block text-sm px-2 py-1 rounded-full bg-gray-100 text-gray-500">
                      Out of Stock
                    </span>
                  )}
                </div>



                {/* Cart Controls */}
                <div className="mt-5 flex flex-col gap-2">
                  {quantity === 0 ? (
                    <button
                      onClick={() => {
                        if (!userId) return navigate("/login");
                        addToCart(userId, product);
                      }}
                      disabled={!inStock || isStaff}
                      className={`w-full py-2 rounded text-white ${!inStock || isStaff ? "bg-gray-400 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700"
                        }`}
                    >
                      Add to Cart
                    </button>
                  ) : (
                    <div className="flex items-center justify-start gap-2">
                      <button
                        onClick={() => {
                          if (!userId) return navigate("/login");
                          updateCart(userId, product.id, 1);
                        }}
                        disabled={!inStock || quantity >= product.stock}
                        className={`px-2 py-1 rounded text-white ${inStock && quantity < product.stock
                          ? "bg-green-500 hover:bg-green-600"
                          : "bg-gray-400 cursor-not-allowed"
                          }`}
                      >
                        +
                      </button>
                      <span>{quantity}</span>
                      <button
                        onClick={() => {
                          if (!userId) return navigate("/login");
                          updateCart(userId, product.id, -1);
                        }}
                        disabled={quantity <= 0}
                        className={`px-2 py-1 rounded text-white ${quantity > 0 ? "bg-red-500 hover:bg-red-600" : "bg-gray-400 cursor-not-allowed"
                          }`}
                      >
                        -
                      </button>
                    </div>
                  )}

                  <button
                    type="button"
                    className={`w-full inline-flex items-center justify-center gap-2 rounded-lg border border-rose-300 py-2 font-medium transition
                      ${isStaff || inWishlist
                        ? "bg-gray-400 cursor-not-allowed text-white border-gray-300"
                        : "bg-white text-rose-600 hover:bg-rose-50"
                      }`}
                    onClick={handleAddToWishlist}
                    disabled={isStaff || inWishlist || !userId}
                    aria-label={inWishlist ? "In wishlist" : "Add to wishlist"}
                  >
                    <svg
                      className="h-5 w-5"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="1.8"
                      aria-hidden="true"
                    >
                      <path
                        d="M20.205 4.792a5.5 5.5 0 00-7.778 0L12 5.219l-.427-.427a5.5 5.5 0 10-7.778 7.778l.427.427L12 21.675l7.778-8.678.427-.427a5.5 5.5 0 000-7.778z"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      />
                    </svg>
                    {inWishlist ? "In Wishlist" : "Add to Wishlist"}
                  </button>

                </div>

                {/* Related Links */}
                <div className="mt-4 flex gap-2">
                  <Link to="/products" className="text-blue-600 hover:underline">Continue Shopping</Link>
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

        {/* Reviews */}
        {!loading && product?.id && (
          <ProductReviews productId={product.id} pageSize={5} />
        )}

        {/* Related Products */}
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
                  <img src={p.imageUrl || PLACEHOLDER_IMG} alt={p.name} className="h-40 w-full object-cover" />
                  <div className="p-3">
                    <div className="font-medium truncate">{p.name}</div>
                    <div className="text-sm text-gray-600">${Number(p.price ?? 0).toLocaleString()}</div>
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
