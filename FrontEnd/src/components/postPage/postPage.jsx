//author: Miguel A.
//version: 1.01
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./postPage.css";

const PostPage = () => {

    const navigate = useNavigate();

    const [content, setContent] = useState("");
    const [image, setImage] = useState(null);
    const [errorMsg, setErrorMsg] = useState("");
    const [loading, setLoading] = useState(false);
    const [userColor, setUserColor] = useState(null);

    useEffect(() => {
        const fetchColor = async () => {
            try {
                const token = localStorage.getItem("authToken");
                if (!token) return;

                const response = await fetch("http://127.0.0.1:8080/api/v1/profile", {
                    headers: { Authorization: `Bearer ${token}` },
                });

                if (!response.ok) return;
                const profile = await response.json();
                if (profile.color != null) {
                    setUserColor(profile.color);
                }
            } catch (error) {
                console.error("Could not load post page profile color:", error);
            }
        };
        fetchColor();
    }, []);

    const toHex = (colorInt) => {
        if (colorInt == null) return "#c4dbef";
        return "#" + Number(colorInt).toString(16).padStart(6, "0");
    };

    const brightenHex = (hex, amount = 40) => {
        try {
            const h = hex.replace("#", "");
            const num = parseInt(h, 16);
            let r = (num >> 16) + amount;
            let g = ((num >> 8) & 0x00ff) + amount;
            let b = (num & 0x0000ff) + amount;
            r = Math.max(Math.min(255, r), 0);
            g = Math.max(Math.min(255, g), 0);
            b = Math.max(Math.min(255, b), 0);
            return "#" + ((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1);
        } catch {
            return hex;
        }
    };

    const primary = userColor != null ? toHex(userColor) : "#c4dbef";
    const secondary = brightenHex(primary, 40);

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validation: content is required
        if (!content) {
            setErrorMsg("Content is required.");
            return;
        }

        // Clear previous errors and start loading
        setErrorMsg("");
        setLoading(true);

        try {
            const formData = new FormData();
            formData.append("content", content);

            if (image) {
                formData.append("picture", image);
            }

            const token = localStorage.getItem("authToken");
            if (!token) {
                setErrorMsg("You must be logged in.");
                setLoading(false);
                return;
            }

            const response = await fetch("http://127.0.0.1:8080/api/posts", {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`
                    // Broswer will automatically set the correct Content-Type for FormData
                },
                body: formData
            });

            if (!response.ok) {
                const data = await response.json().catch(() => ({}));
                throw new Error(data.message || "Failed to create post");
            }

            alert("Post created successfully!");
            navigate("/home");

        } catch (error) {
            console.error(error);
            setErrorMsg(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div
            className="postpage-container"
            style={{
                "--home-bg1": primary,
                "--home-bg2": secondary,
            }}
        >
            <div className="postpage-card">
                <button type="button" className="postpage-back-btn" onClick={() => navigate("/home")}>Back</button>
                <div className="postpage-header">
                    <h2>Create Post</h2>
                    <p>Share your thoughts with others.</p>
                </div>

                <form className="postpage-form" onSubmit={handleSubmit}>

                <div className="create-post-row">
                    <label className="create-post-label">Content</label>
                    <textarea
                        className="create-post-input create-post-textarea"
                        placeholder="Write your caption here..."
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                    />
                </div>

                <div className="create-post-row">
                    <label className="create-post-label">Image (optional)</label>
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setImage(e.target.files[0])}
                    />
                </div>

                {errorMsg && (
                    <div className="postpage-error">
                        {errorMsg}
                    </div>
                )}

                <div className="create-post-submit-wrapper">
                    <button type="submit" disabled={loading} className="create-post-submit">
                        {loading ? "Creating post..." : "Create Post"}
                    </button>
                </div>

                </form>
            </div>
        </div>
    );
};

export default PostPage;