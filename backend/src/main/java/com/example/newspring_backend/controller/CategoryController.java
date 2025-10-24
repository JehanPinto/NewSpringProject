package com.example.newspring_backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.newspring_backend.entity.Category;
import com.example.newspring_backend.entity.User;
import com.example.newspring_backend.repository.CategoryRepository;
import com.example.newspring_backend.repository.UserRepository;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:5173")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // GET /api/categories - Get all categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    // GET /api/categories/user/{userId} - Get categories by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Category>> getCategoriesByUser(@PathVariable Long userId) {
        List<Category> categories = categoryRepository.findByUserId(userId);
        return ResponseEntity.ok(categories);
    }

    // GET /api/categories/user/{userId}/income - Get income categories
    @GetMapping("/user/{userId}/income")
    public ResponseEntity<List<Category>> getIncomeCategories(@PathVariable Long userId) {
        List<Category> categories = categoryRepository.findIncomeCategories(userId);
        return ResponseEntity.ok(categories);
    }

    // GET /api/categories/user/{userId}/expense - Get expense categories
    @GetMapping("/user/{userId}/expense")
    public ResponseEntity<List<Category>> getExpenseCategories(@PathVariable Long userId) {
        List<Category> categories = categoryRepository.findExpenseCategories(userId);
        return ResponseEntity.ok(categories);
    }

    // GET /api/categories/{id} - Get category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/categories/{id}/with-transactions - Get category with transactions
    @GetMapping("/{id}/with-transactions")
    public ResponseEntity<Category> getCategoryWithTransactions(@PathVariable Long id) {
        Optional<Category> category = categoryRepository.findByIdWithTransactions(id);
        return category.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/categories/user/{userId}/search?name=food - Search categories
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<Category>> searchCategories(@PathVariable Long userId, @RequestParam String name) {
        List<Category> categories = categoryRepository.findByUserIdAndNameContainingIgnoreCase(userId, name);
        return ResponseEntity.ok(categories);
    }

    // GET /api/categories/user/{userId}/type/{type} - Get categories by type
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<Category>> getCategoriesByType(@PathVariable Long userId, @PathVariable String type) {
        try {
            Category.CategoryType categoryType = Category.CategoryType.valueOf(type.toUpperCase());
            List<Category> categories = categoryRepository.findByUserIdAndType(userId, categoryType);
            return ResponseEntity.ok(categories);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/categories/user/{userId}/count/{type} - Count categories by type
    @GetMapping("/user/{userId}/count/{type}")
    public ResponseEntity<Long> countCategoriesByType(@PathVariable Long userId, @PathVariable String type) {
        try {
            Category.CategoryType categoryType = Category.CategoryType.valueOf(type.toUpperCase());
            long count = categoryRepository.countByUserIdAndType(userId, categoryType);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST /api/categories - Create new category
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category, @RequestParam Long userId) {
        // Find the user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Check if category name already exists for this user
        if (categoryRepository.existsByUserIdAndName(userId, category.getName())) {
            return ResponseEntity.badRequest().build();
        }

        category.setUser(userOpt.get());
        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(savedCategory);
    }

    // PUT /api/categories/{id} - Update category
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        return categoryRepository.findById(id)
            .map(category -> {
                category.setName(categoryDetails.getName());
                category.setType(categoryDetails.getType());
                category.setColor(categoryDetails.getColor());
                category.setIcon(categoryDetails.getIcon());
                return ResponseEntity.ok(categoryRepository.save(category));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/categories/{id} - Delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        return categoryRepository.findById(id)
            .map(category -> {
                categoryRepository.delete(category);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}