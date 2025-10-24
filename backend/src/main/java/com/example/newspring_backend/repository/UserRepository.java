package com.example.newspring_backend.repository;

import com.example.newspring_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email (for login)
    Optional<User> findByEmail(String email);
    
    // Check if email already exists (for registration)
    boolean existsByEmail(String email);
    
    // Find users by name (for search functionality)
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String firstName, String lastName);
    
    // Custom query to find user with their accounts
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.accounts WHERE u.id = :id")
    Optional<User> findByIdWithAccounts(@Param("id") Long id);
    
    // Custom query to find user with their categories
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.categories WHERE u.id = :id")
    Optional<User> findByIdWithCategories(@Param("id") Long id);
}