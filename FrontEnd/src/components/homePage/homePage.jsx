import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./homePage.css";

// list of moods used by the dropdown
const MOODS = [
    "happy","chill","pumped","melancholic","romantic","nostalgic","energetic","peaceful","dark","motivated","sad","angry","relaxed","hopeful","lonely","mysterious","gritty","groovy","wild","epic","focused","funny","spiritual","rebellious","cozy","adventurous","playful","mellow","party","sentimental","country","spooky","heroic","sexy","moody","motivational","creative","high","free"
];

// Mood-to-gradient-colors mapping: each mood maps to [color1, color2, color3] for animated gradients
const MOOD_COLORS = {
    "happy": ["#FFD700", "#FFA500", "#FF6347"],
    "chill": ["#87CEEB", "#20B2AA", "#5F9EA0"],
    "pumped": ["#FF4500", "#DC143C", "#FFD700"],
    "melancholic": ["#4B0082", "#8B008B", "#4169E1"],
    "romantic": ["#FF69B4", "#FF1493", "#DA70D6"],
    "nostalgic": ["#DEB887", "#CD853F", "#DAA520"],
    "energetic": ["#FF4500", "#FFD700", "#FF1493"],
    "peaceful": ["#20B2AA", "#90EE90", "#98FB98"],
    "dark": ["#1C1C1C", "#2F4F4F", "#191970"],
    "motivated": ["#FF6347", "#FFD700", "#00CED1"],
    "sad": ["#4169E1", "#6A5ACD", "#8B7EC8"],
    "angry": ["#DC143C", "#FF0000", "#8B0000"],
    "relaxed": ["#66CDAA", "#98FB98", "#F0FFF0"],
    "hopeful": ["#FFD700", "#FFA500", "#87CEEB"],
    "lonely": ["#696969", "#778899", "#A9A9A9"],
    "mysterious": ["#2F4F4F", "#4B0082", "#191970"],
    "gritty": ["#696969", "#A0522D", "#8B4513"],
    "groovy": ["#8A2BE2", "#FF7F50", "#20B2AA"],
    "wild": ["#FF4500", "#FF1493", "#00CED1"],
    "epic": ["#0f172a", "#1f2937", "#f59e0b"],
    "focused": ["#1C1C1C", "#4169E1", "#00CED1"],
    "funny": ["#FFD700", "#FF69B4", "#00FF00"],
    "spiritual": ["#9370DB", "#DDA0DD", "#E6B0FF"],
    "rebellious": ["#FF0000", "#000000", "#FFD700"],
    "cozy": ["#DAA520", "#CD853F", "#F4A460"],
    "adventurous": ["#FF4500", "#00CED1", "#32CD32"],
    "playful": ["#FF69B4", "#FFD700", "#32CD32"],
    "mellow": ["#DEB887", "#F08080", "#FFB6C1"],
    "party": ["#FF00FF", "#00FFFF", "#FFD700"],
    "sentimental": ["#FFB6C1", "#DDA0DD", "#B0C4DE"],
    "country": ["#8B4513", "#DAA520", "#CD853F"],
    "spooky": ["#2F4F4F", "#8B008B", "#FF8C00"],
    "heroic": ["#FFD700", "#FF6347", "#4169E1"],
    "sexy": ["#FF1493", "#DC143C", "#8B008B"],
    "moody": ["#4B0082", "#696969", "#778899"],
    "motivational": ["#32CD32", "#FFD700", "#FF6347"],
    "creative": ["#9370DB", "#FF69B4", "#00CED1"],
    "high": ["#00FF00", "#FFD700", "#FF6347"],
    "free": ["#87CEEB", "#00FF00", "#FFD700"],
    "default": ["#87CEEB", "#20B2AA", "#5F9EA0"]
};

// Ensure every mood from MOODS has at least a fallback palette
MOODS.forEach((m) => {
    const key = m.toLowerCase();
    if (!MOOD_COLORS[key]) MOOD_COLORS[key] = MOOD_COLORS['default'];
});

/**
 * Declares a constant variable named HomePage
 * <p> 
 * This function displays and runs the entirety of the Home Page. This page contains information about generating playlists and collecting information from the user’s Spotify account. The user’s Spotify playlists are displayed on the home page. Personalized features, like the user-chosen background	color, are also displayed here. 
 */
const HomePage = () => {
    const [mood, setMood] = useState("");
    const [bgColor, setBgColor] = useState("");
    const [errorMsg, setErrorMsg] = useState("");
    const [generatedMoods, setGeneratedMoods] = useState([]); // newest first
    const [newMoodId, setNewMoodId] = useState(null); // for entry animation
    const [isLoggedIn, setIsLoggedIn] = useState(false); // Track login status
    const [openDropdown, setDropdownOpen] = useState(false);
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

    // utility: adjust hex color by amount (-255..255)
    const adjustHex = (hex, amt) => {
        try {
            const h = hex.replace('#','');
            const num = parseInt(h,16);
            let r = (num >> 16) + amt;
            let g = ((num >> 8) & 0x00FF) + amt;
            let b = (num & 0x0000FF) + amt;
            r = Math.max(Math.min(255, r), 0);
            g = Math.max(Math.min(255, g), 0);
            b = Math.max(Math.min(255, b), 0);
            return '#'+( (1<<24) + (r<<16) + (g<<8) + b ).toString(16).slice(1);
        } catch (e) {
            return hex;
        }
    };

    // utility: get a lighter/brighter version of the color for gradient
    const brightenHex = (hex) => {
        try {
            const h = hex.replace('#','');
            const num = parseInt(h,16);
            let r = (num >> 16);
            let g = ((num >> 8) & 0x00FF);
            let b = (num & 0x0000FF);
            // lighten by moving toward white
            r = Math.min(255, Math.floor(r * 1.4 + 60));
            g = Math.min(255, Math.floor(g * 1.4 + 60));
            b = Math.min(255, Math.floor(b * 1.4 + 60));
            return '#'+( (1<<24) + (r<<16) + (g<<8) + b ).toString(16).slice(1);
        } catch(e){
            return hex;
        }
    };

    // utility: get complementary color by inverting hue via simple RGB invert fallback
    const complementaryHex = (hex) => {
        try {
            const h = hex.replace('#','');
            const num = parseInt(h,16);
            let r = 255 - (num >> 16);
            let g = 255 - ((num >> 8) & 0x00FF);
            let b = 255 - (num & 0x0000FF);
            // lighten the inverted color so it's not too dark (only half as dark)
            r = Math.min(255, Math.floor(r * 0.7 + 76));
            g = Math.min(255, Math.floor(g * 0.7 + 76));
            b = Math.min(255, Math.floor(b * 0.7 + 76));
            return '#'+( (1<<24) + (r<<16) + (g<<8) + b ).toString(16).slice(1);
        } catch(e){
            return hex;
        }
    }

    // utility: return CSS vars for the mood gradient
    const getMoodGradientColors = (moodName) => {
        if (!moodName) moodName = 'default';
        const key = ('' + moodName).toLowerCase();
        const colors = MOOD_COLORS[key] || MOOD_COLORS['default'];
        return {
            '--mood-color1': colors[0],
            '--mood-color2': colors[1],
            '--mood-color3': colors[2]
        };
    };

   const handleSearch = async (e) => {
    e.preventDefault();

    // validation: require mood
    if (!mood) {
        setErrorMsg('Please select a mood before generating.');
        return;
    }
    setErrorMsg('');

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

        // On successful generation, add the mood to the gallery (newest first)
        const id = Date.now() + Math.floor(Math.random() * 1000);
        const newEntry = { id, mood };
        setGeneratedMoods((prev) => [newEntry, ...prev]);
        setNewMoodId(id);

        // briefly keep dropdown closed and clear errors
        setDropdownOpen(false);
        // reset the question prompt back to default
        setMood('');
        setErrorMsg('');

        // clear the transient newMoodId after animation duration
        setTimeout(() => setNewMoodId(null), 600);
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

    // determine gradient colors (defaults if no profile color)
    const defaultPrimary = '#c4dbefff';
    const defaultSecondary = '#8ab4f8'; // changed to a notably different blue to create visible gradient movement
    const primary = bgColor || defaultPrimary;
    const brightSecondary = brightenHex(primary); // always brighten the primary for gradient contrast
    const secondary = bgColor ? brightSecondary : defaultSecondary;
    const btnPrimary = complementaryHex(primary);
    const btnSecondary = complementaryHex(secondary);

    return (
        <div
            className="homepage-container"
            style={{
                '--home-bg1': primary,
                '--home-bg2': secondary,
                '--btn-bg1': btnPrimary,
                '--btn-bg2': btnSecondary,
            }}
        >
            {/* 🔹 Top-left Feed Button */}
            {isLoggedIn && (
                <div className="homepage-feed-btn-topleft">
                    <button
                        className="homepage-login-btn"
                        onClick={() => navigate("/friends")}
                    >
                        FRIENDS
                    </button>
                    <button
                        className="homepage-login-btn"
                        style={{ marginLeft: 12 }}
                        onClick={() => { /* intentionally no-op for now */ }}
                    >
                        SOCIAL
                    </button>
                    <button
                        className="homepage-login-btn"
                        style={{ marginLeft: 12 }}
                        onClick={() => { /* intentionally no-op for now */ }}
                    >
                        MAP
                    </button>
                </div>
            )}

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
            <div className={`homepage-searchbar-top ${generatedMoods.length > 0 ? 'moved-to-top' : 'centered'}`}>
                <form className="homepage-form-top" onSubmit={handleSearch}>
                    <div className="dropdown-wrapper">
                        <div className="custom-dropdown" tabIndex={0} onBlur={() => setDropdownOpen(false)}>
                            <div className="dropdown-selected" onClick={() => setDropdownOpen(open => !open)}>
                                {mood ? mood.charAt(0).toUpperCase() + mood.slice(1) : 'How are you feeling today?'}
                                <span className="dropdown-caret">▾</span>
                            </div>
                            {openDropdown && (
                                <div className="dropdown-options">
                                    {MOODS.map((opt) => (
                                        <div
                                            key={opt}
                                            className="dropdown-option"
                                            onMouseDown={(e) => { e.preventDefault(); setMood(opt); setDropdownOpen(false); setErrorMsg(''); }}
                                        >
                                            {opt.charAt(0).toUpperCase() + opt.slice(1)}
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                    <button type="submit" className="homepage-generate-btn">Generate</button>
                </form>
                {errorMsg && <div className="homepage-error-message">{errorMsg}</div>}
            </div>

    

            {/* Mood Gallery (shows after at least one generation) */}
            {generatedMoods.length > 0 && (
                <div className="homepage-gallery-container">
                    <div className="homepage-mood-gallery">
                        {generatedMoods.map((entry) => (
                            <div
                                key={entry.id}
                                className={`homepage-mood-box ${entry.id === newMoodId ? 'new' : ''}`}
                                style={getMoodGradientColors(entry.mood)}
                            >
                                <p className="homepage-mood-text">{entry.mood.charAt(0).toUpperCase() + entry.mood.slice(1)}</p>
                            </div>
                        ))}
                    </div>
                </div>
            )}
            {errorMsg && <div className="homepage-error-message">{errorMsg}</div>}
        </div>
    );
};

export default HomePage;
