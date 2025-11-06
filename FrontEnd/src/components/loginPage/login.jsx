import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./loginPage.css";
import userIcon from "../assets/person.png";
import emailIcon from "../assets/email.png";
import passwordIcon from "../assets/password.png";

const FormWithValidation = () => {
  const [mode, setMode] = useState("login"); // 'login' or 'signup'
  const [formData, setFormData] = useState({
    name: "",
    lastname: "",
    username: "",
    email: "",
    password: "",
  });
  const [formErrors, setFormErrors] = useState({
    name: "",
    lastname: "",
    username: "",
    email: "",
    password: "",
  });
  const [errorMsg, setErrorMsg] = useState("");
  const [verificationMode, setVerificationMode] = useState(false);
  const [verificationCode, setVerificationCode] = useState("");
  const navigate = useNavigate();

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData({ ...formData, [name]: value });

    // Validation rules
    if (name === "name" && mode === "signup" && value.trim() === "") {
      setFormErrors({ ...formErrors, name: "Name is required." });
    } 
    else if (name === "lastname" && mode === "signup" && value.trim() === "") {
      setFormErrors({ ...formErrors, lastname: "Lastname is required." });
    } 
    else if (name === "username" && mode === "signup" && value.trim() === "") {
      setFormErrors({ ...formErrors, username: "Username is required." });
    } 
    else if (name === "email" && !/^\S+@\S+\.\S+$/.test(value)) {
      setFormErrors({ ...formErrors, email: "Invalid email address." });
    } 
    else if (name === "password" && value.trim() === "") {
      setFormErrors({ ...formErrors, password: "Password is required." });
    } 
    else {
      setFormErrors({ ...formErrors, [name]: "" });
    }
  };

const handleSubmit = async (event) => {
  event.preventDefault();

  // 1.) Validate fields
  const validationErrors = {};
  if (mode === "signup") {
    if (!formData.name.trim()) validationErrors.name = "Name is required.";
    if (!formData.lastname.trim()) validationErrors.lastname = "Last name is required.";
    if (!formData.username.trim()) validationErrors.username = "Username is required.";
  }
  if (!/^\S+@\S+\.\S+$/.test(formData.email)) validationErrors.email = "Invalid email address.";
  if (!formData.password.trim()) validationErrors.password = "Password is required.";

  setFormErrors(validationErrors);
  if (Object.keys(validationErrors).length > 0) return; // Stop if errors

  try {
    const endpoint =
      mode === "signup"
        ? "http://localhost:8080/api/v1/authentication/register"
        : "http://localhost:8080/api/v1/authentication/login";

    // 2.) Map frontend field to backend field
    const payload = mode === "signup"
      ? {
          name: formData.name,
          lastName: formData.lastname, // <-- backend expects camelCase
          username: formData.username,
          email: formData.email,
          password: formData.password
        }
      : {
          email: formData.email,
          password: formData.password
        };

    // 3.) Send POST request
    const response = await fetch(endpoint, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const data = await response.json();
    console.log("Server response:", data);

    if (response.ok) {
      localStorage.setItem("authToken", data.token);

      if (mode === "signup") {
        alert("Registration successful! Please check your email for the verification code.");
        setVerificationMode(true);
      } else {
        alert("Login successful!");
        navigate("/home");
      }
    } else {
      setErrorMsg(data.message || "Something went wrong.");
    }
  } catch (error) {
    console.error("Fetch error:", error);
    setErrorMsg("Could not connect to backend.");
  }
};


console.log("Sending verification code:", verificationCode);

  const handleVerification = async () => {
  if (!verificationCode.trim()) {
    alert("Please enter the verification code.");
    return;
  }

  try {
    // Attempt query string method
    let res = await fetch(
      `http://localhost:8080/api/v1/authentication/validate-email-verification-token?token=${verificationCode}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${localStorage.getItem("authToken") || ""}` // optional
        }
      }
    );

    // If 401, try sending token in body instead
    if (res.status === 401) {
      res = await fetch(
        "http://localhost:8080/api/v1/authentication/validate-email-verification-token",
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("authToken") || ""}` // optional
          },
          body: JSON.stringify({ token: verificationCode })
        }
      );
    }

    // Parse response
    const data = await res.json().catch(() => ({}));
    console.log("Verification response:", res.status, data);

    if (res.ok) {
      alert("Email verified successfully!");
      setVerificationMode(false);
      navigate("/home");
    } else {
      // Show backend error message if any
      alert(data.message || `Verification failed. Status code: ${res.status}`);
    }
  } catch (err) {
    console.error("Error verifying email:", err);
    alert("Network error or backend not reachable.");
  }
};


  return (
    <div className="container">
      <div className="login-home-btn-container">
        <button className="login-home-btn" onClick={() => navigate("/home")}>
          Home
        </button>
      </div>

      <div className="header">
        <div className="text">{mode === "login" ? "Login" : "Sign Up"}</div>
        <div className="underline"></div>
      </div>

      <div className="login-mode-btns">
        <button className="login-mode-btn" onClick={() => setMode("login")} disabled={mode === "login"}>
          Login
        </button>
        <button className="login-mode-btn" onClick={() => setMode("signup")} disabled={mode === "signup"}>
          Sign Up
        </button>
      </div>

      <div className="inputs">
        {!verificationMode ? (
          <form onSubmit={handleSubmit}>
            {mode === "signup" && (
              <>
                <label className="nameInput">
                  <img src={userIcon} width={40} height={40} alt="" />
                  Name:
                  <input type="text" name="name" value={formData.name} onChange={handleInputChange} />
                  <span className="error">{formErrors.name}</span>
                </label>

                <label className="lastnameInput">
                  <img src={userIcon} width={40} height={40} alt="" />
                  Last Name:
                  <input type="text" name="lastname" value={formData.lastname} onChange={handleInputChange} />
                  <span className="error">{formErrors.lastname}</span>
                </label>

                <label className="usernameInput">
                  <img src={userIcon} width={40} height={40} alt="" />
                  Username:
                  <input type="text" name="username" value={formData.username} onChange={handleInputChange} />
                  <span className="error">{formErrors.username}</span>
                </label>
              </>
            )}

            <label className="emailInput">
              <img src={emailIcon} width={40} height={40} alt="" />
              Email:
              <input type="email" name="email" value={formData.email} onChange={handleInputChange} />
              <span className="error">{formErrors.email}</span>
            </label>

            <label className="passwordInput">
              <img src={passwordIcon} width={40} height={40} alt="" />
              Password:
              <input type="password" name="password" value={formData.password} onChange={handleInputChange} />
              <span className="error">{formErrors.password}</span>
            </label>

            <button type="submit">Submit</button>
          </form>
        ) : (
          <div className="verification-container">
            <p>We’ve sent a verification code to your email.</p>
            <input
              type="text"
              placeholder="Enter verification code"
              value={verificationCode}
              onChange={(e) => setVerificationCode(e.target.value)}
            />
            <button onClick={handleVerification}>Verify Email</button>
          </div>
        )}

        {errorMsg && <div className="error login-error-msg">{errorMsg}</div>}
      </div>
    </div>
  );
};

export default FormWithValidation;
