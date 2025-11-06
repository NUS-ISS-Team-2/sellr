import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";

export default function PaymentForm({
  paymentMethod,
  setPaymentMethod,
  paymentDetails,
  setPaymentDetails,
}) {
  const paymentOptions = ["PayNow", "Credit Card", "PayPal"];
  const [errors, setErrors] = useState({});

  // Live validation whenever paymentDetails or paymentMethod changes
  useEffect(() => {
    const newErrors = {};

    if (paymentMethod === "Credit Card") {
      if (paymentDetails.cardNumber && !/^\d{13,19}$/.test(paymentDetails.cardNumber)) {
        newErrors.cardNumber = "Card number must be 13-19 digits";
      }
      if (paymentDetails.cardName === "") {
        newErrors.cardName = "Cardholder name is required";
      }
      if (paymentDetails.expiry && !/^(0[1-9]|1[0-2])\/\d{2}$/.test(paymentDetails.expiry)) {
        newErrors.expiry = "Expiry must be in MM/YY format";
      }
      if (paymentDetails.cvv && !/^\d{3,4}$/.test(paymentDetails.cvv)) {
        newErrors.cvv = "CVV must be 3 or 4 digits";
      }
    } else if (paymentMethod === "PayPal") {
      if (
        paymentDetails.paypalEmail &&
        !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(paymentDetails.paypalEmail)
      ) {
        newErrors.paypalEmail = "Valid PayPal email is required";
      }
    } else if (paymentMethod === "PayNow") {
      if (paymentDetails.referenceNumber && paymentDetails.referenceNumber.length > 20) {
        newErrors.referenceNumber = "Reference number must be less than 20 characters";
      }
    }

    setErrors(newErrors);
  }, [paymentDetails, paymentMethod]);

  // Helper function to handle input changes
  const handleChange = (field, value) => {
    setPaymentDetails({ ...paymentDetails, [field]: value });
  };

  return (
    <div className="space-y-4">
      {/* Tab Buttons */}
      <div className="flex border-b border-gray-300">
        {paymentOptions.map((option) => (
          <button
            key={option}
            type="button"
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
            onChange={(e) => handleChange("cardNumber", e.target.value)}
            className="w-full p-2 border rounded"
          />
          {errors.cardNumber && <p className="text-red-500 text-sm">{errors.cardNumber}</p>}

          <input
            type="text"
            placeholder="Name on Card"
            value={paymentDetails.cardName || ""}
            onChange={(e) => handleChange("cardName", e.target.value)}
            className="w-full p-2 border rounded"
          />
          {errors.cardName && <p className="text-red-500 text-sm">{errors.cardName}</p>}

          <input
            type="text"
            placeholder="Expiry Date (MM/YY)"
            value={paymentDetails.expiry || ""}
            onChange={(e) => handleChange("expiry", e.target.value)}
            className="w-full p-2 border rounded"
          />
          {errors.expiry && <p className="text-red-500 text-sm">{errors.expiry}</p>}

          <input
            type="text"
            placeholder="CVV"
            value={paymentDetails.cvv || ""}
            onChange={(e) => handleChange("cvv", e.target.value)}
            className="w-full p-2 border rounded"
          />
          {errors.cvv && <p className="text-red-500 text-sm">{errors.cvv}</p>}
        </div>
      )}

      {paymentMethod === "PayPal" && (
        <div className="mt-2">
          <input
            type="email"
            placeholder="PayPal Email"
            value={paymentDetails.paypalEmail || ""}
            onChange={(e) => handleChange("paypalEmail", e.target.value)}
            className="w-full p-2 border rounded"
          />
          {errors.paypalEmail && <p className="text-red-500 text-sm">{errors.paypalEmail}</p>}
        </div>
      )}

      {paymentMethod === "PayNow" && (
        <div className="space-y-2 mt-2">
          <input
            type="text"
            placeholder="Enter Reference Number (max 20 characters)"
            value={paymentDetails.referenceNumber || ""}
            onChange={(e) => handleChange("referenceNumber", e.target.value)}
            className="w-full p-2 border rounded"
          />
          {errors.referenceNumber && (
            <p className="text-red-500 text-sm">{errors.referenceNumber}</p>
          )}
          <p className="text-sm text-gray-500">
            Use this fake UEN for testing: <strong>201912345K</strong>
          </p>
          <p className="text-sm text-gray-500">
            Instructions: Open your PayNow app, choose "Pay to UEN", enter the UEN above as the
            recipient, and add your username to the reference ID.
          </p>
        </div>
      )}
    </div>
  );
}

PaymentForm.propTypes = {
  paymentMethod: PropTypes.string.isRequired,
  setPaymentMethod: PropTypes.func.isRequired,
  paymentDetails: PropTypes.shape({
    cardNumber: PropTypes.string,
    cardName: PropTypes.string,
    expiry: PropTypes.string,
    cvv: PropTypes.string,
    paypalEmail: PropTypes.string,
    referenceNumber: PropTypes.string,
  }).isRequired,
  setPaymentDetails: PropTypes.func.isRequired,
};