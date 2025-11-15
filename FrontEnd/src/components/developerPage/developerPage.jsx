import React from 'react';
import { useNavigate } from 'react-router-dom';
import './developerPage.css';

/**
 * Declares a constant variable named DeveloperPage
 * <p>
 * This method sets the value of DeveloperPage to be constant. This function is used to navigate between pages in Javascript.
 */
const DeveloperPage = () => {
  const navigate = useNavigate();

/**
 * Displays the Developer page
 * <p>
 * The styling of the page is displayed to the user. The functionality of the buttons is also declared here. Each button functions using React’s navigation tools. The class names being called are Javascript styling classes, used to maintain a consistent look across the program without having to declare each component's appearance individually. 
 * @return The user interface and functionality of the Developer page.  
 */ 
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