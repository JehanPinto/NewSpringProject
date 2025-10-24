package com.example.newspring_backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.newspring_backend.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Basic filtering methods
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
    Page<Transaction> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Transaction> findByAccountUserId(Long userId, Pageable pageable);
    
    // Date range filtering
    Page<Transaction> findByAccountUserIdAndTransactionDateBetween(
        Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    // Amount filtering
    Page<Transaction> findByAccountUserIdAndAmountGreaterThan(
        Long userId, BigDecimal amount, Pageable pageable);
    
    Page<Transaction> findByAccountUserIdAndAmountLessThan(
        Long userId, BigDecimal amount, Pageable pageable);
    
    // Combined filtering
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND (:accountId IS NULL OR t.account.id = :accountId) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:startDate IS NULL OR t.transactionDate >= :startDate) " +
           "AND (:endDate IS NULL OR t.transactionDate <= :endDate) " +
           "AND (:minAmount IS NULL OR t.amount >= :minAmount) " +
           "AND (:maxAmount IS NULL OR t.amount <= :maxAmount)")
    Page<Transaction> findTransactionsWithFilters(
        @Param("userId") Long userId,
        @Param("accountId") Long accountId,
        @Param("categoryId") Long categoryId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("minAmount") BigDecimal minAmount,
        @Param("maxAmount") BigDecimal maxAmount,
        Pageable pageable);
    
    // Search by description
    Page<Transaction> findByAccountUserIdAndDescriptionContainingIgnoreCase(
        Long userId, String description, Pageable pageable);
    
    // Monthly summaries
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.amount > 0 AND EXTRACT(MONTH FROM t.transactionDate) = :month " +
           "AND EXTRACT(YEAR FROM t.transactionDate) = :year")
    BigDecimal getTotalIncomeByMonth(@Param("userId") Long userId, 
                                   @Param("month") int month, 
                                   @Param("year") int year);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.amount < 0 AND EXTRACT(MONTH FROM t.transactionDate) = :month " +
           "AND EXTRACT(YEAR FROM t.transactionDate) = :year")
    BigDecimal getTotalExpenseByMonth(@Param("userId") Long userId, 
                                    @Param("month") int month, 
                                    @Param("year") int year);
    
    // Yearly summaries
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.amount > 0 AND EXTRACT(YEAR FROM t.transactionDate) = :year")
    BigDecimal getTotalIncomeByYear(@Param("userId") Long userId, @Param("year") int year);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.amount < 0 AND EXTRACT(YEAR FROM t.transactionDate) = :year")
    BigDecimal getTotalExpenseByYear(@Param("userId") Long userId, @Param("year") int year);
    
    // Category-wise spending
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.category.id = :categoryId AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalByCategory(@Param("userId") Long userId, 
                                @Param("categoryId") Long categoryId,
                                @Param("startDate") LocalDate startDate, 
                                @Param("endDate") LocalDate endDate);
    
    // Recent transactions
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId " +
           "ORDER BY t.transactionDate DESC, t.createdAt DESC")
    Page<Transaction> findRecentTransactions(@Param("userId") Long userId, Pageable pageable);
    
    // Count transactions
    long countByAccountUserId(Long userId);
    long countByAccountUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}