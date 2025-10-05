import { useState, useContext } from "react";
import { useLocation, useNavigate, useParams, Link } from "react-router-dom";
import { UserContext } from "../context/UserContext";
import Header from "../components/Header";
import { createProductReview } from "../services/reviewService";

function StarInput({ value, onChange, size = 24 }) {
  const stars = [1,2,3,4,5];
  return (
    <div className="flex items-center gap-1">
      {stars.map((s) => (
        <button
          type="button"
          key={s}
          onClick={() => onChange(s)}
          aria-label={`${s} star${s>1?"s":""}`}
          className="focus:outline-none"
          title={`${s} star${s>1?"s":""}`}
        >
          <svg
            viewBox="0 0 24 24"
            width={size}
            height={size}
            className={s <= value ? "fill-yellow-400 stroke-yellow-500" : "fill-none stroke-gray-300"}
            strokeWidth="1.5"
          >
            <path d="M12 17.27l6.18 3.73-1.64-7.03L21 9.24l-7.19-.61L12 2 10.19 8.63 3 9.24l4.46 4.73L5.82 21z"/>
          </svg>
        </button>
      ))}
    </div>
  );
}

export default function AddProductReviewPage() {
  const { id: productId } = useParams(); // route: /products/:id/review
  const navigate = useNavigate();
  const { state } = useLocation(); // { productName }
  const { userId } = useContext(UserContext);

  const [rating, setRating] = useState(5);
  const [description, setDescription] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [err, setErr] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    if (!userId) {
      setErr("Please sign in to submit a review.");
      return;
    }
    if (rating < 1 || rating > 5) {
      setErr("Rating must be between 1 and 5.");
      return;
    }
    setErr("");
    setSubmitting(true);
    try {
      await createProductReview({
        userId,
        productId,
        rating,
        description: description?.trim() || ""
      });
      // back to product page
      navigate(`/products/${productId}`, { replace: true });
    } catch (e) {
      setErr(e?.response?.data?.message || "Failed to submit review.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <>
      <Header />
      <div className="max-w-2xl mx-auto px-3 py-6">
        <div className="mb-4">
          <Link to={`/products/${productId}`} className="text-sm text-blue-600 hover:underline">← Back to product</Link>
        </div>

        <h1 className="text-2xl font-semibold">Add Review</h1>
        <p className="text-gray-600 mt-1">
          {state?.productName ? `For: ${state.productName}` : `Product ID: ${productId}`}
        </p>

        {err && (
          <div className="mt-3 rounded-lg border border-red-200 bg-red-50 text-red-700 p-3">{err}</div>
        )}

        <form onSubmit={handleSubmit} className="mt-5 space-y-5">
          <div>
            <label className="block text-sm font-medium mb-1">Rating</label>
            <StarInput value={rating} onChange={setRating} />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Description (optional)</label>
            <textarea
              rows={6}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-200"
              placeholder="Share what you liked or didn't like…"
            />
            <div className="text-xs text-gray-500 mt-1">{description.length}/1000</div>
          </div>

          <button
            type="submit"
            disabled={submitting}
            className={`inline-flex items-center gap-2 rounded-lg px-4 py-2 text-white ${submitting ? "bg-gray-400" : "bg-blue-600 hover:bg-blue-700"}`}
          >
            {submitting ? "Submitting…" : "Submit Review"}
          </button>
        </form>
      </div>
    </>
  );
}
