import Header from "../components/Header";
import { useState, useMemo, useEffect } from "react";
import axios from "axios";
import SearchBar from "../components/SearchBar";
import CategoriesList from "../components/CategoriesList";
import Pagination from "../components/Pagination";
import ProductGrid from "../components/ProductGrid";

export default function ProductsPage() {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState(null);
  const [page, setPage] = useState(1);
  const [data, setData] = useState(null);        // paged payload from backend 
  const [loading, setLoading] = useState(false);
  
  // simple debounce (optional)
  const debouncedSearch = useMemo(() => search.trim(), [search]);


  useEffect(() => {
    const controller = new AbortController();
    if (debouncedSearch === "" || debouncedSearch.length >= 3) {
    setLoading(true);

    axios.get("http://localhost:8080/api/products/search", {
      signal: controller.signal,
      params: {
        q: debouncedSearch || undefined,
        category: category || undefined,
        page: Math.max(0, page - 1),
        size: 20,
        sort: "createdAt,desc",
      },
    })
    .then((res) => setData(res.data))
    .catch((err) => {
      if (axios.isCancel(err)) return;
      console.error("Error fetching products:", err);
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
          <aside className="w-1/5 bg-white p-4 rounded shadow">
            <CategoriesList setCategory={setCategory} />
          </aside>

          <main className="flex-1">
            {loading && <div className="p-4">Loading…</div>}

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
