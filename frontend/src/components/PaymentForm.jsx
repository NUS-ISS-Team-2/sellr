import React from "react";

export default function PaymentForm({
  paymentMethod,
  setPaymentMethod,
  paymentDetails,
  setPaymentDetails,
}) {
  return (
    <div className="space-y-2">
      <label className="block mb-1 font-medium">Payment Method</label>
      <select
        value={paymentMethod}
        onChange={(e) => setPaymentMethod(e.target.value)}
        className="w-full p-2 border rounded"
      >
        <option value="Credit Card">Credit Card</option>
        <option value="PayPal">PayPal</option>
        <option value="Bank Transfer">Bank Transfer</option>
      </select>

      {paymentMethod === "Credit Card" && (
        <div className="space-y-2">
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
          className="w-full p-2 border rounded"
          required
        />
      )}

      {paymentMethod === "Bank Transfer" && (
        <div className="space-y-2">
          <input
            type="text"
            placeholder="Bank Name"
            value={paymentDetails.bankName || ""}
            onChange={(e) =>
              setPaymentDetails({ ...paymentDetails, bankName: e.target.value })
            }
            className="w-full p-2 border rounded"
            required
          />
          <input
            type="text"
            placeholder="Account Number"
            value={paymentDetails.accountNumber || ""}
            onChange={(e) =>
              setPaymentDetails({ ...paymentDetails, accountNumber: e.target.value })
            }
            className="w-full p-2 border rounded"
            required
          />
          <input
            type="text"
            placeholder="Account Holder Name"
            value={paymentDetails.accountHolder || ""}
            onChange={(e) =>
              setPaymentDetails({ ...paymentDetails, accountHolder: e.target.value })
            }
            className="w-full p-2 border rounded"
            required
          />
        </div>
      )}
    </div>
  );
}
