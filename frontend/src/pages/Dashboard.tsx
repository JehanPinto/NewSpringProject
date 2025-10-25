import React, { useState, useEffect } from 'react';
import { 
  TrendingUp, 
  TrendingDown, 
  DollarSign, 
  CreditCard,
  ArrowUpRight,
  ArrowDownRight,
  Calendar,
  Filter
} from 'lucide-react';
import { reportsApi, transactionApi, accountApi } from '../services/api';
import type { DashboardReport, Transaction } from '../types';

const Dashboard: React.FC = () => {
  const [dashboard, setDashboard] = useState<DashboardReport | null>(null);
  const [recentTransactions, setRecentTransactions] = useState<Transaction[]>([]);
  const [totalBalance, setTotalBalance] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const userId = 1; // For now, using hardcoded user ID

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        console.log('📊 Fetching dashboard data for user:', userId);
        
        // This will call: http://localhost:8080/api/reports/dashboard?userId=1
        const dashboardData = await reportsApi.getDashboard(userId);
        console.log('✅ Dashboard data received:', dashboardData);
        setDashboard(dashboardData);
        
        // This will call: http://localhost:8080/api/transactions/user/1/recent?size=5
        const transactionsData = await transactionApi.getRecent(userId, 5);
        console.log('✅ Recent transactions received:', transactionsData);
        setRecentTransactions(transactionsData.content || []);
        
        // This will call: http://localhost:8080/api/accounts/user/1/total-balance
        const balance = await accountApi.getTotalBalance(userId);
        console.log('✅ Total balance received:', balance);
        setTotalBalance(balance);
        
        setLoading(false);
      } catch (error) {
        console.error('❌ Error fetching dashboard data:', error);
        setError(error instanceof Error ? error.message : 'An error occurred');
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [userId]);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  const formatDate = (dateStr: string) => {
    return new Date(dateStr).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
    });
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading dashboard data...</p>
          <p className="text-sm text-gray-400">Connecting to backend...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-6">
        <h3 className="text-lg font-semibold text-red-800 mb-2">Connection Error</h3>
        <p className="text-red-600 mb-4">{error}</p>
        <div className="text-sm text-red-500 space-y-1">
          <p>• Make sure your backend is running on port 8080</p>
          <p>• Check that the database is connected</p>
          <p>• Verify CORS is enabled for localhost:5173</p>
        </div>
        <button 
          onClick={() => window.location.reload()} 
          className="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
        >
          Retry Connection
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600">Welcome back! Here's your financial overview.</p>
        </div>
        <div className="flex items-center space-x-3">
          <button className="flex items-center space-x-2 px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50">
            <Calendar className="w-4 h-4" />
            <span>This Month</span>
          </button>
          <button className="flex items-center space-x-2 px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50">
            <Filter className="w-4 h-4" />
            <span>Filter</span>
          </button>
        </div>
      </div>

      {/* Connection Status */}
      <div className="bg-success-50 border border-success-200 rounded-lg p-4">
        <div className="flex items-center">
          <div className="flex-shrink-0">
            <div className="w-2 h-2 bg-success-400 rounded-full animate-pulse"></div>
          </div>
          <div className="ml-3">
            <p className="text-sm text-success-800">
              <strong>✅ Backend Connected:</strong> Successfully loaded data from Spring Boot API
            </p>
          </div>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Total Balance */}
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Total Balance</p>
              <p className="text-2xl font-bold text-gray-900">
                {formatCurrency(totalBalance)}
              </p>
            </div>
            <div className="p-3 bg-primary-100 rounded-lg">
              <DollarSign className="w-6 h-6 text-primary-600" />
            </div>
          </div>
          <div className="mt-4 flex items-center text-sm">
            <ArrowUpRight className="w-4 h-4 text-success-500 mr-1" />
            <span className="text-success-600 font-medium">+2.5%</span>
            <span className="text-gray-500 ml-1">from last month</span>
          </div>
        </div>

        {/* Monthly Income */}
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Monthly Income</p>
              <p className="text-2xl font-bold text-gray-900">
                {dashboard && formatCurrency(dashboard.currentMonth.income)}
              </p>
            </div>
            <div className="p-3 bg-success-100 rounded-lg">
              <TrendingUp className="w-6 h-6 text-success-600" />
            </div>
          </div>
          <div className="mt-4 flex items-center text-sm">
            <ArrowUpRight className="w-4 h-4 text-success-500 mr-1" />
            <span className="text-success-600 font-medium">+12.3%</span>
            <span className="text-gray-500 ml-1">from last month</span>
          </div>
        </div>

        {/* Monthly Expenses */}
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Monthly Expenses</p>
              <p className="text-2xl font-bold text-gray-900">
                {dashboard && formatCurrency(Math.abs(dashboard.currentMonth.expense))}
              </p>
            </div>
            <div className="p-3 bg-danger-100 rounded-lg">
              <TrendingDown className="w-6 h-6 text-danger-600" />
            </div>
          </div>
          <div className="mt-4 flex items-center text-sm">
            <ArrowDownRight className="w-4 h-4 text-danger-500 mr-1" />
            <span className="text-danger-600 font-medium">-8.1%</span>
            <span className="text-gray-500 ml-1">from last month</span>
          </div>
        </div>

        {/* Net Amount */}
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600">Net Amount</p>
              <p className={`text-2xl font-bold ${ (dashboard?.currentMonth?.net ?? 0) >= 0 ? 'text-success-600' : 'text-danger-600' }`}>
                {formatCurrency(dashboard?.currentMonth?.net ?? 0)}
              </p>
            </div>
            <div className="p-3 bg-primary-100 rounded-lg">
              <CreditCard className="w-6 h-6 text-primary-600" />
            </div>
          </div>
          <div className="mt-4 flex items-center text-sm">
            <ArrowUpRight className="w-4 h-4 text-success-500 mr-1" />
            <span className="text-success-600 font-medium">+15.2%</span>
            <span className="text-gray-500 ml-1">from last month</span>
          </div>
        </div>
      </div>

      {/* Recent Transactions */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-6 border-b border-gray-200">
            <h3 className="text-lg font-semibold text-gray-900">Recent Transactions</h3>
            <p className="text-sm text-gray-500">Latest {recentTransactions.length} transactions from your accounts</p>
          </div>
          <div className="p-6">
            {recentTransactions.length > 0 ? (
              <div className="space-y-4">
                {recentTransactions.map((transaction) => (
                  <div key={transaction.id} className="flex items-center justify-between">
                    <div className="flex items-center space-x-3">
                      <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                        transaction.amount >= 0 ? 'bg-success-100' : 'bg-danger-100'
                      }`}>
                        {transaction.amount >= 0 ? (
                          <ArrowUpRight className="w-5 h-5 text-success-600" />
                        ) : (
                          <ArrowDownRight className="w-5 h-5 text-danger-600" />
                        )}
                      </div>
                      <div>
                        <p className="text-sm font-medium text-gray-900">
                          {transaction.description}
                        </p>
                        <p className="text-xs text-gray-500">
                          {formatDate(transaction.transactionDate)} • {transaction.account.name}
                        </p>
                      </div>
                    </div>
                    <p className={`text-sm font-semibold ${
                      transaction.amount >= 0 ? 'text-success-600' : 'text-danger-600'
                    }`}>
                      {transaction.amount >= 0 ? '+' : ''}{formatCurrency(transaction.amount)}
                    </p>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 text-center py-8">No recent transactions found</p>
            )}
            <button className="w-full mt-4 px-4 py-2 text-primary-600 hover:text-primary-800 text-sm font-medium">
              View All Transactions →
            </button>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-6 border-b border-gray-200">
            <h3 className="text-lg font-semibold text-gray-900">Quick Actions</h3>
            <p className="text-sm text-gray-500">Common tasks and shortcuts</p>
          </div>
          <div className="p-6">
            <div className="grid grid-cols-2 gap-4">
              <button className="p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-300 hover:bg-primary-50 transition-colors">
                <TrendingUp className="w-6 h-6 text-primary-600 mx-auto mb-2" />
                <p className="text-sm font-medium text-gray-900">Add Income</p>
              </button>
              <button className="p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-danger-300 hover:bg-danger-50 transition-colors">
                <TrendingDown className="w-6 h-6 text-danger-600 mx-auto mb-2" />
                <p className="text-sm font-medium text-gray-900">Add Expense</p>
              </button>
              <button className="p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-300 hover:bg-primary-50 transition-colors">
                <CreditCard className="w-6 h-6 text-primary-600 mx-auto mb-2" />
                <p className="text-sm font-medium text-gray-900">New Account</p>
              </button>
              <button className="p-4 border-2 border-dashed border-gray-300 rounded-lg hover:border-primary-300 hover:bg-primary-50 transition-colors">
                <Calendar className="w-6 h-6 text-primary-600 mx-auto mb-2" />
                <p className="text-sm font-medium text-gray-900">View Reports</p>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* API Connectivity Info */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-6">
        <h3 className="text-lg font-semibold text-blue-900 mb-2">🔗 API Connectivity Status</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
          <div>
            <p className="font-medium text-blue-800">Frontend</p>
            <p className="text-blue-600">React + Vite (Port 5173)</p>
          </div>
          <div>
            <p className="font-medium text-blue-800">Backend</p>
            <p className="text-blue-600">Spring Boot (Port 8080)</p>
          </div>
          <div>
            <p className="font-medium text-blue-800">Database</p>
            <p className="text-blue-600">PostgreSQL (Port 5432)</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;