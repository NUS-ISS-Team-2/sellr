import React, { useMemo, useState, useContext, useEffect } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { UserContext } from "../context/UserContext";
import { fetchWishlist, removeWishlistItem } from "../services/wishlistService";
import { useCart } from "../context/CartContext";

function mapItemToRow(item) {
  const p = item?.product ?? item ?? {};
  return {
    id: p.productId || p.id || item?.productId || item?.id,
    name: p.name ?? "(Unknown product)",
    price: p.price ?? 0,
    imageUrl: p.imageUrl ?? null,
  };
}

export default function WishlistPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(5);

  const { addToCart } = useCart();
  const { userId } = useContext(UserContext);

  useEffect(() => {
    if (!userId) {
      setLoading(false);
      return;
    }
    const controller = new AbortController();
    (async () => {
      try {
        setLoading(true);
        const dto = await fetchWishlist(userId, { signal: controller.signal });
        const rows = (dto?.items ?? []).map(mapItemToRow).filter((r) => !!r.id);
        setItems(rows);
      } catch (err) {
        if (err.name !== "CanceledError" && err.name !== "AbortError") {
          console.error("Failed to fetch wishlist:", err);
        }
      } finally {
        setLoading(false);
      }
    })();
    return () => controller.abort();
  }, [userId]);

  async function handleRemoveItem(userId, productId) {
    try {
      setLoading(true); 
      const dto = await removeWishlistItem(userId, productId);
      const rows = (dto?.items ?? []).map(mapItemToRow).filter((r) => !!r.id);
      setItems(rows);
    } catch (err) {
      console.error("Failed to remove item from wishlist:", err);
    } finally {
      setLoading(false);
    }
  }

  async function handleAddToCart(userId, item) {
    try {
      await addToCart(userId, item);
      alert("Added to cart!");
    } catch (err) {
      console.error("Failed to add item to cart:", err);
    }
  } 


  const totalItems = items.length;

  const pagedItems = useMemo(() => {
    const start = (page - 1) * pageSize;
    return items.slice(start, start + pageSize);
  }, [items, page, pageSize]);

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />

      <main className="flex-1 container mx-auto px-6 py-10">
        <h1 className="text-3xl font-bold text-center mb-8">My Wishlist</h1>

        <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr className="text-left text-sm font-semibold text-gray-700">
                  <th className="px-4 py-3 w-12"></th>
                  <th className="px-4 py-3 w-20">Image</th>
                  <th className="px-4 py-3">Product</th>
                  <th className="px-4 py-3 w-32">Price</th>
                  <th className="px-4 py-3 w-40 text-right">Action</th>
                </tr>
              </thead>

              <tbody className="divide-y divide-gray-100">
                {loading ? (
                  <tr>
                    <td colSpan={6} className="px-4 py-10 text-center">
                      <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
                    </td>
                  </tr>
                ) : pagedItems.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="px-4 py-10 text-center text-gray-500">
                      No items in your wishlist.
                    </td>
                  </tr>
                ) : (
                  pagedItems.map((item) => {
                    return (
                      <tr key={item.id} className="text-sm">
                        <td className="px-4 py-4 align-middle">
                          <button
                            onClick={() => handleRemoveItem(userId, item.id)}
                            className="p-2 rounded hover:bg-red-50 text-gray-500 hover:text-red-600 transition"
                            aria-label={`Remove ${item.name} from wishlist`}
                            title="Delete"
                          >
                            <svg
                              className="w-5 h-5"
                              fill="none"
                              viewBox="0 0 24 24"
                              strokeWidth="1.5"
                              stroke="currentColor"
                              aria-hidden="true"
                            >
                              <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                d="M6 7h12M9 7V5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2m1 0v12a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V7m3 4v6m4-6v6m4-6v6"
                              />
                            </svg>
                          </button>
                        </td>
                        <td className="px-4 py-4 align-middle">
                          <img
                            src={item.imageUrl || "/placeholder.png"}
                            alt={item.name}
                            className="w-16 h-16 object-cover rounded-md border border-gray-200"
                          />
                        </td>
                        <td className="px-4 py-4 align-middle">
                          <div className="font-medium text-gray-900 line-clamp-2">{item.name}</div>
                        </td>
                        <td className="px-4 py-4 align-middle">
                          <span className="font-semibold text-gray-900">
                            ${Number(item.price ?? 0).toFixed(2)}
                          </span>
                        </td>
                        <td className="px-4 py-4 align-middle text-right">
                          <button
                            onClick={() => handleAddToCart(userId, item)}
                            className="inline-flex items-center justify-center px-4 py-2 rounded-md text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition"
                          >
                            Add to cart
                          </button>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div className="flex flex-col sm:flex-row items-center justify-between gap-3 px-4 py-3 bg-gray-50 border-t border-gray-200">
            <div className="flex items-center gap-2 text-sm">
              <span>Rows per page:</span>
              <select
                className="border border-gray-300 rounded-md px-2 py-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={pageSize}
                onChange={(e) => {
                  setPageSize(Number(e.target.value));
                  setPage(1);
                }}
              >
                {[5, 10, 20].map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </select>
              <span className="text-gray-500">
                {totalItems === 0
                  ? "0–0 of 0"
                  : `${(page - 1) * pageSize + 1}–${Math.min(page * pageSize, totalItems)} of ${totalItems}`}
              </span>
            </div>

            <div className="flex items-center gap-1">
              <button
                onClick={() => setPage(1)}
                disabled={page === 1}
                className="px-3 py-1.5 rounded border border-gray-300 bg-white text-sm hover:bg-gray-100 disabled:opacity-50"
                aria-label="First page"
              >
                «
              </button>
              <button
                onClick={() => setPage((p) => Math.max(1, p - 1))}
                disabled={page === 1}
                className="px-3 py-1.5 rounded border border-gray-300 bg-white text-sm hover:bg-gray-100 disabled:opacity-50"
                aria-label="Previous page"
              >
                Prev
              </button>

              <span className="px-3 py-1.5 text-sm">
                Page <span className="font-semibold">{page}</span> of {Math.max(1, Math.ceil(totalItems / pageSize))}
              </span>

              <button
                onClick={() => setPage((p) => Math.min(Math.ceil(totalItems / pageSize) || 1, p + 1))}
                disabled={page >= (Math.ceil(totalItems / pageSize) || 1)}
                className="px-3 py-1.5 rounded border border-gray-300 bg-white text-sm hover:bg-gray-100 disabled:opacity-50"
                aria-label="Next page"
              >
                Next
              </button>
              <button
                onClick={() => setPage(Math.ceil(totalItems / pageSize) || 1)}
                disabled={page >= (Math.ceil(totalItems / pageSize) || 1)}
                className="px-3 py-1.5 rounded border border-gray-300 bg-white text-sm hover:bg-gray-100 disabled:opacity-50"
                aria-label="Last page"
              >
                »
              </button>
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
}