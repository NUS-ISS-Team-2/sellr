import { useEffect, useState } from "react";
import { API_BASE_URL } from "../config";

export default function CategoriesList({ setCategory }) {
  const [categories, setCategories] = useState(["All"]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const res = await fetch(`${API_BASE_URL}/products/categories`);
        if (!res.ok) throw new Error("Failed to fetch categories");

        const data = await res.json();
        setCategories(["All", ...data]); // Add "All" at the beginning
      } catch (err) {
        console.error("Error fetching categories:", err);
      }
    };

    fetchCategories();
  }, []);

  return (
    <ul className="space-y-2">
      {categories.map((cat) => (
        <li
          key={cat}
          onClick={() => setCategory(cat === "All" ? null : cat)}
          className="cursor-pointer p-2 rounded hover:bg-blue-50 hover:text-blue-600 font-medium"
        >
          {cat}
        </li>
      ))}
    </ul>
  );
}
