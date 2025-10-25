import React from 'react';
import { Menu, Bell, User, Search } from 'lucide-react';
import type { User as UserType } from '../../types';

interface HeaderProps {
  onMenuClick: () => void;
  currentUser: UserType | null;
}

const Header: React.FC<HeaderProps> = ({ onMenuClick, currentUser }) => {
  return (
    <header className="bg-white shadow-sm border-b border-gray-200 px-6 py-4 flex-shrink-0 z-10">
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          {/* Mobile menu button */}
          <button
            onClick={onMenuClick}
            className="p-2 rounded-lg hover:bg-gray-100 lg:hidden"
          >
            <Menu className="w-5 h-5 text-gray-600" />
          </button>
          
          {/* Title */}
          <div>
            <h1 className="text-xl font-semibold text-gray-900">Expense Tracker</h1>
            <p className="text-sm text-gray-500 hidden sm:block">Manage your finances</p>
          </div>
        </div>

        <div className="flex items-center space-x-4">
          {/* Search - hidden on smaller screens */}
          <div className="relative hidden xl:block">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
            <input
              type="text"
              placeholder="Search transactions..."
              className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent w-64"
            />
          </div>

          {/* Search icon for mobile */}
          <button className="p-2 rounded-lg hover:bg-gray-100 xl:hidden">
            <Search className="w-5 h-5 text-gray-600" />
          </button>

          {/* Notifications */}
          <button className="p-2 rounded-lg hover:bg-gray-100 relative">
            <Bell className="w-5 h-5 text-gray-600" />
            <span className="absolute -top-1 -right-1 bg-danger-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
              3
            </span>
          </button>

          {/* User Menu */}
          <div className="flex items-center space-x-3">
            <div className="text-right hidden md:block">
              <p className="text-sm font-medium text-gray-900">
                {currentUser?.firstName} {currentUser?.lastName}
              </p>
              <p className="text-xs text-gray-500">{currentUser?.email}</p>
            </div>
            <button className="p-2 rounded-lg hover:bg-gray-100">
              <User className="w-5 h-5 text-gray-600" />
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;