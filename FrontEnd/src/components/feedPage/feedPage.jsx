//author: Miguel A.
//version: 1.01
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./postPage.css";

const feedPage = () => {
    //Boilerplate from homePage.jsx, will be used to fetch profile color and display posts
    const [bgColor, setBgColor] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [isLoggedIn, setIsLoggedIn] = useState(false); // Track login status


 // fetch current user's profile color from backend (do not use localStorage)
    const fetchProfile = async () => {
        try {
            const token = localStorage.getItem("authToken");
            if (!token) return null;
            const res = await fetch("http://127.0.0.1:8080/api/v1/profile", {
                headers: { Authorization: `Bearer ${token || ""}` },
            });
            if (!res.ok) return null;
            return await res.json();
        } catch (e) {
            console.error("Could not load profile from backend:", e);
            return null;
        }
    };

    //Implement Post fetching logic here
    //User will click on the social button and see a feed in the center column.
    //Users should be able to see posts in a feed format, startin with most recent
    //On the right side, there will be a button to create a new post, which will open go to the post creation page (postPage.jsx)
    
    const fetchPosts = async () => {
        try{
            const token = localStorage.getItem("authToken");
            if (!token) return;
            const res = await fetch("http://127.0.0.1:8080/api/posts", {
                headers: { Authorization: `Bearer ${token || ""}` },
            });
            if (!res.ok) {
                console.error("Failed to fetch posts:", res.statusText);
                return;
            }
            return await res.json();
        } catch (e) {
            console.error("Could not load posts from backend:", e);
            return null;
        }
    };

//Page elements

return(

    <div className="feedpage-container"
    style={{
        "--home-bg-1": primary,
        "--home-bg-2": secondary,
        
    }}
    >
        <div className="feedpage-content">
            <button type="button" className="feedPage-back-btn" onClick={() => navigate("/home")}>← BACK</button>
            <h1>Feed Page</h1>
            <button type="button" className="feedPage-createPost-btn" onClick={() => navigate("/post")}>Create Post</button>
        </div>


    </div>









    );


}; 

export default feedPage;