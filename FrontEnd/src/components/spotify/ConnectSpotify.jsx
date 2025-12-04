import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './connectSpotify.css';

const ConnectSpotify = () => {
  const navigate = useNavigate();

  // disable document scroll while this screen is mounted
  useEffect(() => {
    const previous = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    return () => {
      document.body.style.overflow = previous || '';
    };
  }, []);

  return (
    <div className="connect-spotify-container">
      <div className="connect-spotify-box">
        <button
          className="connect-spotify-btn"
          onClick={() => navigate('/home')}
        >
          Connect your Spotify
        </button>
      </div>
    </div>
  );
};

export default ConnectSpotify;
