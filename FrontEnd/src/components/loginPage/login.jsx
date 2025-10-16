import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./loginPage.css"
import userIcon from '../assets/person.png'
import emailIcon from '../assets/email.png'
import passwordIcon from '../assets/password.png';

const FormWithValidation = () => {
    const [mode, setMode] = useState("login"); // 'login' or 'signup'
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
    });
    const [formErrors, setFormErrors] = useState({
        name: "",
        email: "",
        password: "",
    });
    const [errorMsg, setErrorMsg] = useState("");
    const navigate = useNavigate();

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        setFormData({
            ...formData,
            [name]: value,
        });
        // Validation
        if (name === "name" && mode === "signup" && value.trim() === "") {
            setFormErrors({ ...formErrors, name: "Name is required." });
        } else if (name === "email" && !/^\S+@\S+\.\S+$/.test(value)) {
            setFormErrors({ ...formErrors, email: "Invalid email address." });
        } else if (name === "password" && value.trim() === "") {
            setFormErrors({ ...formErrors, password: "Password is required." });
        } else {
            setFormErrors({ ...formErrors, [name]: "" });
        }
    };
//change this here for backend
const handleSubmit = async (event) => {
  event.preventDefault();

  // Validation
  let validationErrors = {};
  if (mode === "signup") {
    if (formData.name.trim() === "") validationErrors.name = "Name is required.";
    if (!/^\S+@\S+\.\S+$/.test(formData.email)) validationErrors.email = "Invalid email address.";
    if (formData.password.trim() === "") validationErrors.password = "Password is required.";
  } else {
    if (!/^\S+@\S+\.\S+$/.test(formData.email)) validationErrors.email = "Invalid email address.";
    if (formData.password.trim() === "") validationErrors.password = "Password is required.";
  }

  setFormErrors(validationErrors);
  if (Object.keys(validationErrors).length > 0) return;

  const { name, email, password } = formData;

  try {
    const endpoint =
      mode === "signup"
        ? "http://localhost:8080/api/v1/authentication/register"
        : "http://localhost:8080/api/v1/authentication/login";

    const response = await fetch(endpoint, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, email, password }),
    });

    const data = await response.json();
    console.log("Server response:", data);

    if (response.ok) {
      if (mode === "signup") {
        alert("Registration successful! Please check your email.");
      } else {
        alert("Login successful!");
        // optional: store the JWT token from backend
        localStorage.setItem("authToken", data.token);
      }
      navigate("/home");
    } else {
      setErrorMsg(data.message || "Something went wrong.");
    }
  } catch (error) {
    console.error("Fetch error:", error);
    setErrorMsg("Could not connect to backend.");
  }
};



    return (
        <div className="container">
            <div className="login-home-btn-container">
                <button
                    className="login-home-btn"
                    onClick={() => navigate("/home")}
                >
                    Home
                </button>
            </div>
            <div className="header">
                <div className="text">{mode === "login" ? "Login" : "Sign Up"}</div>
                <div className="underline"></div>
            </div>
            <div className="login-mode-btns">
                <button className="login-mode-btn" onClick={() => setMode("login")} disabled={mode === "login"}>Login</button>
                <button className="login-mode-btn" onClick={() => setMode("signup")} disabled={mode === "signup"}>Sign Up</button>
            </div>
            <div className="inputs">
                <form onSubmit={handleSubmit}>
                    {mode === "signup" && (
                        <label className="nameInput">
                            <img src={userIcon} width={40} height={40} alt="" />
                            Name:
                            <input
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={handleInputChange}
                            />
                            <span className="error">{formErrors.name}</span>
                        </label>
                    )}
                    <label className="emailInput">
                        <img src={emailIcon} width={40} height={40} alt="" />
                        Email:
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                        />
                        <span className="error">{formErrors.email}</span>
                    </label>
                    <label className="passwordInput">
                        <img src={passwordIcon} width={40} height={40} alt="" />
                        Password:
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleInputChange}
                        />
                        <span className="error">{formErrors.password}</span>
                    </label>
                    <button type="submit">Submit</button>
                </form>
                {errorMsg && <div className="error login-error-msg">{errorMsg}</div>}
            </div>
        </div>
    );
};

export default FormWithValidation;