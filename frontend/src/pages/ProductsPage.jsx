import Header from "../components/Header";

import { useState, useEffect } from "react";
import axios from "axios";
import SearchBar from "../components/SearchBar";
import CategoriesList from "../components/CategoriesList";
import ProductList from "../components/ProductList";
import Pagination from "../components/Pagination";
import ProductSlider from "../components/ProductSlider";

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState(null);
  const [page, setPage] = useState(1);

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/products", {
        params: { search, category, page }
      })
      .then((res) => setProducts(res.data))
      .catch((err) => console.error("Error fetching products:", err));
  }, [search, category, page]);

  return (
    <>
    <Header />
    <div className="min-h-screen bg-gray-100">
      <div className="flex justify-center p-6 bg-blue-600 shadow">
        <SearchBar search={search} setSearch={setSearch} />
      </div>
      {/* Layout: sidebar + products */}
      <div className="flex max-w-7xl mx-auto p-4 gap-6">
        <aside className="w-1/5 bg-white p-4 rounded shadow">
          <CategoriesList setCategory={setCategory} />
        </aside>

        <main className="flex-1">
          <ProductSlider products={products} />
          <Pagination
            page={page}
            setPage={setPage}
            totalPages={products.totalPages || 1}
          />
        </main>
      </div>
    </div>
    </>
  );
}
