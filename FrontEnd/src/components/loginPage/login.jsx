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

    const handleSubmit = (event) => {
        event.preventDefault();
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
        if (Object.values(validationErrors).length === 0 || Object.values(validationErrors).every((e) => e === "")) {
            // Save variables
            const { name, email, password } = formData;
            // Test case: check if email and password match
            if (email === "test@gmail.com" && password === "1234") {
                setErrorMsg("");
                navigate("/home");
            } else {
                setErrorMsg("Incorrect email or password. Please try again.");
            }
        } else {
            setErrorMsg("");
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