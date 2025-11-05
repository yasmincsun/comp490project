import React from 'react';
import { useNavigate } from 'react-router-dom';
import './aboutPage.css';

const AboutPage = () => {
  const navigate = useNavigate();

  return (
    <div className="aboutpage-container">
      <nav className="aboutpage-nav">
        <div className="nav-buttons">
          <button onClick={() => navigate('/')} className="nav-btn">HOME</button>
          <button onClick={() => navigate('/about')} className="nav-btn active">ABOUT</button>
          <button onClick={() => navigate('/developers')} className="nav-btn">DEVELOPERS</button>
        </div>
        <button onClick={() => navigate('/login')} className="login-btn">Log In</button>
      </nav>
      <div className="aboutpage-content">
        {/* Content will be added later */}
      </div>
    </div>
  );
};

export default AboutPage;
