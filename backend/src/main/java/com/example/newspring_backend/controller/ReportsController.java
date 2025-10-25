package com.example.newspring_backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.newspring_backend.repository.TransactionRepository;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ReportsController {

    @Autowired
    private TransactionRepository transactionRepository;

    // GET /api/reports/monthly?userId=1&year=2024&month=10 - Monthly summary
    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(
            @RequestParam Long userId,
            @RequestParam int year,
            @RequestParam int month) {

        BigDecimal income = transactionRepository.getTotalIncomeByMonth(userId, month, year);
        BigDecimal expense = transactionRepository.getTotalExpenseByMonth(userId, month, year);
        
        Map<String, Object> report = new HashMap<>();
        report.put("month", month);
        report.put("year", year);
        report.put("totalIncome", income != null ? income : BigDecimal.ZERO);
        report.put("totalExpense", expense != null ? expense.abs() : BigDecimal.ZERO);
        report.put("netAmount", (income != null ? income : BigDecimal.ZERO)
                                .add(expense != null ? expense : BigDecimal.ZERO));
        
        return ResponseEntity.ok(report);
    }

    // GET /api/reports/yearly?userId=1&year=2024 - Yearly summary
    @GetMapping("/yearly")
    public ResponseEntity<Map<String, Object>> getYearlyReport(
            @RequestParam Long userId,
            @RequestParam int year) {

        BigDecimal income = transactionRepository.getTotalIncomeByYear(userId, year);
        BigDecimal expense = transactionRepository.getTotalExpenseByYear(userId, year);
        
        Map<String, Object> report = new HashMap<>();
        report.put("year", year);
        report.put("totalIncome", income != null ? income : BigDecimal.ZERO);
        report.put("totalExpense", expense != null ? expense.abs() : BigDecimal.ZERO);
        report.put("netAmount", (income != null ? income : BigDecimal.ZERO)
                                .add(expense != null ? expense : BigDecimal.ZERO));
        
        return ResponseEntity.ok(report);
    }

    // GET /api/reports/category?userId=1&categoryId=3&startDate=2024-01-01&endDate=2024-12-31
    @GetMapping("/category")
    public ResponseEntity<Map<String, Object>> getCategoryReport(
            @RequestParam Long userId,
            @RequestParam Long categoryId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        BigDecimal total = transactionRepository.getTotalByCategory(userId, categoryId, startDate, endDate);
        
        Map<String, Object> report = new HashMap<>();
        report.put("categoryId", categoryId);
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("total", total != null ? total : BigDecimal.ZERO);
        
        return ResponseEntity.ok(report);
    }

    // GET /api/reports/dashboard?userId=1 - Dashboard summary
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardReport(@RequestParam Long userId) {
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        
        // Current month data
        BigDecimal monthlyIncome = transactionRepository.getTotalIncomeByMonth(userId, currentMonth, currentYear);
        BigDecimal monthlyExpense = transactionRepository.getTotalExpenseByMonth(userId, currentMonth, currentYear);
        
        // Current year data
        BigDecimal yearlyIncome = transactionRepository.getTotalIncomeByYear(userId, currentYear);
        BigDecimal yearlyExpense = transactionRepository.getTotalExpenseByYear(userId, currentYear);
        
        // Transaction counts
        long totalTransactions = transactionRepository.countByAccountUserId(userId);
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Monthly summary
        Map<String, Object> monthly = new HashMap<>();
        monthly.put("income", monthlyIncome != null ? monthlyIncome : BigDecimal.ZERO);
        monthly.put("expense", monthlyExpense != null ? monthlyExpense.abs() : BigDecimal.ZERO);
        monthly.put("net", (monthlyIncome != null ? monthlyIncome : BigDecimal.ZERO)
                          .add(monthlyExpense != null ? monthlyExpense : BigDecimal.ZERO));
        
        // Yearly summary
        Map<String, Object> yearly = new HashMap<>();
        yearly.put("income", yearlyIncome != null ? yearlyIncome : BigDecimal.ZERO);
        yearly.put("expense", yearlyExpense != null ? yearlyExpense.abs() : BigDecimal.ZERO);
        yearly.put("net", (yearlyIncome != null ? yearlyIncome : BigDecimal.ZERO)
                         .add(yearlyExpense != null ? yearlyExpense : BigDecimal.ZERO));
        
        dashboard.put("currentMonth", monthly);
        dashboard.put("currentYear", yearly);
        dashboard.put("totalTransactions", totalTransactions);
        dashboard.put("month", currentMonth);
        dashboard.put("year", currentYear);
        
        return ResponseEntity.ok(dashboard);
    }
}