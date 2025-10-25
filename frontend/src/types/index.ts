// User types
export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  createdAt: string;
  updatedAt: string;
}

// Account types
export interface Account {
  id: number;
  name: string;
  currency: string;
  balance: number;
  userId: number;
  createdAt: string;
  updatedAt: string;
}

// Category types
export type CategoryType = 'INCOME' | 'EXPENSE';

export interface Category {
  id: number;
  name: string;
  type: CategoryType;
  color: string;
  icon: string;
  userId: number;
  createdAt: string;
}

// Transaction types
export interface Transaction {
  id: number;
  amount: number;
  description: string;
  transactionDate: string;
  currency: string;
  notes?: string;
  receiptPath?: string;
  account: Account;
  category?: Category;
  createdAt: string;
  updatedAt: string;
}

// API Response types
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Report types
export interface MonthlyReport {
  month: number;
  year: number;
  totalIncome: number;
  totalExpense: number;
  netAmount: number;
}

export interface YearlyReport {
  year: number;
  totalIncome: number;
  totalExpense: number;
  netAmount: number;
}

export interface DashboardReport {
  currentMonth: {
    income: number;
    expense: number;
    net: number;
  };
  currentYear: {
    income: number;
    expense: number;
    net: number;
  };
  totalTransactions: number;
  month: number;
  year: number;
}

// Form types
export interface CreateTransactionForm {
  amount: number;
  description: string;
  transactionDate: string;
  currency: string;
  notes?: string;
  accountId: number;
  categoryId?: number;
}

export interface CreateAccountForm {
  name: string;
  currency: string;
  balance: number;
  userId: number;
}

export interface CreateCategoryForm {
  name: string;
  type: CategoryType;
  color: string;
  icon: string;
  userId: number;
}

// Filter types
export interface TransactionFilters {
  userId?: number;
  accountId?: number;
  categoryId?: number;
  startDate?: string;
  endDate?: string;
  minAmount?: number;
  maxAmount?: number;
  description?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}