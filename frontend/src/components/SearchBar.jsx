export default function SearchBar({ search, setSearch }) {
  return (
    <input
      type="text"
      placeholder="Search products..."
      value={search}
      onChange={(e) => setSearch(e.target.value)}
      className="w-80 px-4 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-300"
    />
  );
}
