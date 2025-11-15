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
    const [genre, setGenre] = useState("");
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

    const handleSearch = (e) => {
        e.preventDefault();
        // You can handle the generate logic here
        alert(`Mood: ${mood}, Genre: ${genre}`);
    };

        // 🔹 Handle logout
    const handleLogout = async () => {
        try {
            const token = localStorage.getItem("authToken");
            await fetch("http://localhost:8080/api/v1/authentication/logout", {
                method: "PUT",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });
            localStorage.removeItem("authToken");; // clear JWT
            setIsLoggedIn(false);
            navigate("/login");
        } catch (error) {
            console.error("Logout failed:", error);
        }
    };

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
                    <input
                        type="text"
                        placeholder="How are you feeling today?"
                        value={mood}
                        onChange={e => setMood(e.target.value)}
                        className="homepage-input-top"
                    />
                    <div className="homepage-genre-select-top">
                        <label>Select a genre:</label>
                        <select
                            value={genre}
                            onChange={e => setGenre(e.target.value)}
                            className="homepage-select-top"
                        >
                            <option value="">Choose genre</option>
                            <option value="pop">Pop</option>
                            <option value="rap">Rap</option>
                            <option value="country">Country</option>
                            <option value="indie">Indie</option>
                            <option value="alternative">Alternative</option>
                        </select>
                    </div>
                    <button type="submit" className="homepage-generate-btn">Generate</button>
                </form>
            </div>

            {/* Left Sidebar */}
            <div className="homepage-sidebar homepage-sidebar-left homepage-sidebar-lower homepage-sidebar-custom">
                <h2>Playlist</h2>
                {/* Playlist content here */}
            </div>

            {/* Right Sidebar */}
            <div className="homepage-sidebar homepage-sidebar-right homepage-sidebar-lower homepage-sidebar-custom">
                <h2>Chat</h2>
                {/* Chat content here */}
                {/* Login button overlays the chat sidebar */}
                {/* <button
                    className="homepage-login-btn-over-chat"
                    onClick={() => navigate("/login")}
                >
                    LOGIN
                </button> */}
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
