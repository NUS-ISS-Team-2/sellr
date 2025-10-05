import axios from "axios";
import { API_BASE_URL } from "../config";

const API = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

export async function getProductReviews(productId, page = 0, size = 10) {
  const res = await API.get(`/reviews/product/${productId}`, {
    params: { page, size, sort: "dateCreated,desc" },
  });
  return res?.data;
}

export async function createProductReview(payload) {
  const res = await API.post("/reviews", payload);
  return res?.data;
}