import React, { useState } from "react";

export default function EmailVerification() {
  const [code, setCode] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [resendMessage, setResendMessage] = useState("");

  // Handle code verification
  const handleVerify = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");

  try {
    // ✅ Add this
    const token = localStorage.getItem("authToken");

    const response = await fetch(
      `http://localhost:8080/api/v1/authentication/validate-email-verification-token?token=${code}`,
      {
        method: "PUT",
        headers: { 
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}` // add JWT here
        },
      }
    );

      const text = await response.text();
      if (response.ok) {
        setMessage("✅ Email verified successfully!");
      } else {
        setMessage("❌ Invalid or expired code. Please try again.");
      }
    } catch (err) {
      console.error(err);
      setMessage("⚠️ Error connecting to server.");
    } finally {
      setLoading(false);
    }
  };

  // Handle resend email verification code
  const handleResend = async () => {
    setResendMessage("Sending new code...");
    const token = localStorage.getItem("authToken"); // only if you store JWT

    try {
      const response = await fetch(
        "http://localhost:8080/api/v1/authentication/resend-email-verification",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const text = await response.text();
      if (response.ok) {
        setResendMessage("✅ Verification email resent! Check your inbox.");
      } else {
        setResendMessage("❌ Failed to resend email. Please try again.");
      }
    } catch (err) {
      console.error(err);
      setResendMessage("⚠️ Error connecting to server.");
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
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 disabled:opacity-50"
          disabled={loading}
        >
          {loading ? "Verifying..." : "Verify"}
        </button>
      </form>

      <button
        onClick={handleResend}
        className="mt-4 bg-gray-200 px-4 py-2 rounded hover:bg-gray-300"
      >
        Resend Code
      </button>

      {message && <p className="mt-4 text-lg">{message}</p>}
      {resendMessage && <p className="mt-2 text-sm text-gray-600">{resendMessage}</p>}
    </div>
  );
}

