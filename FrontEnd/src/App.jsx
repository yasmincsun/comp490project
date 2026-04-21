import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import ProfilePage from "./components/profilePage/profilePage";
import Login from "./components/loginPage/login";
import HomePage from "./components/homePage/homePage";
import FrontPage from "./components/frontPage/frontPage";
import ConnectSpotify from "./components/spotify/ConnectSpotify";
import FriendPage from "./components/friendPage/friendPage";
import PostPage from "./components/postPage/postPage";
import ReviewPage from "./components/reviewPage/reviewPage";
import MapPage from "./components/mapPage/mapPage";
import FeedPage from "./components/feedPage/feedPage";

/**
 * Main application component.
 * Defines client-side routes for the Moody app using React Router.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
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
          <Route path="/reviews" element={<ReviewPage />} />
          <Route path="/post" element={<PostPage />} />
          <Route path="/feed" element={<FeedPage />} />
          {/* <Route path="/map" element={<MapPage />} /> */}
          
        </Routes>
      </BrowserRouter>
  );
}

export default App;