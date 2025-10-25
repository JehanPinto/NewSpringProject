package com.example.newspring_backend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import com.example.newspring_backend.entity.Account;
import com.example.newspring_backend.entity.User;
import com.example.newspring_backend.repository.AccountRepository;
import com.example.newspring_backend.repository.UserRepository;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    // GET /api/accounts - Get all accounts
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return ResponseEntity.ok(accounts);
    }

    // GET /api/accounts/user/{userId} - Get accounts by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUser(@PathVariable Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    // GET /api/accounts/{id} - Get account by ID
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountRepository.findById(id);
        return account.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/accounts/{id}/with-transactions - Get account with transactions
    @GetMapping("/{id}/with-transactions")
    public ResponseEntity<Account> getAccountWithTransactions(@PathVariable Long id) {
        Optional<Account> account = accountRepository.findByIdWithTransactions(id);
        return account.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/accounts/user/{userId}/total-balance - Get total balance for user
    @GetMapping("/user/{userId}/total-balance")
    public ResponseEntity<BigDecimal> getTotalBalance(@PathVariable Long userId) {
        BigDecimal totalBalance = accountRepository.getTotalBalanceByUserId(userId);
        return ResponseEntity.ok(totalBalance != null ? totalBalance : BigDecimal.ZERO);
    }

    // GET /api/accounts/user/{userId}/search?name=savings - Search accounts by name
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<List<Account>> searchAccounts(@PathVariable Long userId, @RequestParam String name) {
        List<Account> accounts = accountRepository.findByUserIdAndNameContainingIgnoreCase(userId, name);
        return ResponseEntity.ok(accounts);
    }

    // GET /api/accounts/user/{userId}/currency/{currency} - Get accounts by currency
    @GetMapping("/user/{userId}/currency/{currency}")
    public ResponseEntity<List<Account>> getAccountsByCurrency(@PathVariable Long userId, @PathVariable String currency) {
        List<Account> accounts = accountRepository.findByUserIdAndCurrency(userId, currency);
        return ResponseEntity.ok(accounts);
    }

    // GET /api/accounts/user/{userId}/low-balance?threshold=100 - Get low balance accounts
    @GetMapping("/user/{userId}/low-balance")
    public ResponseEntity<List<Account>> getLowBalanceAccounts(@PathVariable Long userId, @RequestParam BigDecimal threshold) {
        List<Account> accounts = accountRepository.findByUserIdAndBalanceLessThan(userId, threshold);
        return ResponseEntity.ok(accounts);
    }

    // POST /api/accounts - Create new account
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account, @RequestParam Long userId) {
        // Find the user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Check if account name already exists for this user
        if (accountRepository.existsByUserIdAndName(userId, account.getName())) {
            return ResponseEntity.badRequest().build();
        }

        account.setUser(userOpt.get());
        Account savedAccount = accountRepository.save(account);
        return ResponseEntity.ok(savedAccount);
    }

    // PUT /api/accounts/{id} - Update account
    @PutMapping("/{id}")
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account accountDetails) {
        return accountRepository.findById(id)
            .map(account -> {
                account.setName(accountDetails.getName());
                account.setCurrency(accountDetails.getCurrency());
                account.setBalance(accountDetails.getBalance());
                account.setUpdatedAt(LocalDateTime.now());
                return ResponseEntity.ok(accountRepository.save(account));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/accounts/{id} - Delete account
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        return accountRepository.findById(id)
            .map(account -> {
                accountRepository.delete(account);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}