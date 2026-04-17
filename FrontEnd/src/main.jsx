import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'

/**
 * Application bootstrap entry point.
 * Mounts the React app into the root DOM element.
 * @author Yasmin Zubair
 * Date: April 15th, 2026
 */
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)

//npm install @emotion/react @emotion/styled @maptiler/geocoding-control @maptiler/sdk @mui/icons-material @mui/material