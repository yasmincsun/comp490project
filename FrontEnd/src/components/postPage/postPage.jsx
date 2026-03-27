//author: Miguel A.
//version: 1.01
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./postPage.css";

const PostPage = () => {

    const navigate = useNavigate();

    const [content, setContent] = useState("");
    const [image, setImage] = useState(null);
    const [errorMsg, setErrorMsg] = useState("");
    const [loading, setLoading] = useState(false);

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
        <div className="postpage-container">
            <h2>Create Post</h2>
            <p>Share your thoughts with others.</p>

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
    );
};

export default PostPage;