import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./friendPage.css";
import magGlass from "../assets/magGlass.png";


/**
 * FriendPage component.
 * Provides friend search, friend request creation, and friend activity feed display.
 * Loads profile color and current user data from the backend.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
const FriendPage = () => {
    const navigate = useNavigate();
    const [bgColor, setBgColor] = useState("");
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");
    const [searchResults, setSearchResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [newResultIds, setNewResultIds] = useState(new Set());
    const [hasSearched, setHasSearched] = useState(false);
    const [currentUserId, setCurrentUserId] = useState(null);
    const [addingFriend, setAddingFriend] = useState(null);
    const [notification, setNotification] = useState(null);
    const [addedFriendIds, setAddedFriendIds] = useState(new Set());
    const [searchOpen, setSearchOpen] = useState(false);
    const [friendActivity, setFriendActivity] = useState([]);
    const [loadingActivity, setLoadingActivity] = useState(false);
    const [activityError, setActivityError] = useState(null);

    // Check if user is logged in
    useEffect(() => {
        const token = localStorage.getItem("authToken");
        setIsLoggedIn(!!token);
        if (!token) {
            navigate("/login");
        }
    }, [navigate]);

    /**
     * Load the current user's profile from the backend.
     * @returns profile object or null on error
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

    useEffect(() => {
        (async () => {
            const user = await fetchProfile();
            if (user && user.color != null) {
                const hex = "#" + Number(user.color).toString(16).padStart(6, "0");
                setBgColor(hex);
            }
            if (user && user.id) {
                setCurrentUserId(user.id);
            }
        })();
    }, []);

    useEffect(() => {
        if (!currentUserId) return;
        const loadActivity = async () => {
            setLoadingActivity(true);
            setActivityError(null);
            try {
                const token = localStorage.getItem("authToken");
                const response = await fetch(`http://127.0.0.1:8080/api/v1/friendship/activity/${currentUserId}`, {
                    headers: {
                        Authorization: `Bearer ${token || ""}`,
                    },
                });
                if (!response.ok) {
                    const msg = await response.text();
                    throw new Error(msg || `Activity fetch failed: ${response.status}`);
                }
                const activity = await response.json();
                setFriendActivity(activity);
            } catch (error) {
                console.error("Error loading friend activity:", error);
                setActivityError("Could not load your friends' activity right now.");
            } finally {
                setLoadingActivity(false);
            }
        };

        loadActivity();
    }, [currentUserId]);

    /**
     * Close the search UI and reset search state.
     */
    const closeSearch = () => {
        setSearchOpen(false);
        setHasSearched(false);
        setSearchQuery("");
        setSearchResults([]);
    };

    /**
     * Adjust the brightness of a hex color by a signed amount.
     * @param hex source hex color value
     * @param amt brightness adjustment amount
     * @returns adjusted hex color string
     */
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

    /**
     * Brighten a hex color for use in the UI theme.
     * @param hex source hex color value
     * @returns brightened hex color string
     */
    const brightenHex = (hex) => {
        try {
            const h = hex.replace("#", "");
            const num = parseInt(h, 16);
            let r = num >> 16;
            let g = (num >> 8) & 0x00ff;
            let b = num & 0x0000ff;
            r = Math.min(255, Math.floor(r * 1.4 + 60));
            g = Math.min(255, Math.floor(g * 1.4 + 60));
            b = Math.min(255, Math.floor(b * 1.4 + 60));
            return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
        } catch (e) {
            return hex;
        }
    };

    /**
     * Compute a complementary color for button contrast.
     * @param hex source hex color value
     * @returns complementary hex color string
     */
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

    // Get contrasting text color based on background brightness
    /**
     * Return a contrasting text color for the given background.
     * @param hexColor background color in hex format
     * @returns dark or light text color string
     */
    const getContrastingTextColor = (hexColor) => {
        try {
            const h = hexColor.replace("#", "");
            const num = parseInt(h, 16);
            const r = (num >> 16) & 255;
            const g = (num >> 8) & 255;
            const b = num & 255;
            // Calculate luminance
            const luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
            // Return dark text for light backgrounds, light text for dark backgrounds
            return luminance > 0.5 ? "#1a1a1a" : "#ffffff";
        } catch (e) {
            return "#333333";
        }
    };

    const intToHex = (colorInt) => {
        if (colorInt === null || colorInt === undefined) return "#c4dbef";
        const hex = Number(colorInt).toString(16).padStart(6, "0");
        return "#" + hex;
    };

    const isUserActive = (user) => {
        const status = user.loginStatus ?? user.login_status;
        return status === true || status === "1" || status === 1 || status === "true" || status === "TRUE";
    };

    /**
     * Send a friend request to a user.
     * @param friendUserId id of the user to add as a friend
     */
    const handleAddFriend = async (friendUserId) => {
        if (!currentUserId) {
            setNotification({ type: 'error', message: 'Could not load your profile. Please refresh.' });
            return;
        }

        setAddingFriend(friendUserId);
        try {
            const token = localStorage.getItem("authToken");
            const response = await fetch("http://127.0.0.1:8080/api/v1/friendship/add", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token || ""}`,
                },
                body: JSON.stringify({
                    user1_id: currentUserId,
                    user2_id: friendUserId,
                }),
            });

            if (!response.ok) {
                const msg = await response.text();
                setNotification({ type: 'error', message: msg || `Failed to add friend: ${response.status}` });
            } else {
                setAddedFriendIds(new Set([...addedFriendIds, friendUserId]));
                setNotification({ type: 'success', message: 'Friend request sent! They must accept it before you become friends.' });
            }
        } catch (error) {
            console.error("Error adding friend:", error);
            setNotification({ type: 'error', message: 'Error adding friend. Please try again.' });
        } finally {
            setAddingFriend(null);
            // Clear notification after 3 seconds
            setTimeout(() => setNotification(null), 3000);
        }
    };

    /**
     * Search for users by query string.
     * Populates the search results list.
     */
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

    /**
     * Trigger search on enter key press.
     * @param e keyboard event
     */
    const handleKeyPress = (e) => {
        if (e.key === "Enter") {
            handleSearch();
        }
    };

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
            {/* Notification Banner */}
            {notification && (
                <div className={`friendpage-notification friendpage-notification-${notification.type}`}>
                    {notification.message}
                </div>
            )}

            {/* Back button in top left */}
            <div className="friendpage-back-btn">
                <button
                    className="friendpage-btn"
                    onClick={() => navigate("/home")}
                >
                    ← BACK
                </button>
            </div>

            {!searchOpen && (
                <div className="friendpage-search-toggle">
                    <button
                        className="friendpage-search-toggle-btn"
                        onClick={() => setSearchOpen(true)}
                        title="Search friends"
                    >
                        <img src={magGlass} alt="Search friends" />
                    </button>
                </div>
            )}

            {!searchOpen && (
                <div className="friendpage-activity-section">
                    <div className="friendpage-activity-wrapper">
                        <div className="friendpage-activity-header">
                            <div>
                                <h3 className="friendpage-activity-title">Friends' Activity</h3>
                                <p className="friendpage-activity-subtitle">Recent posts and reviews from your accepted friends, newest first.</p>
                            </div>
                        </div>

                        {loadingActivity && (
                            <div className="friendpage-activity-loading">Loading friend activity…</div>
                        )}

                        {activityError && (
                            <div className="friendpage-activity-error">{activityError}</div>
                        )}

                        {!loadingActivity && !activityError && friendActivity.length === 0 && (
                            <div className="friendpage-activity-empty">No recent activity from friends yet. Search for friends with the magnifying glass to see their latest posts and reviews.</div>
                        )}

                        <div className="friendpage-activity-list">
                        {friendActivity.map((item) => (
                            <div key={`${item.type}-${item.id}`} className="friendpage-activity-card">
                                <div className="friendpage-activity-row">
                                    <span className="friendpage-activity-label">{item.type === "post" ? "Post" : "Review"}</span>
                                    <span className="friendpage-activity-time">{item.createdAt || item.datePosted}</span>
                                </div>
                                <div className="friendpage-activity-author">{item.author}</div>
                                {item.type === "post" ? (
                                    <>
                                        {item.content && <div className="friendpage-activity-content">{item.content}</div>}
                                        {item.picture && <img src={item.picture} alt="Friend post" className="friendpage-activity-image" />}
                                    </>
                                ) : (
                                    <>
                                        <div className="friendpage-activity-content">{item.comment}</div>
                                        <div className="friendpage-activity-meta">Rating: {item.rating} · {item.targetName}</div>
                                    </>
                                )}
                            </div>
                        ))}
                        </div>
                    </div>
                </div>
            )}

            {searchOpen && (
                <div
                    className={`friendpage-search-bar-top ${hasSearched ? "moved-to-top" : "centered"}`}
                >
                    <div className="friendpage-search-top-row">
                        <h3 className="friendpage-search-title">Search for Friends</h3>
                        <button className="friendpage-search-close-btn" onClick={closeSearch} title="Close search">×</button>
                    </div>
                    {!hasSearched && (
                        <p className="friendpage-search-tagline">Find and connect with your friends by searching their usernames</p>
                    )}
                    {/* Show center magnifier when search is open but no query has been performed yet */}
                    {(!hasSearched || (hasSearched && searchResults.length === 0)) && (
                        <>
                            <img src={magGlass} alt="Search" className="friendpage-empty-glass" />
                            {hasSearched && searchResults.length === 0 && (
                                <p style={{ marginTop: 100, color: '#333', fontWeight: 600 }}>No users found. Try searching using a different search term!</p>
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
            )}

            {searchOpen && hasSearched && searchResults.length > 0 && (
                <div className="friendpage-results-container">
                    <div className="friendpage-results-gallery">
                        {searchResults.map((user) => {
                            const stored = user.bgColor ?? user.color ?? null;
                            const intVal = stored != null ? Number(stored) : null;
                            const cardBackground = intVal !== null && !Number.isNaN(intVal) && intVal !== 12901359 ? intToHex(intVal) : "rgba(255, 255, 255, 0.92)";
                            const cardTextColor = getContrastingTextColor(cardBackground);
                            const cardSubTextColor = cardTextColor === "#ffffff" ? "rgba(255,255,255,0.8)" : "rgba(26,26,26,0.75)";
                            const isActive = isUserActive(user);

                            return (
                                <div
                                    key={user.id}
                                    className={`friendpage-user-card ${newResultIds.has(user.id) ? 'new' : ''}`}
                                    style={{ backgroundColor: cardBackground }}
                                >
                                    <div className="friendpage-active-indicator-wrapper">
                                        <div className={isActive ? 'friendpage-active-indicator' : 'friendpage-inactive-indicator'}></div>
                                        <span className={isActive ? 'friendpage-active-tooltip' : 'friendpage-inactive-tooltip'}>{isActive ? 'Active' : 'Inactive'}</span>
                                    </div>
                                    <div className="friendpage-user-avatar">
                                        {user.profileImageUrl ? (
                                            <img src={user.profileImageUrl} alt={user.username} onError={(e) => { e.target.style.display = 'none'; }} />
                                        ) : (
                                            <div className="friendpage-avatar-placeholder">No Photo</div>
                                        )}
                                    </div>
                                    <div className="friendpage-user-info">
                                        <div
                                            className="friendpage-user-username"
                                            style={{ color: cardTextColor }}
                                        >
                                            {user.username}
                                        </div>
                                        {user.bio && (
                                            <div
                                                className="friendpage-user-bio"
                                                style={{ color: cardSubTextColor }}
                                            >
                                                {user.bio}
                                            </div>
                                        )}
                                        {(user.favoriteArtists || user.favoriteSongs) && (
                                            <div className="friendpage-user-favorites">
                                                {user.favoriteArtists && (
                                                    <div className="friendpage-user-favorite-row">
                                                        <span className="friendpage-user-favorite-label" style={{ color: cardTextColor }}>Favorite Artists: </span>
                                                        <span className="friendpage-user-favorite-value" style={{ color: cardTextColor }}>{user.favoriteArtists}</span>
                                                    </div>
                                                )}
                                                {user.favoriteSongs && (
                                                    <div className="friendpage-user-favorite-row">
                                                        <span className="friendpage-user-favorite-label" style={{ color: cardTextColor }}>Current Listen: </span>
                                                        <span className="friendpage-user-favorite-value" style={{ color: cardTextColor }}>{user.favoriteSongs}</span>
                                                    </div>
                                                )}
                                            </div>
                                        )}
                                        <button
                                            className="friendpage-add-friend-btn"
                                            onClick={() => handleAddFriend(user.id)}
                                            disabled={addingFriend === user.id || addedFriendIds.has(user.id)}
                                            style={{
                                                backgroundColor: addedFriendIds.has(user.id) ? '#90EE90' : 'var(--btn-bg1)',
                                                cursor: addedFriendIds.has(user.id) || addingFriend === user.id ? 'default' : 'pointer',
                                                opacity: addedFriendIds.has(user.id) ? 0.8 : 1,
                                            }}
                                        >
                                            {addingFriend === user.id ? '...' : addedFriendIds.has(user.id) ? '✓ Requested' : '+ Add Friend'}
                                        </button>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            )}
        </div>
    );
};

export default FriendPage;
