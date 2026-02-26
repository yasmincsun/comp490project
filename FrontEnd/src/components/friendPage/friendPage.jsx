import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./friendPage.css";
import magGlass from "../assets/magGlass.png";


const FriendPage = () => {
    const navigate = useNavigate();
    const [bgColor, setBgColor] = useState("");
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");
    const [searchResults, setSearchResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [newResultIds, setNewResultIds] = useState(new Set());
    const [hasSearched, setHasSearched] = useState(false);

    // Check if user is logged in
    useEffect(() => {
        const token = localStorage.getItem("authToken");
        setIsLoggedIn(!!token);
        if (!token) {
            navigate("/login");
        }
    }, [navigate]);

    // Fetch current user's profile color from backend (no localStorage)
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

    useEffect(() => {
        (async () => {
            const user = await fetchProfile();
            if (user && user.color != null) {
                const hex = "#" + Number(user.color).toString(16).padStart(6, "0");
                setBgColor(hex);
            }
        })();
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
            setHasSearched(true);
            
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

    // Determine gradient colors (use user's selected color if available)
    const defaultPrimary = "#c4dbef";
    const defaultSecondary = "#8ab4f8";
    const primary = bgColor || defaultPrimary;
    const secondary = bgColor ? brightenHex(primary) : defaultSecondary;
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

            {/* Animated search bar container */}
            <div
                className={`friendpage-search-bar-top ${hasSearched ? "moved-to-top" : "centered"}`}
            >
                <h3 className="friendpage-search-title">Search for Friends</h3>
                {!hasSearched && (
                    <p className="friendpage-search-tagline">Find and connect with your friends by searching their usernames</p>
                )}
                {/* Show center magnifier when initially on page OR when a search returned no results */}
                {(!hasSearched || (hasSearched && searchResults.length === 0)) && (
                    <>
                        <img src={magGlass} alt="Search" className="friendpage-empty-glass" />
                        {hasSearched && searchResults.length === 0 && (
                            <p style={{ marginTop: 8, color: '#333', fontWeight: 600 }}>No users found. Try searching using a different search term!</p>
                        )}
                    </>
                )}
                <div className="friendpage-search-form">
                    <input
                        type="text"
                        placeholder={hasSearched ? "Search for a user..." : "Enter a username..."}
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyPress={handleKeyPress}
                        className="friendpage-search-input-centered"
                    />
                    <button
                        onClick={handleSearch}
                        className="friendpage-search-btn-centered"
                        disabled={loading}
                        title="Search"
                    >
                        {loading ? "..." : "→"}
                    </button>
                </div>
            </div>

            {/* Search results gallery */}
            {hasSearched && searchResults.length > 0 && (
                <div className="friendpage-results-container">
                    <div className="friendpage-results-gallery">
                        {searchResults.map((user) => (
                            <div
                                key={user.id}
                                className={`friendpage-user-card ${newResultIds.has(user.id) ? 'new' : ''}`}
                                style={{
                                    // use user's stored color if present AND not the default account color (12901359)
                                    backgroundColor: (() => {
                                        const stored = user.bgColor ?? user.color ?? null;
                                        const intVal = stored != null ? Number(stored) : null;
                                        if (intVal && intVal !== 12901359) return intToHex(intVal);
                                        // fallback profile preview background
                                        return "rgba(255, 255, 255, 0.92)";
                                    })()
                                }}
                            >
                                {/* Support multiple possible backend formats for online status */}
                                {(() => {
                                    const isActive = user.loginStatus === 1 || user.loginStatus === true || user.login_status === 1 || user.login_status === true || user.online === true || user.loggedIn === true;
                                    return (
                                        <div className="friendpage-active-indicator-wrapper">
                                            <div className={isActive ? 'friendpage-active-indicator' : 'friendpage-inactive-indicator'}></div>
                                            <span className={isActive ? 'friendpage-active-tooltip' : 'friendpage-inactive-tooltip'}>{isActive ? 'Active' : 'Inactive'}</span>
                                        </div>
                                    );
                                })()}
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
        </div>
    );
};

export default FriendPage;
