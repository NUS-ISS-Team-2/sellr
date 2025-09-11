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

export default function App() {
  return (
    <ContextProvider>
      <CartProvider>
      <Router basename="/app">
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/products/:id" element={<ProductDetailPage />} />
          <Route path="/orderCreated" element={<OrderCreatedPage/>} />
          <Route path="/myorders" element={<OrdersPage/>} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Router>
      </CartProvider>
    </ContextProvider>
  );
}