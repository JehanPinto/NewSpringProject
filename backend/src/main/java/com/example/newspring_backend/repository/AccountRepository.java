package com.example.newspring_backend.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.newspring_backend.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    // Find all accounts for a specific user
    List<Account> findByUserId(Long userId);
    
    // Find accounts by name (for search)
    List<Account> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);
    
    // Find accounts by currency
    List<Account> findByUserIdAndCurrency(Long userId, String currency);
    
    // Find account with transactions (for detailed view)
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.transactions WHERE a.id = :id")
    Optional<Account> findByIdWithTransactions(@Param("id") Long id);
    
    // Calculate total balance for a user across all accounts
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);
    
    // Find accounts with balance greater than specified amount
    List<Account> findByUserIdAndBalanceGreaterThan(Long userId, BigDecimal amount);
    
    // Find accounts with balance less than specified amount (low balance alert)
    List<Account> findByUserIdAndBalanceLessThan(Long userId, BigDecimal amount);
    
    // Check if account name exists for user (to prevent duplicates)
    boolean existsByUserIdAndName(Long userId, String name);
}