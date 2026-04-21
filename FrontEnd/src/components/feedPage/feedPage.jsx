//author: Miguel A.
//version: 2.0 (clean rewrite)

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./feedPage.css";

const FeedPage = () => {
    const navigate = useNavigate();

    const [posts, setPosts] = useState([]);
    const [userColor, setUserColor] = useState("#ffffff");
    const [bgColor, setBgColor] = useState("#f5f5f5");
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState("");

    // Fetch profile
    const fetchProfile = async (token) => {
        try {
            const res = await fetch("http://127.0.0.1:8080/api/v1/profile", {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!res.ok) return null;

            return await res.json();
        } catch (err) {
            console.error("Profile fetch error:", err);
            return null;
        }
    };

    // Fetch posts
    const fetchPosts = async (token) => {
        try {
            const res = await fetch("http://127.0.0.1:8080/api/posts", {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!res.ok) {
                console.error("Failed to fetch posts:", res.statusText);
                return [];
            }

            return await res.json();
        } catch (err) {
            console.error("Posts fetch error:", err);
            return [];
        }
    };

    // Load everything on mount
    useEffect(() => {
        const loadData = async () => {
            const token = localStorage.getItem("authToken");

            if (!token) {
                setErrorMsg("You must be logged in.");
                setLoading(false);
                return;
            }

            const profile = await fetchProfile(token);
            if (profile) {
                setUserColor(profile.color || "#ffffff");
                setBgColor(profile.bgColor || "#f5f5f5");
            }

            const postsData = await fetchPosts(token);
            setPosts(postsData);

            setLoading(false);
        };

        loadData();
    }, []);

    return (
        <div
            className="feedpage-container"
            style={{
                "--home-bg-1": userColor,
                "--home-bg-2": bgColor,
            }}
        >
            <div className="feedpage-content">
                <button
                    className="feedPage-back-btn"
                    onClick={() => navigate("/home")}
                >
                    ← BACK
                </button>

                <h1>Feed Page</h1>

                <button
                    className="feedPage-createPost-btn"
                    onClick={() => navigate("/post")}
                >
                    Create Post
                </button>

                {/* STATES */}
                {loading && <p>Loading posts...</p>}
                {errorMsg && <p className="error">{errorMsg}</p>}

                {/* POSTS */}
                {!loading && posts.length === 0 && (
                    <p>No posts yet.</p>
                )}

                <div className="feedpage-posts">
                    {posts.map((post) => (
                        <div key={post.id} className="post">
                            <p className="post-content">{post.content}</p>

                            {post.image && (
                                <img
                                    src={post.image}
                                    alt="post"
                                    className="post-image"
                                />
                            )}

                            <div className="post-meta">
                                <span>{post.author || "Unknown user"}</span>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default FeedPage;