import React, { useState } from "react";

export default function EmailVerification() {
  const [code, setCode] = useState("");
  const [message, setMessage] = useState("");

  const handleVerify = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("authToken"); // saved during login/register

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/authentication/validate-email-verification-token?token=${code}`,
        {
          method: "PUT",
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        }
      );

      const text = await response.text();
      if (response.ok) {
        setMessage("✅ " + text);
      } else {
        setMessage("❌ " + text);
      }
    } catch (err) {
      setMessage("⚠️ Error connecting to server.");
      console.error(err);
    }
  };

  return (
    <div className="flex flex-col items-center mt-10">
      <h2 className="text-2xl font-bold mb-4">Verify Your Email</h2>
      <form onSubmit={handleVerify} className="flex flex-col gap-3">
        <input
          type="text"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          placeholder="Enter verification code"
          className="border rounded px-3 py-2"
        />
        <button
          type="submit"
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          Verify
        </button>
      </form>
      {message && <p className="mt-4">{message}</p>}
    </div>
  );
}
