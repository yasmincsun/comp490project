import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./friendPage.css";

/**
 * FriendPage Component
 * <p>
 * This page displays a user search feature where users can look up other users
 * and view their profile information in a card-based gallery layout.
 */
const FriendPage = () => {
    const navigate = useNavigate();
    const [bgColor, setBgColor] = useState("");
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");
    const [searchResults, setSearchResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [newResultIds, setNewResultIds] = useState(new Set());

    // Check if user is logged in
    useEffect(() => {
        const token = localStorage.getItem("authToken");
        setIsLoggedIn(!!token);
        if (!token) {
            navigate("/login");
        }
    }, [navigate]);

    // Apply background color from profile (localStorage)
    useEffect(() => {
        try {
            const data = JSON.parse(localStorage.getItem("profileData") || "null");
            if (data && data.bgColor) setBgColor(data.bgColor);
        } catch (e) {
            console.error("Error parsing profile data:", e);
        }
    }, []);

    // Utility: adjust hex color by amount (-255..255)
    const adjustHex = (hex, amt) => {
        try {
            const h = hex.replace("#", "");
            const num = parseInt(h, 16);
            let r = (num >> 16) + amt;
            let g = ((num >> 8) & 0x00ff) + amt;
            let b = (num & 0x0000ff) + amt;
            r = Math.max(Math.min(255, r), 0);
            g = Math.max(Math.min(255, g), 0);
            b = Math.max(Math.min(255, b), 0);
            return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
        } catch (e) {
            return hex;
        }
    };

    // Utility: get a lighter/brighter version of the color for gradient
    const brightenHex = (hex) => {
        try {
            const h = hex.replace("#", "");
            const num = parseInt(h, 16);
            let r = num >> 16;
            let g = (num >> 8) & 0x00ff;
            let b = num & 0x0000ff;
            // lighten by moving toward white
            r = Math.min(255, Math.floor(r * 1.4 + 60));
            g = Math.min(255, Math.floor(g * 1.4 + 60));
            b = Math.min(255, Math.floor(b * 1.4 + 60));
            return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
        } catch (e) {
            return hex;
        }
    };

    // Utility: get complementary color
    const complementaryHex = (hex) => {
        try {
            const h = hex.replace("#", "");
            const num = parseInt(h, 16);
            let r = 255 - (num >> 16);
            let g = 255 - ((num >> 8) & 0x00ff);
            let b = 255 - (num & 0x0000ff);
            return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
        } catch (e) {
            return hex;
        }
    };

    // Utility: convert int color to hex
    const intToHex = (colorInt) => {
        if (colorInt === null || colorInt === undefined) return "#c4dbef";
        const hex = Number(colorInt).toString(16).padStart(6, "0");
        return "#" + hex;
    };

    // Search for users
    const handleSearch = async () => {
        if (!searchQuery.trim()) {
            setSearchResults([]);
            return;
        }

        setLoading(true);
        try {
            const token = localStorage.getItem("authToken");
            const response = await fetch(
                `http://127.0.0.1:8080/api/v1/profile/search?query=${encodeURIComponent(searchQuery)}`,
                {
                    headers: {
                        Authorization: `Bearer ${token || ""}`,
                    },
                }
            );

            if (!response.ok) {
                const msg = await response.text();
                throw new Error(msg || `Search failed: ${response.status}`);
            }

            const results = await response.json();
            setSearchResults(results);
            
            // Mark all results as new for animation
            setNewResultIds(new Set(results.map((r) => r.id)));
            
            // Clear the animation flag after transition
            setTimeout(() => setNewResultIds(new Set()), 360);
        } catch (error) {
            console.error("Search error:", error);
            setSearchResults([]);
        } finally {
            setLoading(false);
        }
    };

    // Handle Enter key in search input
    const handleKeyPress = (e) => {
        if (e.key === "Enter") {
            handleSearch();
        }
    };

    // Determine gradient colors
    const defaultPrimary = "#c4dbefff";
    const defaultSecondary = "#8ab4f8";
    const primary = bgColor || defaultPrimary;
    const brightSecondary = brightenHex(primary);
    const secondary = bgColor ? brightSecondary : defaultSecondary;
    const btnPrimary = complementaryHex(primary);
    const btnSecondary = complementaryHex(secondary);

    return (
        <div
            className="friendpage-container"
            style={{
                "--home-bg1": primary,
                "--home-bg2": secondary,
                "--btn-bg1": btnPrimary,
                "--btn-bg2": btnSecondary,
            }}
        >
            {/* Back button in top left */}
            <div className="friendpage-back-btn">
                <button
                    className="friendpage-btn"
                    onClick={() => navigate("/home")}
                >
                    ← BACK
                </button>
            </div>

            {/* Search bar at top */}
            <div className="friendpage-search-bar-container">
                <div className="friendpage-search-bar">
                    <input
                        type="text"
                        placeholder="Search for a user..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyPress={handleKeyPress}
                        className="friendpage-search-input"
                    />
                    <button
                        onClick={handleSearch}
                        className="friendpage-search-btn"
                        disabled={loading}
                        title="Search"
                    >
                        →
                    </button>
                </div>
            </div>

            {/* Search results gallery */}
            {searchResults.length > 0 && (
                <div className="friendpage-gallery-container">
                    <div className="friendpage-results-gallery">
                        {searchResults.map((user) => (
                            <div
                                key={user.id}
                                className={`friendpage-user-card ${newResultIds.has(user.id) ? 'new' : ''}`}
                            >
                                <div className="friendpage-user-avatar">
                                    {user.profileImageUrl ? (
                                        <img src={user.profileImageUrl} alt={user.username} onError={(e) => { e.target.style.display = 'none'; }} />
                                    ) : (
                                        <div className="friendpage-avatar-placeholder">No Photo</div>
                                    )}
                                </div>
                                <div className="friendpage-user-info">
                                    <div className="friendpage-user-username">{user.username}</div>
                                    {user.bio && <div className="friendpage-user-bio">{user.bio}</div>}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Two-column layout (kept for future use) */}
            <div className="friendpage-content">
                {/* Left column: Blank for now */}
                <div className="friendpage-left"></div>

                {/* Right column: Blank for now */}
                <div className="friendpage-right"></div>
            </div>
        </div>
    );
};

export default FriendPage;
