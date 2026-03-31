import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./ReviewPage.css";

export default function ReviewPage() {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState("");
    const [searchResults, setSearchResults] = useState([]);
    const [selectedSong, setSelectedSong] = useState(null);
    const [rating, setRating] = useState(3);
    const [reviewText, setReviewText] = useState("");
    const [loading, setLoading] = useState(false);
    const [submitted, setSubmitted] = useState(false);
    const [error, setError] = useState("");
    const [bgColor, setBgColor] = useState("#c4dbef");
    const [currentUserId, setCurrentUserId] = useState(null);

    // Review search/browsing states
    const [reviewSearchQuery, setReviewSearchQuery] = useState("");
    const [reviewSearchResults, setReviewSearchResults] = useState([]);
    const [ratingFilter, setRatingFilter] = useState(0);
    const [showReviewSearch, setShowReviewSearch] = useState(false);
    const [reviewSearchLoading, setReviewSearchLoading] = useState(false);

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

    const handleSearch = async () => {
        if (!searchQuery.trim()) {
            setSearchResults([]);
            return;
        }

        setLoading(true);
        setError("");
        try {
            const token = localStorage.getItem("authToken");
            
            // Try Spotify API first via backend
            const response = await fetch(
                `http://127.0.0.1:8080/api/v1/spotify/search?q=${encodeURIComponent(searchQuery)}&type=track`,
                {
                    headers: {
                        Authorization: `Bearer ${token || ""}`,
                    },
                }
            );

            if (response.ok) {
                const data = await response.json();
                const tracks = data.tracks?.items?.map((track) => ({
                    id: track.id,
                    title: track.name,
                    artist: track.artists?.map((a) => a.name).join(", ") || "Unknown Artist",
                    album: track.album?.name || "",
                    image: track.album?.images?.[0]?.url || null,
                })) || [];
                setSearchResults(tracks);
            } else {
                // Fallback to simple mock
                setSearchResults([
                    { id: 1, title: searchQuery, artist: "Various Artists", album: "Search Results" }
                ]);
            }
        } catch (error) {
            console.error("Search error:", error);
            // Fallback mock data
            setSearchResults([
                { id: 1, title: searchQuery, artist: "Artist Name", album: "Album Name" }
            ]);
        } finally {
            setLoading(false);
        }
    };

    const handleReviewSearch = async () => {
        if (!reviewSearchQuery.trim()) {
            setReviewSearchResults([]);
            return;
        }

        setReviewSearchLoading(true);
        setError("");
        try {
            const token = localStorage.getItem("authToken");
            let url = `http://127.0.0.1:8080/api/v1/reviews/search?query=${encodeURIComponent(reviewSearchQuery)}`;
            if (ratingFilter > 0) {
                url += `&minRating=${ratingFilter}`;
            }

            const response = await fetch(url, {
                headers: {
                    Authorization: `Bearer ${token || ""}`,
                },
            });

            if (!response.ok) {
                // Fallback mock data
                setReviewSearchResults([
                    { id: 1, songTitle: reviewSearchQuery, rating: 4, comment: "Great song!", username: "User" }
                ]);
            } else {
                const results = await response.json();
                setReviewSearchResults(Array.isArray(results) ? results : []);
            }
        } catch (error) {
            console.error("Review search error:", error);
            // Fallback mock data
            setReviewSearchResults([
                { id: 1, songTitle: reviewSearchQuery, rating: 4, comment: "Amazing track", username: "Someone" }
            ]);
        } finally {
            setReviewSearchLoading(false);
        }
    };

    const handleSubmitReview = async (e) => {
        e.preventDefault();
        if (!selectedSong || !currentUserId) {
            setError("Please select a song and ensure you're logged in.");
            return;
        }

        setLoading(true);
        setError("");
        try {
            const token = localStorage.getItem("authToken");
            const response = await fetch("http://127.0.0.1:8080/api/v1/reviews", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token || ""}`,
                },
                body: JSON.stringify({
                    userID: currentUserId,
                    songID: selectedSong.id,
                    album: selectedSong.album || "",
                    rating: rating,
                    comment: reviewText,
                }),
            });

            if (!response.ok) {
                const msg = await response.text();
                throw new Error(msg || `Failed: ${response.status}`);
            }

            setSubmitted(true);
            setSelectedSong(null);
            setReviewText("");
            setRating(3);
            setTimeout(() => setSubmitted(false), 3000);
        } catch (error) {
            console.error("Submit error:", error);
            setError(`Error posting review: ${error.message}`);
        } finally {
            setLoading(false);
        }
    };

    const complementaryHex = (hex) => {
        try {
            const h = hex.replace("#", "");
            const num = parseInt(h, 16);
            let r = 255 - (num >> 16);
            let g = 255 - ((num >> 8) & 0x00ff);
            let b = 255 - (num & 0x0000ff);
            r = Math.min(255, Math.floor(r * 0.7 + 76));
            g = Math.min(255, Math.floor(g * 0.7 + 76));
            b = Math.min(255, Math.floor(b * 0.7 + 76));
            return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
        } catch (e) {
            return hex;
        }
    };

    const brightenHex = (hex) => {
        try {
            const h = hex.replace("#", "");
            const num = parseInt(h, 16);
            let r = (num >> 16);
            let g = ((num >> 8) & 0x00ff);
            let b = (num & 0x0000ff);
            r = Math.min(255, Math.floor(r * 1.4 + 60));
            g = Math.min(255, Math.floor(g * 1.4 + 60));
            b = Math.min(255, Math.floor(b * 1.4 + 60));
            return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
        } catch (e) {
            return hex;
        }
    };

    const primary = bgColor;
    const secondary = brightenHex(primary);
    const btnPrimary = complementaryHex(primary);
    const btnSecondary = complementaryHex(secondary);

    return (
        <div
            className="reviewpage-container"
            style={{
                "--home-bg1": primary,
                "--home-bg2": secondary,
                "--btn-bg1": btnPrimary,
                "--btn-bg2": btnSecondary,
            }}
        >
            <div className="reviewpage-back-btn">
                <button className="reviewpage-btn" onClick={() => navigate("/home")}>
                    ← BACK
                </button>
            </div>

            <div className="reviewpage-content">
                <div className="reviewpage-tabs">
                    <button 
                        className={`reviewpage-tab ${!showReviewSearch ? 'active' : ''}`}
                        onClick={() => setShowReviewSearch(false)}
                    >
                        Write Review
                    </button>
                    <button 
                        className={`reviewpage-tab ${showReviewSearch ? 'active' : ''}`}
                        onClick={() => setShowReviewSearch(true)}
                    >
                        Search Reviews
                    </button>
                </div>

                {!showReviewSearch ? (
                    // Write Review Section
                    <div className="reviewpage-card">
                        <h1>Write a Review</h1>
                        <p className="reviewpage-subtitle">Find a song, rate it, and share your thoughts!</p>

                        {submitted && (
                            <div className="reviewpage-success-banner">
                                ✓ Review posted successfully!
                            </div>
                        )}

                        {error && (
                            <div className="reviewpage-error-banner">
                                {error}
                            </div>
                        )}

                        <div className="reviewpage-section">
                            <h3>Search for a Song or Album</h3>
                            <div className="reviewpage-search-form">
                                <input
                                    type="text"
                                    placeholder="Enter song title, artist, or album name..."
                                    value={searchQuery}
                                    onChange={(e) => setSearchQuery(e.target.value)}
                                    onKeyPress={(e) => e.key === "Enter" && handleSearch()}
                                    className="reviewpage-search-input"
                                />
                                <button
                                    onClick={handleSearch}
                                    disabled={loading}
                                    className="reviewpage-search-btn"
                                >
                                    {loading ? "Searching..." : "Search"}
                                </button>
                            </div>

                            {searchResults.length > 0 && (
                                <div className="reviewpage-search-results">
                                    {searchResults.map((song) => (
                                        <div
                                            key={song.id}
                                            className={`reviewpage-song-item ${selectedSong?.id === song.id ? 'selected' : ''}`}
                                            onClick={() => setSelectedSong(song)}
                                        >
                                            <div className="reviewpage-song-info">
                                                <div className="reviewpage-song-title">{song.title}</div>
                                                <div className="reviewpage-song-artist">{song.artist || song.name}</div>
                                                {song.album && <div className="reviewpage-song-album">Album: {song.album}</div>}
                                            </div>
                                            <div className="reviewpage-song-check">
                                                {selectedSong?.id === song.id && "✓"}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>

                        {selectedSong && (
                            <form onSubmit={handleSubmitReview} className="reviewpage-form">
                                <div className="reviewpage-section">
                                    <h3>Selected Song</h3>
                                    <div className="reviewpage-selected-song">
                                        <div className="reviewpage-song-title">{selectedSong.title}</div>
                                        <div className="reviewpage-song-artist">{selectedSong.artist || "Unknown Artist"}</div>
                                        <button
                                            type="button"
                                            className="reviewpage-change-song-btn"
                                            onClick={() => {
                                                setSelectedSong(null);
                                                setSearchQuery("");
                                            }}
                                        >
                                            Change
                                        </button>
                                    </div>
                                </div>

                                <div className="reviewpage-section">
                                    <h3>Rating (0-5 stars)</h3>
                                    <div className="reviewpage-rating">
                                        {[0, 1, 2, 3, 4, 5].map((star) => (
                                            <button
                                                key={star}
                                                type="button"
                                                className={`reviewpage-star ${star <= rating ? 'filled' : ''}`}
                                                onClick={() => setRating(star)}
                                            >
                                                ★
                                            </button>
                                        ))}
                                        <span className="reviewpage-rating-text">{rating}/5</span>
                                    </div>
                                </div>

                                <div className="reviewpage-section">
                                    <h3>Your Review</h3>
                                    <textarea
                                        value={reviewText}
                                        onChange={(e) => setReviewText(e.target.value)}
                                        placeholder="Share your thoughts about this song or album (optional)..."
                                        className="reviewpage-textarea"
                                        maxLength={500}
                                    />
                                    <div className="reviewpage-char-count">
                                        {reviewText.length}/500
                                    </div>
                                </div>

                                <button
                                    type="submit"
                                    disabled={loading}
                                    className="reviewpage-submit-btn"
                                >
                                    {loading ? "Posting..." : "Post Review"}
                                </button>
                            </form>
                        )}
                    </div>
                ) : (
                    // Search Reviews Section
                    <div className="reviewpage-card">
                        <h1>Search Reviews</h1>
                        <p className="reviewpage-subtitle">Find reviews by song, artist, or album</p>

                        <div className="reviewpage-section">
                            <h3>Find Reviews</h3>
                            <div className="reviewpage-search-form">
                                <input
                                    type="text"
                                    placeholder="Search by song, artist, or album..."
                                    value={reviewSearchQuery}
                                    onChange={(e) => setReviewSearchQuery(e.target.value)}
                                    onKeyPress={(e) => e.key === "Enter" && handleReviewSearch()}
                                    className="reviewpage-search-input"
                                />
                                <button
                                    onClick={handleReviewSearch}
                                    disabled={reviewSearchLoading}
                                    className="reviewpage-search-btn"
                                >
                                    {reviewSearchLoading ? "Searching..." : "Search"}
                                </button>
                            </div>

                            <div style={{ marginTop: 16 }}>
                                <label style={{ marginRight: 12 }}>Filter by rating:</label>
                                <div className="reviewpage-rating-filter">
                                    {[0, 1, 2, 3, 4, 5].map((star) => (
                                        <button
                                            key={star}
                                            type="button"
                                            className={`reviewpage-filter-star ${star === ratingFilter ? 'selected' : ''}`}
                                            onClick={() => {
                                                setRatingFilter(star === ratingFilter ? 0 : star);
                                            }}
                                        >
                                            {star === 0 ? 'All' : '★'.repeat(star)}
                                        </button>
                                    ))}
                                </div>
                            </div>
                        </div>

                        <div className="reviewpage-section">
                            {reviewSearchResults.length > 0 ? (
                                <div className="reviewpage-reviews-list">
                                    {reviewSearchResults.map((review) => (
                                        <div key={review.id} className="reviewpage-review-card">
                                            <div className="review-header">
                                                <div className="review-song-info">
                                                    <div className="review-title">{review.songTitle || 'Song'}</div>
                                                    {review.username && <div className="review-author">by {review.username}</div>}
                                                </div>
                                                <div className="review-rating-display">
                                                    {'★'.repeat(review.rating)}{'☆'.repeat(5-review.rating)}
                                                </div>
                                            </div>
                                            {review.comment && (
                                                <div className="review-body">
                                                    {review.comment}
                                                </div>
                                            )}
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p style={{ textAlign: 'center', color: '#999' }}>
                                    {reviewSearchQuery ? 'No reviews found. Try a different search!' : 'Search for reviews above'}
                                </p>
                            )}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}