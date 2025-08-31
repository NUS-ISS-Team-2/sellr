import React, { createContext, useState } from "react";

// 1️⃣ Create the context
export const UserContext = createContext();

// 2️⃣ Create the provider component
export function ContextProvider({ children }) {
  const [token, setToken] = useState(sessionStorage.getItem("token") || null);
  const [role, setRole] = useState(sessionStorage.getItem("role") || null);
  const [username, setUsername] = useState(sessionStorage.getItem("username") || null);

  // login function
  const login = (jwtToken) => {
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
  };

  // logout function
  const logout = () => {
    setToken(null);
    setRole(null);
    setUsername(null);
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("role");
    sessionStorage.removeItem("username");
  };

  return (
    <UserContext.Provider value={{ token, role, username, login, logout }}>
      {children}
    </UserContext.Provider>
  );
}
