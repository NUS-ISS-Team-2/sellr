import React from "react";

export default function PaymentForm({
  paymentMethod,
  setPaymentMethod,
  paymentDetails,
  setPaymentDetails,
}) {
  const paymentOptions = ["PayNow", "Credit Card", "PayPal"];

  return (
    <div className="space-y-4">
      {/* Tab Buttons */}
      <div className="flex border-b border-gray-300">
        {paymentOptions.map((option) => (
          <button
            key={option}
            onClick={() => setPaymentMethod(option)}
            className={`flex-1 py-2 text-center font-medium rounded-t
              ${
                paymentMethod === option
                  ? "bg-blue-600 text-white"
                  : "bg-gray-100 text-gray-700 hover:bg-gray-200"
              }`}
          >
            {option}
          </button>
        ))}
      </div>

      {/* Payment Details */}
      {paymentMethod === "Credit Card" && (
        <div className="space-y-2 mt-2">
          <input
            type="text"
            placeholder="Card Number"
            value={paymentDetails.cardNumber || ""}
            onChange={(e) =>
              setPaymentDetails({ ...paymentDetails, cardNumber: e.target.value })
            }
            className="w-full p-2 border rounded"
            required
          />
          <input
            type="text"
            placeholder="Name on Card"
            value={paymentDetails.cardName || ""}
            onChange={(e) =>
              setPaymentDetails({ ...paymentDetails, cardName: e.target.value })
            }
            className="w-full p-2 border rounded"
            required
          />
          <input
            type="text"
            placeholder="Expiry Date (MM/YY)"
            value={paymentDetails.expiry || ""}
            onChange={(e) =>
              setPaymentDetails({ ...paymentDetails, expiry: e.target.value })
            }
            className="w-full p-2 border rounded"
            required
          />
          <input
            type="text"
            placeholder="CVV"
            value={paymentDetails.cvv || ""}
            onChange={(e) =>
              setPaymentDetails({ ...paymentDetails, cvv: e.target.value })
            }
            className="w-full p-2 border rounded"
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
          className="w-full p-2 border rounded mt-2"
          required
        />
      )}

      {paymentMethod === "PayNow" && (
        <div className="space-y-2 mt-2">
          <input
            type="text"
            placeholder="Enter Reference Number (e.g., 12345678)"
            value={paymentDetails.referenceNumber || ""}
            onChange={(e) =>
              setPaymentDetails({ ...paymentDetails, referenceNumber: e.target.value })
            }
            className="w-full p-2 border rounded"
            required
          />
          <p className="text-sm text-gray-500">
            Use this fake UEN for testing: <strong>201912345K</strong>
          </p>
          <p className="text-sm text-gray-500">
            Instructions: Open your PayNow app, choose "Pay to UEN", enter the UEN above as the recipient, and add your username to the reference ID.
          </p>
        </div>
      )}
    </div>
  );
}
