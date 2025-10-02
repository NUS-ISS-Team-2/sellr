import React from "react";

export default function CheckoutForm({
  address,
  setAddress,
  paymentMethod,
  setPaymentMethod,
  paymentDetails,
  setPaymentDetails,
  handleCheckout
}) {
  return (
    <div className="space-y-6">
      {/* Shipping Address Card */}
      <div className="bg-white shadow-md rounded-lg p-6">
        <h2 className="text-xl font-semibold mb-4">Shipping Address</h2>
        <textarea
          value={address}
          onChange={(e) => setAddress(e.target.value)}
          placeholder="Enter your shipping address"
          required
          rows={3}
          className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
        />
      </div>

      {/* Payment Details Card */}
      <div className="bg-white shadow-md rounded-lg p-6">
        <h2 className="text-xl font-semibold mb-4">Payment Information</h2>
        <div className="mb-4">
          <label className="block mb-1 font-medium">Payment Method</label>
          <select
            value={paymentMethod}
            onChange={(e) => setPaymentMethod(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="Credit Card">Credit Card</option>
            <option value="PayPal">PayPal</option>
            <option value="Bank Transfer">Bank Transfer</option>
          </select>
        </div>

        {paymentMethod === "Credit Card" && (
          <div className="space-y-3">
            <input
              type="text"
              placeholder="Card Number"
              value={paymentDetails.cardNumber || ""}
              onChange={(e) =>
                setPaymentDetails({ ...paymentDetails, cardNumber: e.target.value })
              }
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
            <div className="grid grid-cols-2 gap-3">
              <input
                type="text"
                placeholder="Name on Card"
                value={paymentDetails.cardName || ""}
                onChange={(e) =>
                  setPaymentDetails({ ...paymentDetails, cardName: e.target.value })
                }
                className="p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
              <input
                type="text"
                placeholder="Expiry Date (MM/YY)"
                value={paymentDetails.expiry || ""}
                onChange={(e) =>
                  setPaymentDetails({ ...paymentDetails, expiry: e.target.value })
                }
                className="p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>
            <input
              type="text"
              placeholder="CVV"
              value={paymentDetails.cvv || ""}
              onChange={(e) =>
                setPaymentDetails({ ...paymentDetails, cvv: e.target.value })
              }
              className="w-32 p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
        )}

        {paymentMethod === "PayPal" && (
          <input
            type="email"
            placeholder="PayPal Email"
            value={paymentDetails.paypalEmail || ""}
            onChange={(e) =>
              setPaymentDetails({ ...paymentDetails, paypalEmail: e.target.value })
            }
            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        )}

        {paymentMethod === "Bank Transfer" && (
          <div className="space-y-3">
            <input
              type="text"
              placeholder="Bank Name"
              value={paymentDetails.bankName || ""}
              onChange={(e) =>
                setPaymentDetails({ ...paymentDetails, bankName: e.target.value })
              }
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
            <input
              type="text"
              placeholder="Account Number"
              value={paymentDetails.accountNumber || ""}
              onChange={(e) =>
                setPaymentDetails({ ...paymentDetails, accountNumber: e.target.value })
              }
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
            <input
              type="text"
              placeholder="Account Holder Name"
              value={paymentDetails.accountHolder || ""}
              onChange={(e) =>
                setPaymentDetails({ ...paymentDetails, accountHolder: e.target.value })
              }
              className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              required
            />
          </div>
        )}
      </div>

      {/* Checkout Button */}
      <button
        onClick={handleCheckout}
        className="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors"
      >
        Proceed to Checkout
      </button>
    </div>
  );
}
