// import React from 'react'

import './loginPage.css'
import { useState } from "react";
import userIcon from '../assets/person.png'
import emailIcon from '../assets/email.png'
import passwordIcon from '../assets/password.png'

const loginPage = () => {
    const [action, setAction] = useState("Login");


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
                        <input type="text" id="signUpName" placeholder='Name' />
                    </div>}

                <div className="emailInput">
                    <img src={emailIcon} width={40} height={40} alt="" />
                    <input type="email" id="userEmail" placeholder='Email' />
                </div>

                <div className="passwordInput">
                    <img src={passwordIcon} width={40} height={40} alt="" />
                    <input type="password" id="userPassword" placeholder='Password' />
                </div>


            </div>


            {action === "Sign Up" ? <div></div> : <div className="forgotPassword">Forgot Password? <span>Click Here!</span></div>}
            {action === "Sign Up" ? <div></div> : <div className="noAccount">Don't have an Account? <span>Sign Up!</span> </div>}


            <div className="submitContainer">
                <button className={action === "Login" ? "submit gray" : "submit"} onClick={() => { setAction("Sign Up") }}>Sign Up</button>
                <button className={action === "Sign Up" ? "submit gray" : "submit"} onClick={() => { setAction("Login") }}>Login</button>
            </div>

        </div >
    )
}

export default loginPage; 