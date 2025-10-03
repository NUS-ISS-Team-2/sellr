// src/pages/ProductsPage.jsx
import Header from "../components/Header";
import { useState, useEffect, useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import SearchBar from "../components/SearchBar";
import CategoriesList from "../components/CategoriesList";
import Pagination from "../components/Pagination";
import ProductGrid from "../components/ProductGrid";
import { searchProducts } from "../services/productService";

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

export default function ProductsPage() {
  const query = useQuery();
  const navigate = useNavigate();

  const initialCategory = query.get("category") || null;

  const [search, setSearch] = useState("");
  const [category, setCategory] = useState(initialCategory);
  const [page, setPage] = useState(1);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);

  // Update URL when category changes
  useEffect(() => {
    if (category) {
      navigate(`/products?category=${encodeURIComponent(category)}`, { replace: true });
    } else {
      navigate("/products", { replace: true });
    }
  }, [category, navigate]);

  const debouncedSearch = useMemo(() => search.trim(), [search]);

  useEffect(() => {
    const controller = new AbortController();

    if (debouncedSearch === "" || debouncedSearch.length >= 3) {
      setLoading(true);

      searchProducts({
        q: debouncedSearch,
        category,
        page: Math.max(0, page - 1),
        size: 20,
        sort: "createdAt,desc",
        signal: controller.signal,
      })
        .then((res) => setData(res))
        .catch((err) => {
          if (err.name !== "CanceledError") console.error("Failed to fetch products:", err);
        })
        .finally(() => setLoading(false));
    }

    return () => controller.abort();
  }, [debouncedSearch, category, page]);

  const products = data?.content || [];
  const totalPages = data?.totalPages || 1;

  return (
    <>
      <Header />
      <div className="min-h-screen bg-gray-100">
        <div className="flex justify-center p-6 bg-blue-600 shadow">
          <SearchBar search={search} setSearch={setSearch} />
        </div>

        <div className="flex max-w-7xl mx-auto p-4 gap-6">
          {/* Sidebar */}
          <aside className="w-1/5 bg-white p-4 rounded shadow">
            <CategoriesList setCategory={setCategory} />
          </aside>

          {/* Main */}
          <main className="flex-1">
            {/* Filter indicator */}
            {(category || search) && (
              <div className="mb-4 text-sm text-gray-600">
                <span>Filtering by: </span>
                {category && (
                  <span className="inline-block px-2 py-1 mr-2 bg-blue-100 text-blue-700 rounded">
                    Category: {category}
                  </span>
                )}
                {search && (
                  <span className="inline-block px-2 py-1 mr-2 bg-green-100 text-green-700 rounded">
                    Search: "{search}"
                  </span>
                )}
              </div>
            )}

            {loading && <div className="p-4">Loading…</div>}

            {!loading && products.length === 0 && (
              <div className="p-4 text-gray-500">No products found.</div>
            )}

            <ProductGrid products={products} />

            <Pagination
              page={page}
              setPage={setPage}
              totalPages={totalPages}
            />
          </main>
        </div>
      </div>
    </>
  );
}
