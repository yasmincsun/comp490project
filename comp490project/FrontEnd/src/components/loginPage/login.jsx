import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./loginPage.css";
import userIcon from "../assets/person.png";
import emailIcon from "../assets/email.png";
import passwordIcon from "../assets/password.png";

/**
 * Changes display based on user input 
 * <p>  
 * Displays a login input form if the user already has an account or displays a signup input form if the user wants to create an account
 */
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

  // Disable page scrolling while this component is mounted to prevent background scroll
  // useEffect(() => {
  //   const previousOverflow = document.body.style.overflow;
  //   document.body.style.overflow = "hidden";
  //   return () => {
  //     document.body.style.overflow = previousOverflow || "";
  //   };
  // }, []);

  /**
 * Checks values of user input 
 * <p>
 * Checks whether the user’s inputs are valid, ensuring that they are including all required fields in the standard format. Error messages are displayed to the user if they are missing a component or included information in an incorrect format. 
 */
  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData({ ...formData, [name]: value });

    // Validation rules
    if (name === "name" && mode === "signup" && value.trim() === "") {
      setFormErrors({ ...formErrors, name: "First Name is Required." });
    } 
    else if (name === "lastname" && mode === "signup" && value.trim() === "") {
      setFormErrors({ ...formErrors, lastname: "Last Name is Required." });
    } 
    else if (name === "username" && mode === "signup" && value.trim() === "") {
      setFormErrors({ ...formErrors, username: "Username is Required." });
    } 
    else if (name === "email" && !/^\S+@\S+\.\S+$/.test(value)) {
      setFormErrors({ ...formErrors, email: "Invalid email address." });
    } 
    else if (name === "password" && value.trim() === "") {
      setFormErrors({ ...formErrors, password: "Password is Required." });
    } 
    else {
      setFormErrors({ ...formErrors, [name]: "" });
    }
  };

  /**
 * Processes the login information 
 * <p>
 * This function processes the login information after ensuring the inputs are valid, and sends the information through to the backend. The information is then stored in the database and now gives the user the option to login using their information. 
 */
const handleSubmit = async (event) => {
  event.preventDefault();

  // 1.) Validate fields
  const validationErrors = {};
  if (mode === "signup") {
    if (!formData.name.trim()) validationErrors.name = "Name is Required.";
    if (!formData.lastname.trim()) validationErrors.lastname = "Last Name is Required.";
    if (!formData.username.trim()) validationErrors.username = "Username is Required.";
  }
  if (!/^\S+@\S+\.\S+$/.test(formData.email)) validationErrors.email = "Invalid email address.";
  if (!formData.password.trim()) validationErrors.password = "Password is Required.";

  setFormErrors(validationErrors);
  if (Object.keys(validationErrors).length > 0) return; // Stop if errors

  try {
    // const endpoint =
      // mode === "signup"
      //   ? "http://localhost:8080/api/v1/authentication/register"
      //   : "http://localhost:8080/api/v1/authentication/login";
      const endpoint =
          mode === "signup"
            ? "http://127.0.0.1:8080/api/v1/authentication/register"
            : "http://127.0.0.1:8080/api/v1/authentication/login";


 
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

/**
 * Sends verification code 
 * <p>
 * This function sends a verification code to the user’s email address to confirm their account. Verifying the email address ensures that spam bots are not created, and guarantees that the user did not accidentally input the wrong address. The verification code is sent via MailChimp, which is accessed through Port 8080.  
 */
  const handleVerification = async () => {
  if (!verificationCode.trim()) {
    alert("Please enter the verification code.");
    return;
  }

  try {
    // Attempt query string method
    // let res = await fetch(
    //   `http://localhost:8080/api/v1/authentication/validate-email-verification-token?token=${verificationCode}`,
    //   {
    //     method: "PUT",
    //     headers: {
    //       "Content-Type": "application/json",
    //       "Authorization": `Bearer ${localStorage.getItem("authToken") || ""}` // optional
    //     }
    //   }
    // );

    let res = await fetch(
  `http://127.0.0.1:8080/api/v1/authentication/validate-email-verification-token?token=${verificationCode}`,
  {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${localStorage.getItem("authToken") || ""}`
    }
  }
);


    // If 401, try sending token in body instead
    // if (res.status === 401) {
    //   res = await fetch(
    //     "http://localhost:8080/api/v1/authentication/validate-email-verification-token",
    //     {
    //       method: "PUT",
    //       headers: {
    //         "Content-Type": "application/json",
    //         "Authorization": `Bearer ${localStorage.getItem("authToken") || ""}` // optional
    //       },
    //       body: JSON.stringify({ token: verificationCode })
    //     }
    //   );
    // }

    if (res.status === 401) {
  res = await fetch(
    "http://127.0.0.1:8080/api/v1/authentication/validate-email-verification-token",
    {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${localStorage.getItem("authToken") || ""}`
      },
      body: JSON.stringify({ token: verificationCode }),
      credentials: "include" // <--- make sure cookies are sent
    }
  );
}


    // Parse response
    const data = await res.json().catch(() => ({}));
    console.log("Verification response:", res.status, data);

    if (res.ok) {
      alert("Email verified successfully!");
      setVerificationMode(false);
      // navigate to connect spotify screen first so user can connect their account
      navigate("/connect-spotify");
    } else {
      // Show backend error message if any
      alert(data.message || `Verification failed. Status code: ${res.status}`);
    }
  } catch (err) {
    console.error("Error verifying email:", err);
    alert("Network error or backend not reachable.");
  }
};

/**
 * Displays the Login Page and runs all of the components 
 * <p>
 * This function displays and runs the entirety of the Login Page. This method checks whether the user wants to login or sign up, and runs validations to ensure that the user’s information is accurate. 
* @return Login Page display to the Web Page
 */
  // determine if form is complete (for signup require name/lastname/username/email/password; for login only email/password)
  const isComplete = () => {
    if (mode === "signup") {
      return (
        formData.name.trim() &&
        formData.lastname.trim() &&
        formData.username.trim() &&
        formData.email.trim() &&
        formData.password.trim()
      );
    }
    return formData.email.trim() && formData.password.trim();
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
                <label className="rowInput nameInput">
                  <img src={userIcon} width={40} height={40} alt="" />
                  <div className="labelAndField">
                    <span className="labelText">First Name:</span>
                    <input className="fieldInput" type="text" name="name" value={formData.name} onChange={handleInputChange} />
                    <span className="error">{formErrors.name}</span>
                  </div>
                </label>

                <label className="rowInput lastnameInput">
                  <img src={userIcon} width={40} height={40} alt="" />
                  <div className="labelAndField">
                    <span className="labelText">Last Name:</span>
                    <input className="fieldInput" type="text" name="lastname" value={formData.lastname} onChange={handleInputChange} />
                    <span className="error">{formErrors.lastname}</span>
                  </div>
                </label>

                <label className="rowInput usernameInput">
                  <img src={userIcon} width={40} height={40} alt="" />
                  <div className="labelAndField">
                    <span className="labelText">Username:</span>
                    <input className="fieldInput" type="text" name="username" value={formData.username} onChange={handleInputChange} />
                    <span className="error">{formErrors.username}</span>
                  </div>
                </label>
              </>
            )}

            <label className="rowInput emailInput">
              <img src={emailIcon} width={40} height={40} alt="" />
              <div className="labelAndField">
                <span className="labelText">Email:</span>
                <input className="fieldInput" type="email" name="email" value={formData.email} onChange={handleInputChange} />
                <span className="error">{formErrors.email}</span>
              </div>
            </label>

            <label className="rowInput passwordInput">
              <img src={passwordIcon} width={40} height={40} alt="" />
              <div className="labelAndField">
                <span className="labelText">Password:</span>
                <input className="fieldInput" type="password" name="password" value={formData.password} onChange={handleInputChange} />
                <span className="error">{formErrors.password}</span>
              </div>
            </label>

            <div className="submitWrapper">
              <button
                type="submit"
                className={`submitBtn ${isComplete() ? 'enabled' : 'disabled'}`}
                disabled={!isComplete()}
              >
                Submit
              </button>
            </div>
          </form>
        ) : (
          <div className="verification-container">
            <p>We’ve sent a verification code to your email.</p>

            <label className="rowInput verification-row">
              <img src={emailIcon} width={40} height={40} alt="" />
              <div className="labelAndField">
                <span className="labelText">Code:</span>
                <input
                  className="fieldInput"
                  type="text"
                  placeholder="Enter verification code"
                  value={verificationCode}
                  onChange={(e) => setVerificationCode(e.target.value)}
                />
              </div>
            </label>

            <div className="submitWrapper">
              <button className="submitBtn enabled" onClick={handleVerification}>
                Verify Email
              </button>
            </div>
          </div>
        )}

        {errorMsg && <div className="error login-error-msg">{errorMsg}</div>}
      </div>
    </div>
  );
};

export default FormWithValidation;
