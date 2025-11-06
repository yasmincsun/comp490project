import React from 'react';
import { useNavigate } from 'react-router-dom';
import './developerPage.css';

const DeveloperPage = () => {
  const navigate = useNavigate();

  return (
    <div className="developerpage-container">
      <nav className="developerpage-nav">
        <div className="nav-buttons">
          <button onClick={() => navigate('/')} className="nav-btn">Front Page</button>
          <button onClick={() => navigate('/about')} className="nav-btn">About</button>
          <button onClick={() => navigate('/developers')} className="nav-btn active">Developers</button>
        </div>
        <button onClick={() => navigate('/login')} className="login-btn">Log In</button>
      </nav>
      <div className="developerpage-content">
        {/* Content will be added later */}
      </div>
    </div>
  );
};

export default DeveloperPage;
