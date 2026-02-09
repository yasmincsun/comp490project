import React from 'react';
import { useNavigate } from 'react-router-dom';
import projectLogo from '../assets/projectLogo.png';
import teamPic from '../assets/teamMembers.png';
import './frontPage.css';

/**
 * Team member data for the developer section
 */
const TEAM_MEMBERS = [
  {
    name: 'MIGUEL',
    role: 'Database Design',
    description: 'Created a MySQL Database that stores User, API, and other program information',
  },
  {
    name: 'JOSE',
    role: 'Backend Development',
    description: 'Created necessary backend components and connected the UI to the backend of the program',
  },
  {
    name: 'ALLEN',
    role: 'API',
    description: 'Established a connection with Spotify\'s API that allowed REAL playlists to be generated',
  },
  {
    name: 'YASMIN',
    role: 'User Interface Design',
    description: 'Created the GUI using React, JavaScript, and CSS',
  },
];

/**
 * FrontPage Component
 * 
 * Displays the landing page with welcome message, about section, and team information.
 * Users can navigate to login from the navigation bar.
 */
const FrontPage = () => {
  const navigate = useNavigate();

  return (
    <div className="frontpage-container">
      {/* Navigation Bar */}
      <nav className="frontpage-nav">
        <img src={projectLogo} alt="Moody logo" className="logo-btn" onClick={() => navigate('/')} />
        <button onClick={() => navigate('/login')} className="login-btn">Login</button>
      </nav>
      
      {/* Welcome Section */}
      <div className="frontpage-content">
        <h1>Welcome to Moody</h1>
        <p>Generate Spotify playlists based on your moods!</p>
      </div>

      {/* About Section */}
      <div className="frontpage-about-section">
        <div className="about-animated-center">
          MOODY is our program that creates Spotify playlists based on the user's inputted mood. <br />
          There are over 30 moods to choose from, each with a unique playlist generated using Spotify's API. 
          Users can log in with their Spotify account to save and access their playlists directly on Spotify. 
          Moody utilizes Java to establish the backend of the program, MySQL to securely store user and program 
          information, and JavaScript and CSS to develop and display the graphical user interface. Our program 
          offers an accessible and interesting way for users to enhance their listening experiences, without 
          having to use any strenuous effort.
        </div>
      </div>

      {/* Team Section */}
      <div className="frontpage-team-section">
        <div className="team-grid">
          {TEAM_MEMBERS.map((member) => (
            <div key={member.name} className="team-card">
              <img src={teamPic} alt={member.name} />
              <h3>{member.name}</h3>
              <p>
                {member.role}: <br /> <br /> {member.description}
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default FrontPage;