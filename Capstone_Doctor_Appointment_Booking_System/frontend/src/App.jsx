import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        {/* Navigation Bar will go here */}
        
        <div className="container mt-4">
          <Routes>
            <Route path="/" element={<h1>Welcome to Doctor Appointment System</h1>} />
            {/* Add more routes here later */}
          </Routes>
        </div>

        {/* Global Toast Notifications */}
        <ToastContainer position="bottom-right" />
      </div>
    </Router>
  );
}

export default App;
