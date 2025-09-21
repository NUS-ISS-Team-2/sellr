import React from "react";

export default function AddressForm({ address, setAddress }) {
  return (
    <div className="space-y-2">
      <input
        type="text"
        placeholder="Full Name"
        value={address.fullName}
        onChange={(e) => setAddress({ ...address, fullName: e.target.value })}
        className="w-full p-2 border rounded"
        required
      />
      <input
        type="text"
        placeholder="Street Address"
        value={address.street}
        onChange={(e) => setAddress({ ...address, street: e.target.value })}
        className="w-full p-2 border rounded"
        required
      />
      <div className="grid grid-cols-2 gap-2">
        <input
          type="text"
          placeholder="City"
          value={address.city}
          onChange={(e) => setAddress({ ...address, city: e.target.value })}
          className="w-full p-2 border rounded"
          required
        />
        <input
          type="text"
          placeholder="State / ZIP / Country"
          value={address.stateZipCountry}
          onChange={(e) =>
            setAddress({ ...address, stateZipCountry: e.target.value })
          }
          className="w-full p-2 border rounded"
          required
        />
      </div>
    </div>
  );
}
