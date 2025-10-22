import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./homePage.css"


const HomePage = () => {
    const [mood, setMood] = useState("");
    const [genre, setGenre] = useState("");
    const [bgColor, setBgColor] = useState("");
    const navigate = useNavigate();

    // apply background color from profile (localStorage)
    useEffect(() => {
        try {
            const data = JSON.parse(localStorage.getItem('profileData') || 'null');
            if (data && data.bgColor) setBgColor(data.bgColor);
        } catch (e) {
            // ignore parse errors
        }
    }, []);

    const handleSearch = (e) => {
        e.preventDefault();
        // You can handle the generate logic here
        alert(`Mood: ${mood}, Genre: ${genre}`);
    };

    return (
    <div className="homepage-container" style={bgColor ? { backgroundColor: bgColor } : {}}>
            {/* Login Button Top Right */}
            <div className="homepage-login-btn-topright">
                <button
                    className="homepage-login-btn"
                    onClick={() => navigate("/login")}
                >
                    LOGIN
                </button>
                <button
                    className="homepage-login-btn"
                    style={{ marginLeft: 12 }}
                    onClick={() => navigate("/profile")}
                >
                    PROFILE
                </button>
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
                <button
                    className="homepage-login-btn-over-chat"
                    onClick={() => navigate("/login")}
                >
                    LOGIN
                </button>
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
