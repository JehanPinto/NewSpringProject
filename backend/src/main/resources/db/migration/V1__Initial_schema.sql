-- Initial schema for expense tracker
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    balance NUMERIC(19,4) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    color VARCHAR(7) DEFAULT '#6B7280',
    icon VARCHAR(50) DEFAULT 'folder',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transaction (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES category(id),
    amount NUMERIC(19,4) NOT NULL,
    transaction_date DATE NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    description TEXT,
    notes TEXT,
    receipt_path VARCHAR(512),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes for performance
CREATE INDEX idx_transaction_account_id ON transaction(account_id);
CREATE INDEX idx_transaction_category_id ON transaction(category_id);
CREATE INDEX idx_transaction_date ON transaction(transaction_date);
CREATE INDEX idx_category_user_id ON category(user_id);

-- Insert test data
INSERT INTO app_user (email, password_hash, first_name, last_name) 
VALUES ('test@example.com', 'dummy_hash', 'Test', 'User');

INSERT INTO account (user_id, name, currency, balance) 
VALUES (1, 'Main Account', 'USD', 1000.00);

INSERT INTO category (user_id, name, type, color, icon) VALUES 
(1, 'Salary', 'INCOME', '#10B981', 'briefcase'),
(1, 'Freelance', 'INCOME', '#059669', 'laptop'),
(1, 'Food & Dining', 'EXPENSE', '#EF4444', 'utensils'),
(1, 'Transportation', 'EXPENSE', '#F59E0B', 'car'),
(1, 'Shopping', 'EXPENSE', '#8B5CF6', 'shopping-bag'),
(1, 'Bills & Utilities', 'EXPENSE', '#6B7280', 'receipt');

INSERT INTO transaction (account_id, category_id, amount, transaction_date, description) VALUES 
(1, 1, 5000.00, '2024-10-01', 'Monthly salary'),
(1, 3, -45.50, '2024-10-02', 'Lunch at restaurant'),
(1, 4, -25.00, '2024-10-02', 'Uber ride'),
(1, 5, -120.00, '2024-10-03', 'Shopping'),
(1, 6, -180.00, '2024-10-05', 'Electricity bill'),
(1, 2, 800.00, '2024-10-10', 'Freelance project');