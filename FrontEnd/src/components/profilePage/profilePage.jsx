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
  "Ariana Grande",
  "Post Malone",
  "Travis Scott",
  "Playboi Carti",
  "Lil Uzi Vert",
  "The 1975",
  "Arctic Monkeys",
  "Kings of Leon",
  "The Strokes",
  "Franz Ferdinand",
  "Two Door Cinema Club",
  "Tame Impala",
  "MGMT",
  "Vampire Weekend",
  "Foster the People",
  "MGMT",
  "The Killers",
];

/**
 * Declares a constant variable named ProfilePage
 * <p>
 * This method sets the value of ProfilePage to be constant. This function is used to navigate between pages in Javascript, to display all of the components of the ProfilePage, and adds useStates for all profile variables.
 */
const ProfilePage = () => {
  const navigate = useNavigate();
  
  // ========== DATABASE VARIABLES: Save these to the database ==========
  const [nickname, setNickname] = useState("");             // User's nickname
  const [description, setDescription] = useState("");       // User's bio/description
  const [bgColor, setBgColor] = useState("#eaf6ff");     // User's background color preference
  const [favorites, setFavorites] = useState([]);           // User's list of favorite artists (max 3)
  // ====================================================================
  
  const [profilePic, setProfilePic] = useState(null);
  const [artistQuery, setArtistQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  
  // Editing states for account fields
  const [editingFirstName, setEditingFirstName] = useState(false);
  const [editingLastName, setEditingLastName] = useState(false);
  const [editingEmail, setEditingEmail] = useState(false);
  const [editingUsername, setEditingUsername] = useState(false);
  const [editingPassword, setEditingPassword] = useState(false);
  
  // Temp values for editing
  const [tempFirstName, setTempFirstName] = useState("");
  const [tempLastName, setTempLastName] = useState("");
  const [tempEmail, setTempEmail] = useState("");
  const [tempUsername, setTempUsername] = useState("");
  const [tempPass1, setTempPass1] = useState("");
  const [tempPass2, setTempPass2] = useState("");
  const [passError, setPassError] = useState("");
  
  // Track if any account fields have been changed
  const [hasChanges, setHasChanges] = useState(false);
  
  const fileInputRef = useRef(null);

  const [imgRetry, setImgRetry] = useState(0); // prevents infinite retry loop



  const fetchProfile = async () => {
  const token = localStorage.getItem("authToken");

  const res = await fetch("http://127.0.0.1:8080/api/v1/profile", {
    headers: {
      Authorization: `Bearer ${token || ""}`,
    },
  });

  if (!res.ok) {
    const msg = await res.text();
    throw new Error(msg || `Failed: ${res.status}`);
  }

  return res.json();
};


  // Load from localStorage once
  // Note: do not load profile data from localStorage anymore.
  // Profile is loaded from the backend in the next effect.

////////////////////////////////////////////////////////////////////////////////

  const hexToRgbInt = (hex) => parseInt(hex.replace("#", ""), 16);
  const rgbIntToHex = (n) => "#" + Number(n).toString(16).padStart(6, "0");

//Load from backend
  useEffect(() => {
    (async () => {
      try {
        const user = await fetchProfile();

        setNickname(user.username ?? "");
        setDescription(user.bio ?? "");
        setBgColor(user.color != null ? rgbIntToHex(user.color) : "#eaf6ff");

        // Presigned GET URL (bucket private)
        setProfilePic(user.profileImageUrl || null);

        // Fetch account information from user object
        setFirstName(user.name || "");
        setLastName(user.lastName || "");
        setEmail(user.email || "");
        setUsername(user.username || "");
        // Password is never returned from backend for security

        // allow retry again after a successful load
        setImgRetry(0);
      } catch (e) {
        console.error("Could not load profile from backend:", e);
      }
    })();
  }, []);
  // Do not persist profile to localStorage anymore — backend is the source of truth.

  // artist search
  useEffect(() => {
    if (!artistQuery) return setSearchResults([]);
    const q = artistQuery.toLowerCase();
    const results = MOCK_ARTISTS.filter((a) => a.toLowerCase().includes(q));
    setSearchResults(results);
  }, [artistQuery]);

  const [selectedProfileFile, setSelectedProfileFile] = useState(null);

  const onPickProfilePic = (e) => {
    const file = e.target.files && e.target.files[0];
    if (!file) return;

    // basic client-side validation
  const allowed = ["image/jpeg", "image/png", "image/webp"];
  if (!allowed.includes(file.type)) {
    alert("Please upload a JPG, PNG, or WEBP image.");
    return;
  }

  const maxBytes = 3 * 1024 * 1024; // 3MB (match backend)
  if (file.size > maxBytes) {
    alert("Image is too large (max 3MB).");
    return;
  }

  setSelectedProfileFile(file);

    //show preview
  const localPreviewUrl = URL.createObjectURL(file);
  setProfilePic(localPreviewUrl);
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
    setBgColor("#eaf6ff");
    setProfilePic(null);
    setFavorites([]);
    setFirstName("");
    setLastName("");
    setEmail("");
    setUsername("");
    setPassword("");
    localStorage.removeItem("profileData");
  };


  // This updates the bio
  const handleSaveBio = async () => {
    try{
      const token = localStorage.getItem("authToken");
      const response = await fetch(
        `http://127.0.0.1:8080/api/v1/profile/bio?bio=${encodeURIComponent(description)}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token || ""}`,
          },
        }
      );

      if(!response.ok){
        const msg = await response.text();
        throw new Error(msg || `Failed: ${response.status}`);
      }

      alert("Bio updated!");
    }
    catch(err){
      console.error(err);
      alert("Could not update bio.");
    }
  };

 const handleSaveProfile = async () => {
  try {
    const token = localStorage.getItem("authToken");
    const headers = { Authorization: `Bearer ${token || ""}` };

    const colorInt = hexToRgbInt(bgColor);

    const response1 = await fetch(
      `http://127.0.0.1:8080/api/v1/profile/userName?userName=${encodeURIComponent(nickname)}`,
      { method: "PUT", headers }
    );

    const response2 = await fetch(
      `http://127.0.0.1:8080/api/v1/profile/bio?bio=${encodeURIComponent(description)}`,
      { method: "PUT", headers }
    );

    const response3 = await fetch(
      `http://127.0.0.1:8080/api/v1/profile/color?color=${colorInt}`,
      { method: "PUT", headers }
    );

    for (const r of [response1, response2, response3]) {
      if (!r.ok) {
        const msg = await r.text();
        console.error("Update failed:", r.status, msg);
        throw new Error(msg || `Failed: ${r.status}`);
      }
    }

    alert("Profile updated!");
  } catch (e) {
    console.error(e);
    alert("Could not update profile.");
  }

};

  // call this when user clicks an "Upload" button
const handleUploadProfilePic = async () => {
  try {
    if (!selectedProfileFile) {
      alert("Pick an image first.");
      return;
    }

    const token = localStorage.getItem("authToken");

    // 1) ask backend for presigned upload URL
    const res = await fetch("http://127.0.0.1:8080/api/v1/profile/picture/upload-url", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token || ""}`,
      },
      body: JSON.stringify({
        contentType: selectedProfileFile.type,
        fileSize: selectedProfileFile.size,
      }),
    });

    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || `Failed to get upload URL: ${res.status}`);
    }

    const { uploadUrl, objectKey } = await res.json();

    // 2) upload directly to R2
    const putRes = await fetch(uploadUrl, {
      method: "PUT",
      headers: {
        "Content-Type": selectedProfileFile.type, // MUST match what backend signed
      },
      body: selectedProfileFile,
    });

    if (!putRes.ok) {
      const msg = await putRes.text();
      throw new Error(msg || `Upload failed: ${putRes.status}`);
    }

    // 3) tell backend to save objectKey to the user 
    const saveRes = await fetch("http://127.0.0.1:8080/api/v1/profile/picture", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token || ""}`,
      },
      body: JSON.stringify({ objectKey }),
    });

    if (!saveRes.ok) {
      const msg = await saveRes.text();
      throw new Error(msg || `Failed to save profile image: ${saveRes.status}`);
    }

    // If backend returns a publicUrl, it will use it. Otherwise it will build from base URL + objectKey.
    const saved = await saveRes.json().catch(() => ({}));

    if (saved.profileImageUrl) {
      setProfilePic(saved.profileImageUrl);
      setImgRetry(0);
    }

    alert("Profile picture uploaded!");
  } catch (err) {
    console.error(err);
    alert("Could not upload profile picture.");
  }
};

// Save account information to the database
const handleSaveAccountInfo = async () => {
  try {
    const token = localStorage.getItem("authToken");
    const headers = {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token || ""}`,
    };

    // Prepare the request body with the fields that have been changed
    const updates = {};
    if (tempFirstName !== firstName) updates.firstName = tempFirstName;
    if (tempLastName !== lastName) updates.lastName = tempLastName;
    if (tempPass1) {
      if (tempPass1 !== tempPass2) {
        setPassError("Passwords do not match");
        return;
      }
      updates.password = tempPass1;
    }

    const response = await fetch("http://127.0.0.1:8080/api/v1/profile/account", {
      method: "PUT",
      headers,
      body: JSON.stringify(updates),
    });

    if (!response.ok) {
      const msg = await response.text();
      throw new Error(msg || `Failed: ${response.status}`);
    }

    // Update the state with the new values
    setFirstName(tempFirstName);
    setLastName(tempLastName);
    if (tempPass1) setPassword(tempPass1);
    setHasChanges(false);
    setEditingFirstName(false);
    setEditingLastName(false);
    setEditingPassword(false);
    setTempPass1("");
    setTempPass2("");
    setPassError("");
    
    alert("Account information updated!");
  } catch (e) {
    console.error(e);
    alert("Could not update account information.");
  }
};





/**
 * Displays the profile information to the user
 * <p>
 * This statement displays the profile information to the user, including all of the necessary styling and API information. 
* @return ProfilePage interface to the Webpage
 */

  // Helper function to convert hex to rgba with 50% opacity
  const hexToRgba50 = (hex) => {
    const h = hex.replace("#", "");
    const r = parseInt(h.substr(0, 2), 16);
    const g = parseInt(h.substr(2, 2), 16);
    const b = parseInt(h.substr(4, 2), 16);
    return `rgba(${r}, ${g}, ${b}, 0.5)`;
  };

  return (
    <div className="profilepage-container" style={{ background: bgColor, "--profile-preview-bg": hexToRgba50(bgColor) }}>
      <div className="profilepage-login-btn-topright">
        <button className="profilepage-back-btn" onClick={() => navigate("/home")}>Home</button>
      </div>

      <div className="profilepage-card">
        <h1>Your Profile</h1>
        
        <div className="profile-content">
          <div className="profile-row">
            <div className="profile-avatar-column">
              <div className="profile-avatar">
                {profilePic ? (
                  <img src={profilePic} alt="profile" 
                      onError={async () => {
                        // retry only once to avoid infinite loops
                        if (imgRetry >= 1) return;
                        setImgRetry(1);

                        try {
                          const user = await fetchProfile();
                          setProfilePic(user.profileImageUrl || null);
                        } catch (e) {
                          console.error("Could not refresh profile image URL:", e);
                          setProfilePic(null);
                        }
                      }} 
                    />
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
                {/* Image Upload Buttons */}
                <button onClick={() => fileInputRef.current && fileInputRef.current.click()}className="profile-btn"> 
                  Choose Image
                  </button>
                <button onClick={handleUploadProfilePic} className="profile-btn" disabled={!selectedProfileFile}>
                  Upload Photo
                  </button>
                <button onClick={removeProfilePic} className="profile-btn gray">
                  Remove
                  </button>
              </div>
            </div>

            <div className="profile-details-column">
              <label className="label">Username</label>
              <input className="input nickname-input" value={nickname} onChange={(e) => setNickname(e.target.value)} placeholder="Add a nickname" />

              <label className="label">Description</label>
              <textarea className="input description-input" value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Write something about yourself..." />

              <label className="label">Background Color</label>
              <div className="bgcolor-row">
                <input type="color" value={bgColor} onChange={(e) => setBgColor(e.target.value)} className="color-input" />
              </div>
            </div>
          </div>

          {/* Profile Preview Section */}
          <div className="profile-preview">
            <h3>Profile Preview</h3>
            <div className="preview-card">
              <div className="preview-avatar">
                {profilePic ? <img src={profilePic} alt="preview" /> : <div className="profile-avatar-placeholder">No Photo</div>}
              </div>
              <div className="preview-info">
                <div className="preview-nickname">{nickname || "Your nickname"}</div>
                <div className="preview-desc">{description || "Your description will appear here."}</div>
              </div>
            </div>
          </div>

          {/* Your Account Section */}
          <div className="account-section">
            <h3>Your Account</h3>
            
            <div className="account-field-row">
              <div className="account-label">First Name</div>
              {!editingFirstName ? (
                <div className="account-value">
                  <span className="account-text">{firstName || "(not set)"}</span>
                  <button className="pencil-btn" title="Edit first name" onClick={() => { setTempFirstName(firstName); setEditingFirstName(true); }}>✏️</button>
                </div>
              ) : (
                <div className="account-edit">
                  <input className="input" value={tempFirstName} onChange={(e) => { setTempFirstName(e.target.value); setHasChanges(true); }} />
                  <div style={{display:'flex',gap:8}}>
                    <button className="profile-save-btn" onClick={() => setEditingFirstName(false)}>Done</button>
                    <button className="profile-clear-btn gray" onClick={() => { setTempFirstName(firstName); setEditingFirstName(false); }}>Cancel</button>
                  </div>
                </div>
              )}
            </div>

            <div className="account-field-row">
              <div className="account-label">Last Name</div>
              {!editingLastName ? (
                <div className="account-value">
                  <span className="account-text">{lastName || "(not set)"}</span>
                  <button className="pencil-btn" title="Edit last name" onClick={() => { setTempLastName(lastName); setEditingLastName(true); }}>✏️</button>
                </div>
              ) : (
                <div className="account-edit">
                  <input className="input" value={tempLastName} onChange={(e) => { setTempLastName(e.target.value); setHasChanges(true); }} />
                  <div style={{display:'flex',gap:8}}>
                    <button className="profile-save-btn" onClick={() => setEditingLastName(false)}>Done</button>
                    <button className="profile-clear-btn gray" onClick={() => { setTempLastName(lastName); setEditingLastName(false); }}>Cancel</button>
                  </div>
                </div>
              )}
            </div>

            <div className="account-field-row">
              <div className="account-label">Email</div>
              <div className="account-value">
                <span className="account-text">{email || "(not set)"}</span>
              </div>
            </div>

            <div className="account-field-row">
              <div className="account-label">Username</div>
              {!editingUsername ? (
                <div className="account-value">
                  <span className="account-text">{username || "(not set)"}</span>
                  <button className="pencil-btn" title="Edit username" onClick={() => { setTempUsername(username); setEditingUsername(true); }}>✏️</button>
                </div>
              ) : (
                <div className="account-edit">
                  <input className="input" value={tempUsername} onChange={(e) => { setTempUsername(e.target.value); setHasChanges(true); }} />
                  <div style={{display:'flex',gap:8}}>
                    <button className="profile-save-btn" onClick={() => setEditingUsername(false)}>Done</button>
                    <button className="profile-clear-btn gray" onClick={() => { setTempUsername(username); setEditingUsername(false); }}>Cancel</button>
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
                  <input className="input" type="password" placeholder="New password" value={tempPass1} onChange={(e) => { setTempPass1(e.target.value); setHasChanges(true); }} />
                  <input className="input" type="password" placeholder="Retype new password" value={tempPass2} onChange={(e) => setTempPass2(e.target.value)} />
                  {passError && <div className="error" style={{marginTop:6}}>{passError}</div>}
                  <div style={{marginTop:8, display:'flex', gap:8}}>
                    <button className="profile-save-btn" onClick={() => setEditingPassword(false)}>Done</button>
                    <button className="profile-clear-btn gray" onClick={() => { setEditingPassword(false); setPassError(""); }}>Cancel</button>
                  </div>
                </div>
              )}
            </div>

            {/* Account Save Button */}
            <div className="account-save-row">
              <button 
                className={`account-save-btn ${hasChanges ? 'active' : 'inactive'}`}
                disabled={!hasChanges}
                onClick={handleSaveAccountInfo}
              >
                Save Changes
              </button>
            </div>
          </div>

          <div className="profile-actions-row">
            <button className="profile-save-btn" onClick={handleSaveProfile}>Save Profile</button>
            <button className="profile-clear-btn gray" onClick={clearProfile}>Clear</button>
          </div>
        </div>
      </div>

    </div>
  );
};

export default ProfilePage;
