import React from 'react';
import { useNavigate } from 'react-router-dom';
import projectLogo from '../assets/projectLogo.png';
import './aboutPage.css';

/**
 * Declares a constant variable named AboutPage
 * <p> 
 * This method sets the value of AboutPage to be constant. This function is used to navigate between pages in Javascript.
 *
 */
const AboutPage = () => {
  const navigate = useNavigate();

/**
 * Displays the AboutUs page
 * <p>
 * The styling of the page is displayed to the user. The functionality of the buttons is also declared here. Each button functions using React’s navigation tools. The class names being called are Javascript styling classes, used to maintain a consistent look across the program without having to declare each component's appearance individually. 
 * @return The user interface and functionality of the About page.  
 */

  return (
    <div className="aboutpage-container">
      <nav className="aboutpage-nav">
        <img src={projectLogo} alt="Moody logo" className="logo-btn" onClick={() => navigate('/')} />
        <div className="nav-buttons">
          {/* <button onClick={() => navigate('/')} className="nav-btn">Front Page</button> */}
          <button onClick={() => navigate('/about')} className="nav-btn active">About</button>
          <button onClick={() => navigate('/developers')} className="nav-btn">Developers</button>
        </div>
        <button onClick={() => navigate('/login')} className="login-btn">Login</button>
      </nav>
      <div className="aboutpage-content">
        <div className="about-animated-center">will fill in this info later</div>
      </div>
    </div>
  );
};

export default AboutPage;