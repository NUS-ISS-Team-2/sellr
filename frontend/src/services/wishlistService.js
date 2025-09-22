import axios from "axios";

const BASE_URL = "http://localhost:8080";

/**
 * Fetch WishlistDTO for the given userId.
 * Expects { userId: string, items: WishlistItemDTO[] }
 */
export async function fetchWishlist(userId, { signal } = {}) {
  if (!userId) throw new Error("fetchWishlist: userId is required");
  const res = await axios.get(`${BASE_URL}/api/wishlist/getWishlist`, {
    params: { userId },
    signal,
  });
  return res.data; // WishlistDTO
}


/**
 * Calls POST /api/wishlist/add with { userId, productId }.
 * Returns the updated WishlistDTO from your backend.
 *
 * @param {string} userId
 * @param {string} productId
 * @param {{ signal?: AbortSignal }} [opts]
 * @returns {Promise<WishlistDTO>}
 */
export async function addToWishlist(userId, productId, opts = {}) {
  if (!userId) throw new Error("addToWishlist: userId is required");
  if (!productId) throw new Error("addToWishlist: productId is required");

  const res = await axios.post(
    `${BASE_URL}/api/wishlist/add`,
    { userId, productId },
    { signal: opts.signal }
  );

  return res.data;
}

export async function removeWishlistItem(userId, productId, opts = {}) {
  if (!userId) throw new Error("removeWishlistItem: userId is required");
  if (!productId) throw new Error("removeWishlistItem: productId is required");

  const res = await axios.delete(
    `${BASE_URL}/api/wishlist/items/${encodeURIComponent(productId)}`,
    {
      params: { userId },           // <-- query string ?userId=...
      signal: opts.signal,
    }
  );
  return res.data; // WishlistDTO
}