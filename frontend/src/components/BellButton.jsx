import { Link } from "react-router-dom";

export default function BellButton({ count = 0, to = "/manageorders" }) {
  return (
    <Link to={to} className="relative inline-block">
      <button className="p-2 rounded-full hover:bg-blue-700 transition relative">
        {/* Simple Bell SVG */}
        <svg
          xmlns="http://www.w3.org/2000/svg"
          className="h-6 w-6 text-white"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          strokeWidth={2}
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 00-5-5.917V4a1 1 0 10-2 0v1.083A6 6 0 006 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0a3 3 0 11-6 0h6z"
          />
        </svg>

        {/* Badge */}
        {count > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs font-bold rounded-full px-1.5 animate-pulse">
            {count}
          </span>
        )}
      </button>
    </Link>
  );
}
