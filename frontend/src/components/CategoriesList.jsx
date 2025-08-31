const categories = ["All", "Electronics", "Clothing", "Books"];

export default function CategoriesList({ setCategory }) {
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
