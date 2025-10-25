import React, { useState } from 'react';
import { Outlet } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';
import type { User } from '../../types';

interface LayoutProps {
  currentUser: User | null;
}

const Layout: React.FC<LayoutProps> = ({ currentUser }) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="flex h-screen bg-gray-50 overflow-hidden">
      {/* Sidebar */}
      <Sidebar 
        isOpen={sidebarOpen} 
        onClose={() => setSidebarOpen(false)} 
      />
      
      {/* Main Content Area */}
      <div className="flex flex-col flex-1 min-w-0 lg:ml-0">
        {/* Header */}
        <Header
          onMenuClick={() => setSidebarOpen(true)}
          currentUser={currentUser}
        />
        
        {/* Main Content */}
        <main className="flex-1 overflow-y-auto p-6 bg-gray-50">
          <div className="max-w-7xl mx-auto">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;