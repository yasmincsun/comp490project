//author: Miguel A.
//version: 1.01
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./postPage.css";

/**
 * FeedPage component.
 * Intended to display a social feed of posts and provide navigation to post creation.
 * Loads the current user's profile color and fetches posts from the backend.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
const feedPage = () => {
    //Boilerplate from homePage.jsx, will be used to fetch profile color and display posts
    const [bgColor, setBgColor] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [isLoggedIn, setIsLoggedIn] = useState(false); // Track login status


 /**
     * Fetch the current user's profile color from the backend.
     * @returns profile object or null on failure
     */
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

    /**
     * Fetch social posts for the feed from the backend.
     * @returns list of post objects or null on failure
     */
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