import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000,
});

export async function getProductById(id) {
  const { data } = await API.get(`/products/${id}`);
  return data;
}

export async function getRelatedProducts(category, excludeId, limit = 8) {
  const { data } = await API.get(`/products/search`, {
    params: { category, size: limit, page: 0, sort: "createdAt,desc" },
  });
  const items = data?.content ?? data ?? [];
  return items.filter((p) => String(p.id) !== String(excludeId));
}

export async function searchProducts({ q, category, page = 0, size = 20, sort = "createdAt,desc", signal }) {
  const { data } = await API.get("/products/search", {
    signal,
    params: {
      q: q || undefined,
      category: category || undefined,
      page: Math.max(0, page),
      size,
      sort,
    },
  });
  return data;
}

export async function addToCart({ userId, productId, quantity }) {
  const { data } = await API.post("/cart/add", {
    userId,
    productId,
    quantity,
  });
  return data; // this should be your CartDTO
}