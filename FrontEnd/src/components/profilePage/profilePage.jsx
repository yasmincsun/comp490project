import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import "./profilePage.css";

/**
 * Declares a constant variable named MOCK_ARTISTS
 * <p> 
 * This method sets the value of MOCK_ARTISTS to be a constant array. This list provides the user with a list of options to include as their favorite on their profile page.
 */
const MOCK_ARTISTS = [
  "Taylor Swift",
  "Drake",
  "Beyonce",
  "The Weeknd",
  "Adele",
  "Kendrick Lamar",
  "Billie Eilish",
  "Coldplay",
  "Radiohead",
  "Rihanna",
  "Ed Sheeran",
  "Bruno Mars",
  "Dua Lipa",
];

/**
 * Declares a constant variable named ProfilePage
 * <p>
 * This method sets the value of ProfilePage to be constant. This function is used to navigate between pages in Javascript, to display all of the components of the ProfilePage, and adds useStates for all profile variables.
 */
const ProfilePage = () => {
  const navigate = useNavigate();
  const [view, setView] = useState("profile"); // 'profile' or 'account'
  const [nickname, setNickname] = useState("");
  const [description, setDescription] = useState("");
  const [bgColor, setBgColor] = useState("#dde2ef");
  const [profilePic, setProfilePic] = useState(null);
  const [artistQuery, setArtistQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [favorites, setFavorites] = useState([]);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const fileInputRef = useRef(null);
  // editing states for account panel
  const [editingName, setEditingName] = useState(false);
  const [editingEmail, setEditingEmail] = useState(false);
  const [editingPassword, setEditingPassword] = useState(false);
  const [tempName, setTempName] = useState("");
  const [tempEmail, setTempEmail] = useState("");
  const [tempPass1, setTempPass1] = useState("");
  const [tempPass2, setTempPass2] = useState("");
  const [passError, setPassError] = useState("");

  // Load from localStorage once
  useEffect(() => {
    try {
      const data = JSON.parse(localStorage.getItem("profileData") || "null");
      if (data) {
        setNickname(data.nickname || "");
        setDescription(data.description || "");
        setBgColor(data.bgColor || "#dde2ef");
        setProfilePic(data.profilePic || null);
        setFavorites(data.favorites || []);
        setName(data.name || "");
        setEmail(data.email || "");
        setPassword(data.password || "");
      }
    } catch (e) {
      // ignore
    }
  }, []);

  // Persist to localStorage whenever anything important changes
  useEffect(() => {
    const data = { nickname, description, bgColor, profilePic, favorites, name, email, password };
    localStorage.setItem("profileData", JSON.stringify(data));
  }, [nickname, description, bgColor, profilePic, favorites, name, email, password]);

  // artist search
  useEffect(() => {
    if (!artistQuery) return setSearchResults([]);
    const q = artistQuery.toLowerCase();
    const results = MOCK_ARTISTS.filter((a) => a.toLowerCase().includes(q));
    setSearchResults(results);
  }, [artistQuery]);

  const onPickProfilePic = (e) => {
    const file = e.target.files && e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => setProfilePic(reader.result);
    reader.readAsDataURL(file);
  };

  const removeProfilePic = () => setProfilePic(null);

  const toggleFavorite = (artist) => {
    if (favorites.includes(artist)) {
      setFavorites(favorites.filter((a) => a !== artist));
    } else {
      if (favorites.length >= 3) return; // limit 3
      setFavorites([...favorites, artist]);
    }
  };

  const clearProfile = () => {
    setNickname("");
    setDescription("");
    setBgColor("#dde2ef");
    setProfilePic(null);
    setFavorites([]);
    setName("");
    setEmail("");
    setPassword("");
    localStorage.removeItem("profileData");
  };

/**
 * Displays the profile information to the user
 * <p>
 * This statement displays the profile information to the user, including all of the necessary styling and API information. 
* @return ProfilePage interface to the Webpage
 */
  return (
    <div className="profilepage-container" style={{ background: bgColor }}>
      <div className="profilepage-login-btn-topright">
        <button className="profilepage-back-btn" onClick={() => navigate("/")}>Home</button>
      </div>

      <div className="left-panel">
        <button className={`left-panel-btn ${view === 'account' ? 'active' : ''}`} onClick={() => setView('account')}>ACCOUNT</button>
        <button className={`left-panel-btn ${view === 'profile' ? 'active' : ''}`} onClick={() => setView('profile')}>PROFILE</button>
      </div>

      <div className="profilepage-card">
        <h1>{view === 'profile' ? 'Your Profile' : 'Account'}</h1>
        
        {view === 'profile' ? (
          <div className="profile-content">
            <div className="profile-row">
              <div className="profile-avatar-column">
                <div className="profile-avatar">
                  {profilePic ? (
                    <img src={profilePic} alt="profile" />
                  ) : (
                    <div className="profile-avatar-placeholder">No Photo</div>
                  )}
                </div>
                <div className="profile-avatar-actions">
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/*"
                    id="profile-pic-input"
                    onChange={onPickProfilePic}
                    style={{ display: "none" }}
                  />
                  <button onClick={() => fileInputRef.current && fileInputRef.current.click()} className="profile-btn">Upload</button>
                  <button onClick={removeProfilePic} className="profile-btn gray">Remove</button>
                </div>
              </div>

              <div className="profile-details-column">
                <label className="label">Nickname</label>
                <input className="input nickname-input" value={nickname} onChange={(e) => setNickname(e.target.value)} placeholder="Add a nickname" />

                <label className="label">Description</label>
                <textarea className="input description-input" value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Write something about yourself..." />

                <label className="label">Background Color</label>
                <div className="bgcolor-row">
                  <input type="color" value={bgColor} onChange={(e) => setBgColor(e.target.value)} className="color-input" />
                </div>
              </div>
            </div>

            <div className="favorites-section">
              <h3>Favorite Artists (up to 3)</h3>
              <div className="artist-search">
                <input className="input artist-search-input" placeholder="Search artists..." value={artistQuery} onChange={(e) => setArtistQuery(e.target.value)} />
                <div className="search-results">
                  {searchResults.map((a) => (
                    <div key={a} className="search-item" onClick={() => toggleFavorite(a)}>
                      <span>{a}</span>
                      <button className={`small-btn ${favorites.includes(a) ? "selected" : ""}`}>{favorites.includes(a) ? "Selected" : "Add"}</button>
                    </div>
                  ))}
                </div>
              </div>

              <div className="favorites-list">
                {favorites.length === 0 ? (
                  <div className="no-fav">No favorites selected</div>
                ) : (
                  favorites.map((f) => (
                    <div key={f} className="fav-item">
                      {f}
                      <button className="remove-small" onClick={() => toggleFavorite(f)}>Remove</button>
                    </div>
                  ))
                )}
              </div>
            </div>

            <div className="profile-actions-row">
              <button className="profile-save-btn" onClick={() => alert("Profile saved locally")}>Save</button>
              <button className="profile-clear-btn gray" onClick={clearProfile}>Clear</button>
            </div>

            <div className="profile-preview">
              <h3>Preview</h3>
              <div className="preview-card">
                <div className="preview-avatar">
                  {profilePic ? <img src={profilePic} alt="preview" /> : <div className="profile-avatar-placeholder">No Photo</div>}
                </div>
                <div className="preview-info">
                  <div className="preview-nickname">{nickname || "Your nickname"}</div>
                  <div className="preview-desc">{description || "Your description will appear here."}</div>
                  <div className="preview-favs">Favorites: {favorites.length ? favorites.join(", ") : "None"}</div>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="account-content">
            <h2>Account Information</h2>
            <div className="account-field-row">
              <div className="account-label">Name</div>
              {!editingName ? (
                <div className="account-value">
                  <span className="account-text">{name || "(no name)"}</span>
                  <button className="pencil-btn" title="Edit name" onClick={() => { setTempName(name); setEditingName(true); }}>✏️</button>
                </div>
              ) : (
                <div className="account-edit">
                  <input className="input" value={tempName} onChange={(e) => setTempName(e.target.value)} />
                  <div style={{display:'flex',gap:8}}>
                    <button className="profile-save-btn" onClick={() => { setName(tempName); setEditingName(false); }}>Save</button>
                    <button className="profile-clear-btn gray" onClick={() => setEditingName(false)}>Cancel</button>
                  </div>
                </div>
              )}
            </div>

            <div className="account-field-row">
              <div className="account-label">Email</div>
              {!editingEmail ? (
                <div className="account-value">
                  <span className="account-text">{email || "(no email)"}</span>
                  <button className="pencil-btn" title="Edit email" onClick={() => { setTempEmail(email); setEditingEmail(true); }}>✏️</button>
                </div>
              ) : (
                <div className="account-edit">
                  <input className="input" value={tempEmail} onChange={(e) => setTempEmail(e.target.value)} />
                  <div style={{display:'flex',gap:8}}>
                    <button className="profile-save-btn" onClick={() => { setEmail(tempEmail); setEditingEmail(false); }}>Save</button>
                    <button className="profile-clear-btn gray" onClick={() => setEditingEmail(false)}>Cancel</button>
                  </div>
                </div>
              )}
            </div>

            <div className="account-field-row">
              <div className="account-label">Password</div>
              {!editingPassword ? (
                <div className="account-value">
                  <span className="account-text">••••••••</span>
                  <button className="pencil-btn" title="Change password" onClick={() => { setTempPass1(""); setTempPass2(""); setPassError(""); setEditingPassword(true); }}>✏️</button>
                </div>
              ) : (
                <div className="account-edit password-edit">
                  <input className="input" type="password" placeholder="New password" value={tempPass1} onChange={(e) => setTempPass1(e.target.value)} />
                  <input className="input" type="password" placeholder="Retype new password" value={tempPass2} onChange={(e) => setTempPass2(e.target.value)} />
                  {passError && <div className="error" style={{marginTop:6}}>{passError}</div>}
                  <div style={{marginTop:8, display:'flex', gap:8}}>
                    <button className="profile-save-btn" onClick={() => {
                      if (!tempPass1 || tempPass1 !== tempPass2) { setPassError("Passwords must match"); return; }
                      setPassword(tempPass1); setEditingPassword(false); setPassError("");
                    }} disabled={!tempPass1 || tempPass1 !== tempPass2}>Save</button>
                    <button className="profile-clear-btn gray" onClick={() => setEditingPassword(false)}>Cancel</button>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </div>

    </div>
  );
};

export default ProfilePage;
