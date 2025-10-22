import React, { useState, useEffect } from "react";

export default function AddressForm({ address, setAddress }) {
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({}); // track touched fields

  const handleChange = (field, value) => {
    setAddress({ ...address, [field]: value });
  };

  const handleBlur = (field) => {
    setTouched({ ...touched, [field]: true });
  };

  useEffect(() => {
    const newErrors = {};

    if (!address.fullName || address.fullName.trim().length === 0) {
      newErrors.fullName = "Full name is required";
    } else if (address.fullName.length > 50) {
      newErrors.fullName = "Full name cannot exceed 50 characters";
    }

    if (!address.street || address.street.trim().length === 0) {
      newErrors.street = "Street address is required";
    } else if (address.street.length > 100) {
      newErrors.street = "Street address cannot exceed 100 characters";
    }

    if (!address.city || address.city.trim().length === 0) {
      newErrors.city = "City is required";
    } else if (address.city.length > 50) {
      newErrors.city = "City cannot exceed 50 characters";
    }

    if (!address.stateZipCountry || address.stateZipCountry.trim().length === 0) {
      newErrors.stateZipCountry = "State / ZIP / Country is required";
    } else if (address.stateZipCountry.length > 100) {
      newErrors.stateZipCountry = "State / ZIP / Country cannot exceed 100 characters";
    }

    setErrors(newErrors);
  }, [address]);

  return (
    <div className="space-y-2">
      <input
        type="text"
        placeholder="Full Name"
        value={address.fullName || ""}
        onChange={(e) => handleChange("fullName", e.target.value)}
        onBlur={() => handleBlur("fullName")}
        className={`w-full p-2 border rounded ${touched.fullName && errors.fullName ? "border-red-500" : "border-gray-300"
          }`}
      />
      {touched.fullName && errors.fullName && (
        <p className="text-red-500 text-sm">{errors.fullName}</p>
      )}

      <input
        type="text"
        placeholder="Street Address"
        value={address.street || ""}
        onChange={(e) => handleChange("street", e.target.value)}
        onBlur={() => handleBlur("street")}
        className={`w-full p-2 border rounded ${touched.street && errors.street ? "border-red-500" : "border-gray-300"
          }`}
      />
      {touched.street && errors.street && (
        <p className="text-red-500 text-sm">{errors.street}</p>
      )}

      <div className="grid grid-cols-2 gap-2">
        <div className="flex flex-col">
          <input
            type="text"
            placeholder="City"
            value={address.city || ""}
            onChange={(e) => handleChange("city", e.target.value)}
            onBlur={() => handleBlur("city")}
            className={`w-full p-2 border rounded ${touched.city && errors.city ? "border-red-500" : "border-gray-300"
              }`}
          />
          {touched.city && errors.city && (
            <p className="text-red-500 text-sm">{errors.city}</p>
          )}
        </div>

        <div className="flex flex-col">
          <input
            type="text"
            placeholder="State / ZIP / Country"
            value={address.stateZipCountry || ""}
            onChange={(e) => handleChange("stateZipCountry", e.target.value)}
            onBlur={() => handleBlur("stateZipCountry")}
            className={`w-full p-2 border rounded ${touched.stateZipCountry && errors.stateZipCountry
                ? "border-red-500"
                : "border-gray-300"
              }`}
          />
          {touched.stateZipCountry && errors.stateZipCountry && (
            <p className="text-red-500 text-sm">{errors.stateZipCountry}</p>
          )}
        </div>
      </div>

    </div>
  );
}
