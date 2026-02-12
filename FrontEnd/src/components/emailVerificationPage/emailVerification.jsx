import React, { useState } from "react";
import "./emailVerification.css";

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
    <div className="email-verification-container">
      <div className="email-verification-card">
        <h2 className="email-verification-title">Verify Your Email</h2>
        <p className="email-verification-subtitle">We've sent a verification code to your email address</p>
        
        <form onSubmit={handleVerify} className="email-verification-form">
          <div className="email-verification-input-group">
            <input
              type="text"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              placeholder="Enter verification code"
              className="email-verification-input"
              disabled={loading}
            />
          </div>
          
          <button
            type="submit"
            className={`email-verification-btn ${loading ? 'loading' : ''}`}
            disabled={loading}
          >
            {loading ? "Verifying..." : "Verify Email"}
          </button>
        </form>

        <button
          onClick={handleResend}
          className="email-verification-resend-btn"
        >
          Resend Code
        </button>

        {message && <p className={`email-verification-message ${message.startsWith('✅') ? 'success' : 'error'}`}>{message}</p>}
        {resendMessage && <p className={`email-verification-resend-message ${resendMessage.startsWith('✅') ? 'success' : 'error'}`}>{resendMessage}</p>}
      </div>
    </div>
  );
}

