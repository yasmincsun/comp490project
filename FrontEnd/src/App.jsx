import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import ProfilePage from "./components/profilePage/profilePage";
import Login from "./components/loginPage/login";
import HomePage from "./components/homePage/homePage";
import FrontPage from "./components/frontPage/frontPage";
import ConnectSpotify from "./components/spotify/ConnectSpotify";
import FriendPage from "./components/friendPage/friendPage";

function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<FrontPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/connect-spotify" element={<ConnectSpotify />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/friends" element={<FriendPage />} />
        </Routes>
      </BrowserRouter>
  );
}

export default App;