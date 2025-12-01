import React from 'react';
import { useNavigate } from 'react-router-dom';
import projectLogo from '../assets/projectLogo.png';
import './frontPage.css';

/**
 * Declares a constant variable named FrontPage
 * <p> 
 * This method sets the value of FrontPage to be constant. This function is used to navigate between pages in Javascript.
 */
const FrontPage = () => {
  const navigate = useNavigate();

/**
 * Displays the Front page
 * <p>
 * The styling of the page is displayed to the user. The functionality of the buttons is also declared here. Each button functions using React’s navigation tools. The class names being called are Javascript styling classes, used to maintain a consistent look across the program without having to declare each component's appearance individually. 
* @return The user interface and functionality of the Front page.  
 */
  return (
    <div className="frontpage-container">
      <nav className="frontpage-nav">
        <img src={projectLogo} alt="Moody logo" className="logo-btn" onClick={() => navigate('/')} />
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