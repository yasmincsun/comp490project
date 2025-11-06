import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import ProfilePage from "./components/profilePage/profilePage";
import Login from "./components/loginPage/login";
import HomePage from "./components/homePage/homePage";
import FrontPage from "./components/frontPage/frontPage";
import AboutPage from "./components/aboutPage/aboutPage";
import DeveloperPage from "./components/developerPage/developerPage";

function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<FrontPage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route path="/developers" element={<DeveloperPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Routes>
      </BrowserRouter>
  );
}

export default App;