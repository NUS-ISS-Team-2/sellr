// src/components/ProductReviews.jsx
import { useEffect, useState } from "react";
import { getProductReviews } from "../services/reviewService";

function StarRating({ value = 0, size = 18 }) {
  const stars = [1, 2, 3, 4, 5];
  return (
    <div className="inline-flex items-center gap-0.5" aria-label={`${value} out of 5`}>
      {stars.map((s) => (
        <svg
          key={s}
          xmlns="http://www.w3.org/2000/svg"
          viewBox="0 0 24 24"
          width={size}
          height={size}
          className={s <= value ? "fill-yellow-400 stroke-yellow-500" : "fill-none stroke-gray-300"}
          strokeWidth="1.5"
        >
          <path d="M12 17.27l6.18 3.73-1.64-7.03L21 9.24l-7.19-.61L12 2 10.19 8.63 3 9.24l4.46 4.73L5.82 21z"/>
        </svg>
      ))}
    </div>
  );
}

function formatDate(dtStr) {
  try {
    const d = new Date(dtStr);
    return new Intl.DateTimeFormat(undefined, {
      year: "numeric",
      month: "short",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    }).format(d);
  } catch {
    return dtStr ?? "";
  }
}

export default function ProductReviews({ productId, pageSize = 5 }) {
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  useEffect(() => {
    let ignore = false;
    async function load() {
      if (!productId) return;
      setLoading(true);
      setErr("");
      try {
        const res = await getProductReviews(productId, page, pageSize);
        if (!ignore) setData(res);
      } catch (e) {
        if (!ignore) setErr(e?.response?.data?.message || "Failed to load reviews.");
      } finally {
        if (!ignore) setLoading(false);
      }
    }
    load();
    return () => { ignore = true; };
  }, [productId, page, pageSize]);

  const content = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;

  return (
    <section className="mt-10">
      <div className="flex items-center justify-between mb-3">
        <h2 className="text-lg font-semibold">Reviews</h2>
        {totalPages > 0 && (
          <div className="text-sm text-gray-500">
            Page {Number(data?.number ?? 0) + 1} of {totalPages}
          </div>
        )}
      </div>

      {/* Loading / Error / Empty */}
      {loading && <div className="rounded-lg border border-gray-200 p-4 animate-pulse">Loading reviews…</div>}
      {!!err && (
        <div className="rounded-lg border border-red-200 bg-red-50 text-red-700 p-3">{err}</div>
      )}
      {!loading && !err && content.length === 0 && (
        <div className="rounded-lg border border-gray-200 p-4 text-gray-600">No reviews yet.</div>
      )}

      {/* List */}
      <ul className="space-y-4">
        {content.map((r) => (
          <li key={r.id} className="border border-gray-200 rounded-xl p-4">
            <div className="flex items-center justify-between">
              <div className="font-medium text-gray-900">
                {r.username || "Anonymous"}
              </div>
              <StarRating value={Number(r.rating ?? 0)} />
            </div>
            {r.description && (
              <p className="mt-2 text-gray-700 whitespace-pre-wrap">{r.description}</p>
            )}
            <div className="mt-2 text-sm text-gray-500">
              {r.createdDate ? `Reviewed on ${formatDate(r.createdDate)}` : null}
            </div>
          </li>
        ))}
      </ul>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="mt-4 flex items-center justify-between">
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={(data?.number ?? 0) <= 0}
            className="px-3 py-1.5 rounded border border-gray-300 disabled:opacity-50"
          >
            ← Prev
          </button>
          <button
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={(data?.number ?? 0) >= totalPages - 1}
            className="px-3 py-1.5 rounded border border-gray-300 disabled:opacity-50"
          >
            Next →
          </button>
        </div>
      )}
    </section>
  );
}
