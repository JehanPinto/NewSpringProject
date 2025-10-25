import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  CreditCard,
  Receipt,
  Settings,
  Wallet,
  Tag,
  TrendingUp,
  X
} from 'lucide-react';
import clsx from 'clsx';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

const navigation = [
  { name: 'Dashboard', href: '/', icon: LayoutDashboard },
  { name: 'Transactions', href: '/transactions', icon: Receipt },
  { name: 'Accounts', href: '/accounts', icon: Wallet },
  { name: 'Categories', href: '/categories', icon: Tag },
  { name: 'Reports', href: '/reports', icon: TrendingUp },
  { name: 'Settings', href: '/settings', icon: Settings },
];

const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  return (
    <>
      {/* Mobile overlay */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <div
        className={clsx(
          'fixed inset-y-0 left-0 z-30 w-64 bg-white shadow-lg border-r border-gray-200 transform transition-transform duration-300 ease-in-out',
          'lg:translate-x-0 lg:static lg:inset-0',
          isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        )}
      >
        {/* Header Section */}
        <div className="flex items-center justify-between h-16 px-6 border-b border-gray-200 bg-white">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center">
              <CreditCard className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg font-semibold text-gray-900">ExpenseTracker</span>
          </div>
          <button
            onClick={onClose}
            className="p-2 rounded-lg hover:bg-gray-100 lg:hidden"
          >
            <X className="w-5 h-5 text-gray-600" />
          </button>
        </div>

        {/* Navigation Section */}
        <nav className="flex-1 px-4 py-6 space-y-1 overflow-y-auto">
          {navigation.map((item) => (
            <NavLink
              key={item.name}
              to={item.href}
              className={({ isActive }) =>
                clsx(
                  'flex items-center space-x-3 px-4 py-3 rounded-lg text-sm font-medium transition-all duration-200',
                  'hover:bg-gray-50 hover:text-gray-900',
                  isActive
                    ? 'bg-primary-50 text-primary-600 border-r-4 border-primary-600 font-semibold'
                    : 'text-gray-600'
                )
              }
              onClick={() => window.innerWidth < 1024 && onClose()}
            >
              <item.icon className="w-5 h-5 flex-shrink-0" />
              <span className="truncate">{item.name}</span>
            </NavLink>
          ))}
        </nav>

        {/* Bottom Help Section */}
        <div className="p-4 border-t border-gray-200 bg-white">
          <div className="bg-primary-50 rounded-lg p-4">
            <h4 className="text-sm font-medium text-primary-900 mb-1">
              Need Help?
            </h4>
            <p className="text-xs text-primary-700 mb-3 line-clamp-2">
              Check our documentation for guides and tutorials
            </p>
            <button className="text-xs text-primary-600 font-medium hover:text-primary-800 transition-colors">
              Get Support →
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default Sidebar;