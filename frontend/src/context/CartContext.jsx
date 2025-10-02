import { createContext, useState, useContext, useEffect } from "react";
import axios from "axios";
import { API_BASE_URL } from "../config";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState(() => {
    const saved = sessionStorage.getItem("cart");
    return saved ? JSON.parse(saved) : [];
  });

  useEffect(() => {
    sessionStorage.setItem("cart", JSON.stringify(cartItems));
  }, [cartItems]);

  const fetchCart = async (userId) => {
    try {
      const res = await axios.get(`${API_BASE_URL}/cart/?userId=${userId}`);
      if (res.status === 200) {
        handleSetCartItems(res.data.items);
        console.log(res.data)
      }
    } catch (err) {
      console.error("Failed to fetch cart:", err);
    }
  };
  
  const handleSetCartItems = (items) => {
    sessionStorage.setItem("cart", JSON.stringify(items));
    setCartItems(items);
  }


  const updateCart = async (userId, productId, qtyChange) => {
    try {
      const existing = cartItems.find(p => p.productId === productId);
      console.log("Existing item:", existing);

      const newQuantity = (existing?.quantity || 0) + qtyChange;

      const res = await axios.put(`${API_BASE_URL}/cart/`, {
        userId,
        productId: existing.productId,
        quantity: newQuantity
      });

      console.log(res.data)

      if (res.status === 200) {
        const updatedItem = res.data.items; 
        handleSetCartItems(updatedItem);
      }
    } catch (err) {
      console.error("Failed to update cart:", err);
    }
  };

  const addToCart = async (userId, item) => {

    console.log(userId, item.id)
    const res = await axios.post(`${API_BASE_URL}/cart/add`, {
      userId,
      productId: item.id,
      quantity: 1
    });

    if (res.status !== 200) {
      console.error("Failed to add to cart:");
      return;
    }

    handleSetCartItems(res.data.items);
  };

  const removeFromCart = async (userId, productId) => {
    try {
      const res = await axios.delete(`${API_BASE_URL}/cart/remove?userId=${userId}&productId=${productId}`);
      if (res.status === 200) {
        fetchCart(userId);
      }
    } catch (err) {
      console.error("Failed to remove item:", err);
    }
  };

  const clearCart = () => {
    setCartItems([]);
    sessionStorage.removeItem("cart");
  };

  return (
    <CartContext.Provider
      value={{
        cartItems,
        fetchCart,
        updateCart,
        removeFromCart,
        addToCart,
        clearCart
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);
