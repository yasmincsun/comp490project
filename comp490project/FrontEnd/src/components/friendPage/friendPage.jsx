import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./friendPage.css";

/**
 * FriendPage Component
 * <p>
 * This page displays a two-column layout with posts on the left side
 * and a blank right side for future content. The page features a moving
 * gradient background similar to the home page.
 */
const FriendPage = () => {
    const navigate = useNavigate();
    const [bgColor, setBgColor] = useState("");
    const [isLoggedIn, setIsLoggedIn] = useState(false);

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

            {/* Two-column layout */}
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
