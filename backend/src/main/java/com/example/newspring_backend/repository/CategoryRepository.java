package com.example.newspring_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.newspring_backend.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find all categories for a specific user
    List<Category> findByUserId(Long userId);
    
    // Find categories by type (INCOME or EXPENSE)
    List<Category> findByUserIdAndType(Long userId, Category.CategoryType type);
    
    // Find categories by name (for search)
    List<Category> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);
    
    // Find category with transactions (for detailed view)
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.transactions WHERE c.id = :id")
    Optional<Category> findByIdWithTransactions(@Param("id") Long id);
    
    // Check if category name exists for user (to prevent duplicates)
    boolean existsByUserIdAndName(Long userId, String name);
    
    // Find income categories for a user
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.type = 'INCOME' ORDER BY c.name")
    List<Category> findIncomeCategories(@Param("userId") Long userId);
    
    // Find expense categories for a user
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.type = 'EXPENSE' ORDER BY c.name")
    List<Category> findExpenseCategories(@Param("userId") Long userId);
    
    // Count categories by type
    @Query("SELECT COUNT(c) FROM Category c WHERE c.user.id = :userId AND c.type = :type")
    long countByUserIdAndType(@Param("userId") Long userId, @Param("type") Category.CategoryType type);
}