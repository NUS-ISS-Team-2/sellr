import React, { useContext } from "react";
import { Navigate } from "react-router-dom";
import { UserContext } from "./UserContext";

export default function ProtectedRoute({ children, allowedRoles }) {
  const { token, role } = useContext(UserContext);

  // If not logged in, redirect to login
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // If allowedRoles is provided, check if user's role is allowed
  if (allowedRoles && !allowedRoles.includes(role)) {
    return <Navigate to="/" replace />;
  }

  // Otherwise, render the protected component
  return children;
}
