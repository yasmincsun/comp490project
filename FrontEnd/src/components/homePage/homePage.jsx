import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./homePage.css"

/**
 * Declares a constant variable named HomePage
 * <p> 
 * This function displays and runs the entirety of the Home Page. This page contains information about generating playlists and collecting information from the user’s Spotify account. The user’s Spotify playlists are displayed on the home page. Personalized features, like the user-chosen background	color, are also displayed here. 
 */
const HomePage = () => {
    const [mood, setMood] = useState("");
    const [bgColor, setBgColor] = useState("");
    const [isLoggedIn, setIsLoggedIn] = useState(false); // Track login status
    const navigate = useNavigate();

/**
 * Sets up the Home Page environment
 * <p>
 * This function checks whether or not the user is logged into their account, and stores their Spotify API token to their local account. This function also receives the user’s inputted mood and genre choice to generate a playlist. The received values are then displayed back to the user in a dialogue box.  
 */
    useEffect(() => {
        const token = localStorage.getItem("authToken"); // Make sure this matches how you store your JWT
        setIsLoggedIn(!!token);
    }, []);

    // apply background color from profile (localStorage)
    useEffect(() => {
        try {
            const data = JSON.parse(localStorage.getItem('profileData') || 'null');
            if (data && data.bgColor) setBgColor(data.bgColor);
        } catch (e) {
            console.error("Error parsing profile data:", e);
        }
    }, []);

   const handleSearch = async (e) => {
    e.preventDefault();

    try {

        const response = await fetch(`http://127.0.0.1:8080/playlist/from-mood?mood=${mood}&limit=10`, {
         //const response = await fetch(`http://127.0.0.1:8080/mood/by?mood=${mood}`, {
        method: "GET",
        credentials: "include"
    });


        if (!response.ok) {
            throw new Error("Backend returned error: " + response.status);
        }

        const data = await response.json();
        console.log("Mood response:", data);

        alert("Mood sent: " + mood);
    } catch (err) {
        console.error("Error:", err);
        alert("Failed to fetch recommendations.");
    }
};


        // 🔹 Handle logout

const handleLogout = async () => {
  try {
    const token = localStorage.getItem("authToken");
    const res = await fetch(`http://127.0.0.1:8080/api/v1/authentication/logout`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!res.ok) {
      throw new Error(`Logout failed with status ${res.status}`);
    }

    localStorage.removeItem("authToken");
    setIsLoggedIn(false);
    navigate("/login");
  } catch (error) {
    console.error("Logout failed:", error);
    alert("Logout failed. Please try again.");
  }
};



    // const handleLogout = async () => {
    //     try {
    //         const token = localStorage.getItem("authToken");
    //         await fetch("http://localhost:8080/api/v1/authentication/logout", {
    //             method: "PUT",
    //             headers: {
    //                 "Authorization": `Bearer ${token}`,
    //                 "Content-Type": "application/json",
    //             },
    //         });
    //         localStorage.removeItem("authToken");; // clear JWT
    //         setIsLoggedIn(false);
    //         navigate("/login");
    //     } catch (error) {
    //         console.error("Logout failed:", error);
    //     }
    // };

/**
 * Displays the Home Page and runs all of the components 
 * <p>
 * This function displays and runs the entirety of the Home Page. This method checks the user’s login status and provides the user with the Playlist generation function.
* @return Home Page display to the Web Page
 */

    return (
        <div className="homepage-container" style={bgColor ? { backgroundColor: bgColor } : {}}>
            {/* 🔹 Top-right Buttons */}
            <div className="homepage-login-btn-topright">
                <button
                    className="homepage-login-btn"
                    onClick={async () => {
                        if (isLoggedIn) {
                            await handleLogout(); // logout and redirect
                        } else {
                            navigate("/login"); // go to login page
                        }
                    }}
                >
                    {isLoggedIn ? "LOGOUT" : "LOGIN"}
                </button>

                {isLoggedIn && (
                    <button
                        className="homepage-login-btn"
                        style={{ marginLeft: 12 }}
                        onClick={() => navigate("/profile")}
                    >
                        PROFILE
                    </button>
                )}
            </div>

                
    
            {/* Search Bar at Very Top */}
            <div className="homepage-searchbar-top">
                <form className="homepage-form-top" onSubmit={handleSearch}>
                    <select
                        value={mood}
                        onChange={e => setMood(e.target.value)}
                        className="form-control homepage-select-top"
                    >
                        <option value="">How are you feeling today?</option>
                        <option value="happy">Happy</option>
                        <option value="chill">Chill</option>
                        <option value="pumped">Pumped</option>
                        <option value="melancholic">Melancholic</option>
                        <option value="romantic">Romantic</option>
                        <option value="nostalgic">Nostalgic</option>
                        <option value="energetic">Energetic</option>
                        <option value="peaceful">Peaceful</option>
                        <option value="dark">Dark</option>
                        <option value="motivated">Motivated</option>
                        <option value="sad">Sad</option>
                        <option value="angry">Angry</option>
                        <option value="relaxed">Relaxed</option>
                        <option value="hopeful">Hopeful</option>
                        <option value="lonely">Lonely</option>
                        <option value="mysterious">Mysterious</option>
                        <option value="gritty">Gritty</option>
                        <option value="groovy">Groovy</option>
                        <option value="wild">Wild</option>
                        <option value="epic">Epic</option>
                        <option value="focused">Focused</option>
                        <option value="funny">Funny</option>
                        <option value="spiritual">Spiritual</option>
                        <option value="rebellious">Rebellious</option>
                        <option value="cozy">Cozy</option>
                        <option value="adventurous">Adventurous</option>
                        <option value="playful">Playful</option>
                        <option value="mellow">Mellow</option>
                        <option value="party">Party</option>
                        <option value="sentimental">Sentimental</option>
                        <option value="country">Country</option>
                        <option value="spooky">Spooky</option>
                        <option value="heroic">Heroic</option>
                        <option value="sexy">Sexy</option>
                        <option value="moody">Moody</option>
                        <option value="motivational">Motivational</option>
                        <option value="creative">Creative</option>
                        <option value="high">High</option>
                        <option value="free">Free</option>

                    </select>
                    <button type="submit" className="homepage-generate-btn">Generate</button>     {/* Change this */}
                </form>
            </div>

    
            {/* Main Content */}
            <div className="homepage-main">
                {/* Spotify Login Question */}
                <div className="homepage-spotify-question">
                    Want your playlist personalized to your taste?{' '}
                    <span
                        className="homepage-spotify-link"
                        style={{ color: '#1DB954', cursor: 'pointer', textDecoration: 'underline' }}
                        onClick={() => navigate('/login')}
                    >
                        Login with your Spotify
                    </span>
                </div>
            </div>
        </div>
    );
};

export default HomePage;
