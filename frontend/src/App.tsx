import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout/Layout';
import Dashboard from './pages/Dashboard';
import { userApi } from './services/api';
import type { User } from './types';  // ← Add 'type' here
import './App.css';

function App() {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCurrentUser = async () => {
      try {
        const users = await userApi.getAll();
        if (users.length > 0) {
          setCurrentUser(users[0]); // Use first user for demo
        }
        setLoading(false);
      } catch (error) {
        console.error('Error fetching user:', error);
        setLoading(false);
      }
    };

    fetchCurrentUser();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading application...</p>
        </div>
      </div>
    );
  }

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Layout currentUser={currentUser} />}>
          <Route index element={<Dashboard />} />
          <Route path="transactions" element={<div className="p-8 text-center text-gray-500">Transactions page coming soon...</div>} />
          <Route path="accounts" element={<div className="p-8 text-center text-gray-500">Accounts page coming soon...</div>} />
          <Route path="categories" element={<div className="p-8 text-center text-gray-500">Categories page coming soon...</div>} />
          <Route path="reports" element={<div className="p-8 text-center text-gray-500">Reports page coming soon...</div>} />
          <Route path="settings" element={<div className="p-8 text-center text-gray-500">Settings page coming soon...</div>} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;