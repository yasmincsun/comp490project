import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './connectSpotify.css';

/**
 * ConnectSpotify component.
 * Displays a Spotify connection call-to-action and disables page scroll while mounted.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
const ConnectSpotify = () => {
  const navigate = useNavigate();

  /**
   * Disable page scrolling while the Spotify connect screen is displayed.
   * Restores the previous document overflow style when the component unmounts.
   */
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
            onClick={() => {
               window.location.href = 'http://127.0.0.1:8080/login';
              }}
              >
                Connect your Spotify
        </button>
      </div>
    </div>
  );
};
 
export default ConnectSpotify;
