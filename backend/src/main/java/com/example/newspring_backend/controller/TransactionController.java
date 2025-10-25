package com.example.newspring_backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.example.newspring_backend.entity.Account;
import com.example.newspring_backend.entity.Category;
import com.example.newspring_backend.entity.Transaction;
import com.example.newspring_backend.repository.AccountRepository;
import com.example.newspring_backend.repository.CategoryRepository;
import com.example.newspring_backend.repository.TransactionRepository;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // GET /api/transactions - Get all transactions with pagination and filtering
    @GetMapping
    public ResponseEntity<Page<Transaction>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transaction> transactions;

        if (userId != null) {
            transactions = transactionRepository.findTransactionsWithFilters(
                userId, accountId, categoryId, startDate, endDate, minAmount, maxAmount, pageable);
        } else {
            transactions = transactionRepository.findAll(pageable);
        }

        return ResponseEntity.ok(transactions);
    }

    // GET /api/transactions/user/{userId} - Get transactions by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Transaction>> getTransactionsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Transaction> transactions = transactionRepository.findByAccountUserId(userId, pageable);
        
        return ResponseEntity.ok(transactions);
    }

    // GET /api/transactions/user/{userId}/recent - Get recent transactions
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<Page<Transaction>> getRecentTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(0, size);
        Page<Transaction> transactions = transactionRepository.findRecentTransactions(userId, pageable);
        
        return ResponseEntity.ok(transactions);
    }

    // GET /api/transactions/{id} - Get transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return transaction.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/transactions/account/{accountId} - Get transactions by account
    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<Transaction>> getTransactionsByAccount(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionRepository.findByAccountId(accountId, pageable);
        
        return ResponseEntity.ok(transactions);
    }

    // GET /api/transactions/category/{categoryId} - Get transactions by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Transaction>> getTransactionsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionRepository.findByCategoryId(categoryId, pageable);
        
        return ResponseEntity.ok(transactions);
    }

    // GET /api/transactions/search - Search transactions by description
    @GetMapping("/search")
    public ResponseEntity<Page<Transaction>> searchTransactions(
            @RequestParam Long userId,
            @RequestParam String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionRepository.findByAccountUserIdAndDescriptionContainingIgnoreCase(
            userId, description, pageable);
        
        return ResponseEntity.ok(transactions);
    }

    // POST /api/transactions - Create new transaction
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction, 
                                                        @RequestParam Long accountId,
                                                        @RequestParam(required = false) Long categoryId) {
        // Find the account
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Find the category (optional)
        if (categoryId != null) {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            transaction.setCategory(categoryOpt.get());
        }

        transaction.setAccount(accountOpt.get());
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return ResponseEntity.ok(savedTransaction);
    }

    // PUT /api/transactions/{id} - Update transaction
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transactionDetails) {
        return transactionRepository.findById(id)
            .map(transaction -> {
                transaction.setAmount(transactionDetails.getAmount());
                transaction.setDescription(transactionDetails.getDescription());
                transaction.setTransactionDate(transactionDetails.getTransactionDate());
                transaction.setNotes(transactionDetails.getNotes());
                transaction.setCurrency(transactionDetails.getCurrency());
                transaction.setUpdatedAt(LocalDateTime.now());
                return ResponseEntity.ok(transactionRepository.save(transaction));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/transactions/{id} - Delete transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        return transactionRepository.findById(id)
            .map(transaction -> {
                transactionRepository.delete(transaction);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}