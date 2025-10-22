import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { QuizProvider } from "./context/quiz-context";
import Quiz from "./components/quiz";
import ProfilePage from "./components/profilePage/profilePage";
import Login from "./components/loginPage/login";
import HomePage from "./components/homePage/homePage";

function App() {
  return (
    <QuizProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/login" element={<Login />} />
          <Route path="/quiz" element={<Quiz />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Routes>
      </BrowserRouter>
    </QuizProvider>
  );
}

export default App;