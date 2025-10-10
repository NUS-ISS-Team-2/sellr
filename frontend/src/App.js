import React from "react";
import MainPage from "./pages/MainPage";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import { ContextProvider } from "./context/UserContext";
import NotFoundPage from "./pages/NotFoundPage";
import ProductsPage from "./pages/ProductsPage";
import ProductDetailPage from "./pages/ProductDetailPage";
import CartPage from "./pages/CartPage";
import { CartProvider } from "./context/CartContext";
import OrderCreatedPage from "./pages/OrderCreatedPage";
import OrdersPage from "./pages/OrdersPage";
import CheckoutPage from "./pages/CheckoutPage";
import ProductManagementPage from "./pages/ProductManagementPage";
import OrderManagementPage from "./pages/OrderManagementPage";
import WishlistPage from "./pages/WishlistPage";
import UsersPage from "./pages/UserPage";
import AddProductReviewPage from "./pages/AddProductReviewPage";
import ContactUsPage from "./pages/ContactUsPage";
import LogoutPage from "./pages/LogoutPage";
import ProtectedRoute from "./context/ProtectedRoutes";
import DisputePage from "./pages/DisputePage";

export default function App() {
  return (
    <ContextProvider>
      <CartProvider>
      <Router basename="/">
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/wishlist" element={<WishlistPage />} />
          <Route path="/products/:id/review" element={<AddProductReviewPage />} />
          <Route path="/contact" element={<ContactUsPage />} />
          <Route path="/success-logout" element={<LogoutPage />} />


          <Route
            path="/product-management"
            element={
              <ProtectedRoute allowedRoles={["SELLER", "ADMIN"]}>
                <ProductManagementPage />
              </ProtectedRoute>
            }
          />



          <Route
            path="/disputes"
            element={
              <ProtectedRoute allowedRoles={["SELLER"]}>
                <DisputePage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/users"
            element={
              <ProtectedRoute allowedRoles={["ADMIN"]}>
                <UsersPage />
              </ProtectedRoute>
            }
          />
          
          <Route
            path="/manageorders"
            element={
              <ProtectedRoute allowedRoles={["SELLER", "ADMIN"]}>
                <OrderManagementPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/order-created"
            element={
              <ProtectedRoute allowedRoles={["BUYER"]}>
                <OrderCreatedPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/myorders"
            element={
              <ProtectedRoute allowedRoles={["BUYER"]}>
                <OrdersPage />
              </ProtectedRoute>
            }
          />


          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Router>
      </CartProvider>
    </ContextProvider>
  );
}