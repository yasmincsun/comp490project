import React from 'react';
import { useNavigate } from 'react-router-dom';
import './aboutPage.css';

const AboutPage = () => {
  const navigate = useNavigate();

  return (
    <div className="aboutpage-container">
      <nav className="aboutpage-nav">
        <div className="nav-buttons">
          <button onClick={() => navigate('/')} className="nav-btn">Front Page</button>
          <button onClick={() => navigate('/about')} className="nav-btn active">About</button>
          <button onClick={() => navigate('/developers')} className="nav-btn">Developers</button>
        </div>
        <button onClick={() => navigate('/login')} className="login-btn">Login</button>
      </nav>
      <div className="aboutpage-content">
        {/* Content will be added later */}
      </div>
    </div>
  );
};

export default AboutPage;
