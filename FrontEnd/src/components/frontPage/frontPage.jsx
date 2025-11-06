import React from 'react';
import { useNavigate } from 'react-router-dom';
import './frontPage.css';

const FrontPage = () => {
  const navigate = useNavigate();

  return (
    <div className="frontpage-container">
      <nav className="frontpage-nav">
        <div className="nav-buttons">
          <button onClick={() => navigate('/about')} className="nav-btn">About</button>
          <button onClick={() => navigate('/developers')} className="nav-btn">Developers</button>
        </div>
        <button onClick={() => navigate('/login')} className="login-btn">Login</button>
      </nav>
      <div className="frontpage-content">
        <h1>Welcome to Moody</h1>
        <p>STUFF WILL BE ADDED HERE WHEN WE HAVE THINGS I CAN ATTACH</p>
      </div>
    </div>
  );
};

export default FrontPage;
