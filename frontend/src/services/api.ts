import axios from 'axios';
import type {
  User,
  Account,
  Category,
  Transaction,
  PageResponse,
  MonthlyReport,
  YearlyReport,
  DashboardReport,
  CreateTransactionForm,
  CreateAccountForm,
  CreateCategoryForm,
  TransactionFilters
} from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for logging
api.interceptors.request.use(
  (config) => {
    console.log(`Making ${config.method?.toUpperCase()} request to:`, config.url);
    return config;
  },
  (error) => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// User API
export const userApi = {
  getAll: (): Promise<User[]> => 
    api.get('/users').then(res => res.data),
  
  getById: (id: number): Promise<User> => 
    api.get(`/users/${id}`).then(res => res.data),
  
  getWithAccounts: (id: number): Promise<User> => 
    api.get(`/users/${id}/with-accounts`).then(res => res.data),
  
  getWithCategories: (id: number): Promise<User> => 
    api.get(`/users/${id}/with-categories`).then(res => res.data),
  
  search: (name: string): Promise<User[]> => 
    api.get(`/users/search?name=${name}`).then(res => res.data),
  
  create: (user: Omit<User, 'id' | 'createdAt' | 'updatedAt'>): Promise<User> => 
    api.post('/users', user).then(res => res.data),
  
  update: (id: number, user: Partial<User>): Promise<User> => 
    api.put(`/users/${id}`, user).then(res => res.data),
  
  delete: (id: number): Promise<void> => 
    api.delete(`/users/${id}`).then(() => {}),
};

// Account API
export const accountApi = {
  getAll: (): Promise<Account[]> => 
    api.get('/accounts').then(res => res.data),
  
  getByUser: (userId: number): Promise<Account[]> => 
    api.get(`/accounts/user/${userId}`).then(res => res.data),
  
  getById: (id: number): Promise<Account> => 
    api.get(`/accounts/${id}`).then(res => res.data),
  
  getWithTransactions: (id: number): Promise<Account> => 
    api.get(`/accounts/${id}/with-transactions`).then(res => res.data),
  
  getTotalBalance: (userId: number): Promise<number> => 
    api.get(`/accounts/user/${userId}/total-balance`).then(res => res.data),
  
  search: (userId: number, name: string): Promise<Account[]> => 
    api.get(`/accounts/user/${userId}/search?name=${name}`).then(res => res.data),
  
  getByCurrency: (userId: number, currency: string): Promise<Account[]> => 
    api.get(`/accounts/user/${userId}/currency/${currency}`).then(res => res.data),
  
  getLowBalance: (userId: number, threshold: number): Promise<Account[]> => 
    api.get(`/accounts/user/${userId}/low-balance?threshold=${threshold}`).then(res => res.data),
  
  create: (account: CreateAccountForm): Promise<Account> => 
    api.post(`/accounts?userId=${account.userId}`, account).then(res => res.data),
  
  update: (id: number, account: Partial<Account>): Promise<Account> => 
    api.put(`/accounts/${id}`, account).then(res => res.data),
  
  delete: (id: number): Promise<void> => 
    api.delete(`/accounts/${id}`).then(() => {}),
};

// Category API
export const categoryApi = {
  getAll: (): Promise<Category[]> => 
    api.get('/categories').then(res => res.data),
  
  getByUser: (userId: number): Promise<Category[]> => 
    api.get(`/categories/user/${userId}`).then(res => res.data),
  
  getIncomeCategories: (userId: number): Promise<Category[]> => 
    api.get(`/categories/user/${userId}/income`).then(res => res.data),
  
  getExpenseCategories: (userId: number): Promise<Category[]> => 
    api.get(`/categories/user/${userId}/expense`).then(res => res.data),
  
  getById: (id: number): Promise<Category> => 
    api.get(`/categories/${id}`).then(res => res.data),
  
  getWithTransactions: (id: number): Promise<Category> => 
    api.get(`/categories/${id}/with-transactions`).then(res => res.data),
  
  search: (userId: number, name: string): Promise<Category[]> => 
    api.get(`/categories/user/${userId}/search?name=${name}`).then(res => res.data),
  
  getByType: (userId: number, type: string): Promise<Category[]> => 
    api.get(`/categories/user/${userId}/type/${type}`).then(res => res.data),
  
  countByType: (userId: number, type: string): Promise<number> => 
    api.get(`/categories/user/${userId}/count/${type}`).then(res => res.data),
  
  create: (category: CreateCategoryForm): Promise<Category> => 
    api.post(`/categories?userId=${category.userId}`, category).then(res => res.data),
  
  update: (id: number, category: Partial<Category>): Promise<Category> => 
    api.put(`/categories/${id}`, category).then(res => res.data),
  
  delete: (id: number): Promise<void> => 
    api.delete(`/categories/${id}`).then(() => {}),
};

// Transaction API
export const transactionApi = {
  getAll: (filters: TransactionFilters = {}): Promise<PageResponse<Transaction>> => {
    const params = new URLSearchParams();
    
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        params.append(key, value.toString());
      }
    });
    
    return api.get(`/transactions?${params.toString()}`).then(res => res.data);
  },
  
  getByUser: (userId: number, page = 0, size = 10, sortBy = 'transactionDate', sortDir = 'desc'): Promise<PageResponse<Transaction>> => 
    api.get(`/transactions/user/${userId}?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`).then(res => res.data),
  
  getRecent: (userId: number, size = 5): Promise<PageResponse<Transaction>> => 
    api.get(`/transactions/user/${userId}/recent?size=${size}`).then(res => res.data),
  
  getById: (id: number): Promise<Transaction> => 
    api.get(`/transactions/${id}`).then(res => res.data),
  
  getByAccount: (accountId: number, page = 0, size = 10): Promise<PageResponse<Transaction>> => 
    api.get(`/transactions/account/${accountId}?page=${page}&size=${size}`).then(res => res.data),
  
  getByCategory: (categoryId: number, page = 0, size = 10): Promise<PageResponse<Transaction>> => 
    api.get(`/transactions/category/${categoryId}?page=${page}&size=${size}`).then(res => res.data),
  
  search: (userId: number, description: string, page = 0, size = 10): Promise<PageResponse<Transaction>> => 
    api.get(`/transactions/search?userId=${userId}&description=${description}&page=${page}&size=${size}`).then(res => res.data),
  
  create: (transaction: CreateTransactionForm): Promise<Transaction> => {
    const params = new URLSearchParams();
    params.append('accountId', transaction.accountId.toString());
    if (transaction.categoryId) {
      params.append('categoryId', transaction.categoryId.toString());
    }
    
    return api.post(`/transactions?${params.toString()}`, transaction).then(res => res.data);
  },
  
  update: (id: number, transaction: Partial<Transaction>): Promise<Transaction> => 
    api.put(`/transactions/${id}`, transaction).then(res => res.data),
  
  delete: (id: number): Promise<void> => 
    api.delete(`/transactions/${id}`).then(() => {}),
};

// Reports API
export const reportsApi = {
  getMonthly: (userId: number, year: number, month: number): Promise<MonthlyReport> => 
    api.get(`/reports/monthly?userId=${userId}&year=${year}&month=${month}`).then(res => res.data),
  
  getYearly: (userId: number, year: number): Promise<YearlyReport> => 
    api.get(`/reports/yearly?userId=${userId}&year=${year}`).then(res => res.data),
  
  getCategory: (userId: number, categoryId: number, startDate: string, endDate: string): Promise<unknown> => 
    api.get(`/reports/category?userId=${userId}&categoryId=${categoryId}&startDate=${startDate}&endDate=${endDate}`).then(res => res.data),
  
  getDashboard: (userId: number): Promise<DashboardReport> => 
    api.get(`/reports/dashboard?userId=${userId}`).then(res => res.data),
};

export default api;