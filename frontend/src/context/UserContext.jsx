import React, { createContext, useState } from "react";

// 1️⃣ Create the context
export const UserContext = createContext();

// 2️⃣ Create the provider component
export function ContextProvider({ children }) {
  const [token, setToken] = useState(sessionStorage.getItem("token") || null);
  const [role, setRole] = useState(sessionStorage.getItem("role") || null);
  const [username, setUsername] = useState(sessionStorage.getItem("username") || null);
  const [userId, setUserId] = useState(sessionStorage.getItem("userId") || null);

  // login function
  const login = (jwtToken, userId) => {
    setToken(jwtToken);
    sessionStorage.setItem("token", jwtToken);

    // Extract role and username from JWT
    const payloadBase64 = jwtToken.split('.')[1];
    const payloadJson = atob(payloadBase64);
    const payload = JSON.parse(payloadJson);

    setRole(payload.role);
    setUsername(payload.sub || payload.username); // adjust based on your JWT
    sessionStorage.setItem("role", payload.role);
    sessionStorage.setItem("username", payload.sub || payload.username);

    setUserId(userId);
    sessionStorage.setItem("userId", userId);
    console.log(userId);
  };

  // logout function
  const logout = (navigate) => {
    setToken(null);
    setRole(null);
    setUsername(null);
    setUserId(null);
    sessionStorage.clear();
    if (navigate) navigate("/success-logout"); 
  };

  return (
    <UserContext.Provider value={{ token, role, username, login, logout, userId }}>
      {children}
    </UserContext.Provider>
  );
}
