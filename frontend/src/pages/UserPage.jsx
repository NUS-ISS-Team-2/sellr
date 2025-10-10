import React, { useEffect, useState, useContext } from "react";
import { Navigate } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";
import axios from "axios";
import { UserContext } from "../context/UserContext";
import { API_BASE_URL } from "../config";

export default function UsersPage() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [editingUser, setEditingUser] = useState(null);
    const API_USERS = `${API_BASE_URL}/users`;
    const [passwordUser, setPasswordUser] = useState(null);
    const [newPassword, setNewPassword] = useState("");

    const { role } = useContext(UserContext);

    useEffect(() => {
        if (role !== "ADMIN") return;

        const fetchUsers = async () => {
            try {
                const res = await axios.get(API_USERS);
                setUsers(res.data.filter((user) => user.role !== "ADMIN"));
            } catch (err) {
                console.error("Error fetching users:", err);
                setError("Failed to load users.");
            } finally {
                setLoading(false);
            }
        };

        fetchUsers();
    }, [role, API_USERS]);

    if (role !== "ADMIN") {
        return <Navigate to="/" replace />;
    }

    const handleSaveUser = async (updatedUser) => {
        try {
            const res = await axios.put(`${API_USERS}/${updatedUser.id}`, updatedUser);
            setUsers((prev) =>
                prev.map((u) => (u.id === updatedUser.id ? res.data : u))
            );
            setEditingUser(null);
        } catch (err) {
            console.error("Failed to update user:", err);
            alert("Failed to update user");
        }
    };

    const handleToggleDisable = async (user) => {
        try {
            const endpoint = `${API_USERS}/${user.id}/${user.disabled ? "enable" : "disable"}`;
            await axios.put(endpoint);
            // Update state
            setUsers((prev) =>
                prev.map((u) =>
                    u.id === user.id ? { ...u, disabled: !u.disabled } : u
                )
            );
        } catch (err) {
            console.error("Failed to update user status:", err);
            alert("Failed to update user status");
        }
    };

    return (
        <div className="flex flex-col min-h-screen bg-gray-50">
            <Header />
            <main className="flex-1 container mx-auto px-6 py-10">
                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-3xl font-bold">All Users</h1>
                </div>

                <div className="bg-white border border-gray-200 rounded-xl shadow-sm overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr className="text-left text-sm font-semibold text-gray-700">
                                    <th className="px-4 py-3">User ID</th>
                                    <th className="px-4 py-3">Username</th>
                                    <th className="px-4 py-3">Email</th>
                                    <th className="px-4 py-3">Role</th>
                                    <th className="px-4 py-3">Status</th>
                                    <th className="px-4 py-3 text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                                {loading ? (
                                    <tr>
                                        <td colSpan="5" className="px-4 py-10 text-center">
                                            <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
                                        </td>
                                    </tr>
                                ) : error ? (
                                    <tr>
                                        <td colSpan="5" className="px-4 py-10 text-center text-red-600">
                                            {error}
                                        </td>
                                    </tr>
                                ) : users.length === 0 ? (
                                    <tr>
                                        <td colSpan="5" className="px-4 py-10 text-center text-gray-500">
                                            No users found.
                                        </td>
                                    </tr>
                                ) : (
                                    users.map((user) => (
                                        <tr key={user.id} className="text-sm">
                                            <td className="px-4 py-4">{user.id}</td>
                                            <td className="px-4 py-4">{user.username}</td>
                                            <td className="px-4 py-4">{user.email}</td>
                                            <td className="px-4 py-4">{user.role}</td>
                                            <td className="px-4 py-4">
                                                {user.disabled ? (
                                                    <span className="text-red-600 font-semibold">Disabled</span>
                                                ) : (
                                                    <span className="text-green-600 font-semibold">Active</span>
                                                )}
                                            </td>
                                            <td className="px-4 py-4 text-right space-x-2">
                                                <button
                                                    className="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                                                    onClick={() => setEditingUser(user)}
                                                >
                                                    Edit
                                                </button>
                                                <button
                                                    className="px-2 py-1 bg-purple-500 text-white rounded hover:bg-purple-600"
                                                    onClick={() => setPasswordUser(user)}
                                                >
                                                    Change Password
                                                </button>
                                                <button
                                                    className={`px-2 py-1 rounded text-white ${user.disabled ? "bg-green-500 hover:bg-green-600" : "bg-red-500 hover:bg-red-600"
                                                        }`}
                                                    onClick={() => handleToggleDisable(user)}
                                                >
                                                    {user.disabled ? "Enable" : "Disable"}
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>

                {/* Edit User Modal */}
                {editingUser && (
                    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
                        <div className="bg-white p-6 rounded-xl shadow max-w-md w-full">
                            <h2 className="text-xl font-bold mb-4">Edit User</h2>
                            <form
                                onSubmit={(e) => {
                                    e.preventDefault();
                                    handleSaveUser(editingUser);
                                }}
                            >
                                <label className="block mb-2">
                                    Username:
                                    <input
                                        type="text"
                                        className="mt-1 w-full border rounded px-2 py-1"
                                        value={editingUser.username}
                                        onChange={(e) =>
                                            setEditingUser({ ...editingUser, username: e.target.value })
                                        }
                                    />
                                </label>

                                <label className="block mb-2">
                                    Email:
                                    <input
                                        type="email"
                                        className="mt-1 w-full border rounded px-2 py-1"
                                        value={editingUser.email}
                                        onChange={(e) =>
                                            setEditingUser({ ...editingUser, email: e.target.value })
                                        }
                                    />
                                </label>

                                {/* Display role as read-only */}
                                <label className="block mb-2">
                                    Role:
                                    <input
                                        type="text"
                                        className="mt-1 w-full border rounded px-2 py-1 bg-gray-100 cursor-not-allowed"
                                        value={editingUser.role}
                                        readOnly
                                    />
                                </label>

                                <div className="flex justify-end mt-4 space-x-2">
                                    <button
                                        type="button"
                                        className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
                                        onClick={() => setEditingUser(null)}
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        type="submit"
                                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                                    >
                                        Save
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}


                {/* Change Password Modal */}
                {passwordUser && (
                    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
                        <div className="bg-white p-6 rounded-xl shadow max-w-md w-full">
                            <h2 className="text-xl font-bold mb-4">Change Password</h2>
                            <form
                                onSubmit={async (e) => {
                                    e.preventDefault();
                                    try {
                                        await axios.put(`${API_USERS}/${passwordUser.id}/password`, {
                                            password: newPassword
                                        });
                                        alert("Password updated successfully");
                                        setPasswordUser(null);
                                        setNewPassword("");
                                    } catch (err) {
                                        console.error("Failed to update password:", err);
                                        alert("Failed to update password");
                                    }
                                }}
                            >
                                <label className="block mb-2">
                                    New Password:
                                    <input
                                        type="password"
                                        className="mt-1 w-full border rounded px-2 py-1"
                                        value={newPassword}
                                        onChange={(e) => setNewPassword(e.target.value)}
                                        required
                                    />
                                </label>

                                <div className="flex justify-end mt-4 space-x-2">
                                    <button
                                        type="button"
                                        className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
                                        onClick={() => {
                                            setPasswordUser(null);
                                            setNewPassword("");
                                        }}
                                    >
                                        Cancel
                                    </button>
                                    <button
                                        type="submit"
                                        className="px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700"
                                    >
                                        Update
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}

            </main>
            <Footer />
        </div>
    );
}
