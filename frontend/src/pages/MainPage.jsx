
// import React, { useEffect, useState } from "react";
// import axios from "axios";
// import Hero from "../components/Hero";
// import ProductCard from "../components/ProductCard";
// import Footer from "../components/Footer";
// import Header from "../components/Header";
// import ProductForm from "../components/ProductForm";
// import ProductView from "../components/ProductView";


// export default function MainPage() {
//   const [products, setProducts] = useState([]);
//   const [showForm, setShowForm] = useState(false);
//   const [editingProduct, setEditingProduct] = useState(null);
//   const [viewingProduct, setViewingProduct] = useState(null);

//   // Fetch products
//   useEffect(() => {
//     axios
//       .get("http://localhost:8080/api/products")
//       .then((res) => setProducts(res.data))
//       .catch((err) => console.error("Error fetching products:", err));
//   }, []);

//   // After create/update
//   const handleProductAdded = (newProduct) => {
//     setProducts((prev) => {
//       const exists = prev.find((p) => p.id === newProduct.id);
//       return exists
//         ? prev.map((p) => (p.id === newProduct.id ? newProduct : p))
//         : [...prev, newProduct];
//     });
//     setShowForm(false);
//     setEditingProduct(null);
//   };

//   // Delete
//   const handleDelete = async (id) => {
//     if (!window.confirm("Are you sure you want to delete this product?")) return;
//     try {
//       await axios.delete(`http://localhost:8080/api/products/${id}`);
//       setProducts((prev) => prev.filter((p) => p.id !== id));
//     } catch (err) {
//       console.error("Failed to delete product:", err);
//     }
//   };

//   return (
//     <div className="flex flex-col min-h-screen bg-gray-50">
//       <Header />
//       <Hero />

//       <main className="flex-1 container mx-auto px-6 py-10">
//         <div className="flex justify-between items-center mb-6">
//           <h3 className="text-2xl font-bold">Featured Products</h3>
//           <button
//             className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
//             onClick={() => {
//               setEditingProduct(null);
//               setShowForm(true);
//             }}
//           >
//             Create Product
//           </button>
//         </div>

//         {/* Modal: Create/Edit */}
//         {showForm && (
//           <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-20">
//             <div className="bg-white p-6 rounded shadow-lg w-full max-w-md relative">
//               <button
//                 className="absolute top-2 right-2 text-gray-600 hover:text-gray-900"
//                 onClick={() => {
//                   setShowForm(false);
//                   setEditingProduct(null);
//                 }}
//               >
//                 ✕
//               </button>
//               <ProductForm
//                 onProductAdded={handleProductAdded}
//                 initialData={editingProduct}
//               />
//             </div>
//           </div>
//         )}

//         {/* Modal: View Details */}
//         {viewingProduct && (
//           <ProductView
//             product={viewingProduct}
//             onClose={() => setViewingProduct(null)}
//           />
//         )}


//         <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 mt-6">
//           {products.map((product) => (
//             <ProductCard
//               key={product.id}
//               product={product}
//               onView={setViewingProduct}
//               onEdit={(p) => {
//                 setEditingProduct(p);
//                 setShowForm(true);
//               }}
//               onDelete={handleDelete}
//             />
//           ))}
//         </div>
//       </main>

//       <Footer />
//     </div>
//   );
// }


import React, { useEffect, useState } from "react";
import axios from "axios";
import Hero from "../components/Hero";
import ProductCard from "../components/ProductCard";
import Footer from "../components/Footer";
import Header from "../components/Header";
import ProductForm from "../components/ProductForm";
import ProductView from "../components/ProductView";

export default function MainPage() {
  const [products, setProducts] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [viewingProduct, setViewingProduct] = useState(null);
  const [confirmDelete, setConfirmDelete] = useState(null); // NEW state

  // Fetch products
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/products")
      .then((res) => setProducts(res.data))
      .catch((err) => console.error("Error fetching products:", err));
  }, []);

  // After create/update
  const handleProductAdded = (newProduct) => {
    setProducts((prev) => {
      const exists = prev.find((p) => p.id === newProduct.id);
      return exists
        ? prev.map((p) => (p.id === newProduct.id ? newProduct : p))
        : [...prev, newProduct];
    });
    setShowForm(false);
    setEditingProduct(null);
  };

  // Delete confirmed
  const confirmDeleteProduct = async () => {
    if (!confirmDelete) return;
    try {
      await axios.delete(`http://localhost:8080/api/products/${confirmDelete.id}`);
      setProducts((prev) => prev.filter((p) => p.id !== confirmDelete.id));
    } catch (err) {
      console.error("Failed to delete product:", err);
    } finally {
      setConfirmDelete(null); // Close modal
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Header />
      <Hero />

      <main className="flex-1 container mx-auto px-6 py-10">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-2xl font-bold">Featured Products</h3>
          <button
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
            onClick={() => {
              setEditingProduct(null);
              setShowForm(true);
            }}
          >
            Create Product
          </button>
        </div>

        {/* Modal: Create/Edit */}
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-20">
            <div className="bg-white p-6 rounded shadow-lg w-full max-w-md relative">
              <button
                className="absolute top-2 right-2 text-gray-600 hover:text-gray-900"
                onClick={() => {
                  setShowForm(false);
                  setEditingProduct(null);
                }}
              >
                ✕
              </button>
              <ProductForm
                onProductAdded={handleProductAdded}
                initialData={editingProduct}
              />
            </div>
          </div>
        )}

        {/* Modal: View Details */}
        {viewingProduct && (
          <ProductView
            product={viewingProduct}
            onClose={() => setViewingProduct(null)}
          />
        )}

        {/* Modal: Confirm Delete */}
        {confirmDelete && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-30">
            <div className="bg-white p-6 rounded shadow-lg w-full max-w-sm text-center relative">
              <h3 className="text-lg font-bold mb-4">Delete Product</h3>
              <p className="text-gray-700 mb-6">
                Are you sure you want to delete{" "}
                <span className="font-semibold">{confirmDelete.name}</span>?
              </p>
              <div className="flex justify-center space-x-4">
                <button
                  className="px-4 py-2 rounded bg-gray-200 hover:bg-gray-300"
                  onClick={() => setConfirmDelete(null)}
                >
                  Cancel
                </button>
                <button
                  className="px-4 py-2 rounded bg-red-600 hover:bg-red-700 text-white"
                  onClick={confirmDeleteProduct}
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        )}

        <div className="grid gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 mt-6">
          {products.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              onView={setViewingProduct}
              onEdit={(p) => {
                setEditingProduct(p);
                setShowForm(true);
              }}
              onDelete={(id) => {
                const prod = products.find((p) => p.id === id);
                setConfirmDelete(prod); // open modal instead of window.confirm
              }}
            />
          ))}
        </div>
      </main>

      <Footer />
    </div>
  );
}
