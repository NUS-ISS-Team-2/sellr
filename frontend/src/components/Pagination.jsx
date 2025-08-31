export default function Pagination({ page, setPage, totalPages }) {
  return (
    <div className="flex justify-center items-center mt-6 space-x-2">
      <button
        disabled={page === 1}
        onClick={() => setPage(page - 1)}
        className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50 hover:bg-gray-300"
      >
        Prev
      </button>
      <span className="text-gray-700 font-medium">
        {page} / {totalPages}
      </span>
      <button
        disabled={page === totalPages}
        onClick={() => setPage(page + 1)}
        className="px-3 py-1 bg-gray-200 rounded disabled:opacity-50 hover:bg-gray-300"
      >
        Next
      </button>
    </div>
  );
}
