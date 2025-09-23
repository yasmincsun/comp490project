// import React from 'react'
import './loginPage.css'
import { useState } from "react";
import userIcon from '../assets/person.png'
import emailIcon from '../assets/email.png'
import passwordIcon from '../assets/password.png'


const loginPage = () => {
    const [action, setAction] = useState("Login"); // Changes login screen
    const [emailSignIn, setEmailSignIn] = useState("");
    const [passwordSignIn, setpasswordSignIn] = useState("");


    return (
        <div className="container">

            <div className="header">
                <div className="text">{action}</div>
                <div className="underline"></div>
            </div>


            <div className="inputs">

                {action === "Login" ? <div></div> :

                    <div className="nameInput">
                        <img src={userIcon} width={40} height={40} alt="" />
                        <input type="text" id="signUpName" placeholder='Name'/>
                    </div>}

                <div className="emailInput">
                    <img src={emailIcon} width={40} height={40} alt="" />
                    <input type="text" id="userEmail" placeholder='Email' value = {emailSignIn} onChange={e => {console.log("Email: ", e.target.value), setEmailSignIn(e.target.value)}}
                     />
                </div>
                <form className = "passwordInput">
                    <img src={passwordIcon} width={40} height={40} alt="" />
                    <label htmlFor="password"></label>
                    <input type="password" id="password" name="password" placeholder ='Password' value = {passwordSignIn} onChange={e => {console.log("Password: ", e.target.value), setpasswordSignIn(e.target.value)}} />
                    </form>

            </div>


            {action === "Sign Up" ? <div></div> : <div className="forgotPassword">Forgot Password? <span>Click Here!</span></div>}
            {action === "Sign Up" ? <div></div> : <div className="noAccount">Don't have an Account? <span>Sign Up!</span> </div>}


            <div className="submitContainer">
                <button className={action === "Login" ? "submit gray" : "submit"} onClick={() => { setAction("Sign Up") }}>Sign Up</button>
                <button className={action === "Sign Up" ? "submit gray" : "submit"} onClick={() => { setAction("Login") }}>Login</button>
                <button type="submit">Submit</button>
            </div>

        </div >
    )
}

export default loginPage; 