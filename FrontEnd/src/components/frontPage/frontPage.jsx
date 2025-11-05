import React from 'react';
import { useNavigate } from 'react-router-dom';
import './frontPage.css';

const FrontPage = () => {
  const navigate = useNavigate();

  return (
    <div className="frontpage-container">
      <nav className="frontpage-nav">
        <div className="nav-buttons">
          <button onClick={() => navigate('/about')} className="nav-btn">ABOUT</button>
          <button onClick={() => navigate('/developers')} className="nav-btn">DEVELOPERS</button>
        </div>
        <button onClick={() => navigate('/login')} className="login-btn">Log In</button>
      </nav>
      <div className="frontpage-content">
        <h1>Welcome to MoodMusic</h1>
        <p>Get personalized music recommendations based on your mood</p>
        <button onClick={() => navigate('/home')} className="start-btn">Get Started</button>
      </div>
    </div>
  );
};

export default FrontPage;
