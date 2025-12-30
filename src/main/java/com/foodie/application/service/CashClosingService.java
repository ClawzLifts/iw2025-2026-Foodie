package com.foodie.application.service;

import com.foodie.application.domain.CashClosing;
import com.foodie.application.domain.OrderStatus;
import com.foodie.application.domain.PaymentMethod;
import com.foodie.application.dto.CashClosingDto;
import com.foodie.application.dto.OrderDto;
import com.foodie.application.repository.CashClosingRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing cash closing operations
 * Handles opening cash, closing cash, and calculating differences
 */
@Service
public class CashClosingService {

    private final CashClosingRepository cashClosingRepository;
    private final OrderService orderService;

    public CashClosingService(CashClosingRepository cashClosingRepository, OrderService orderService) {
        this.cashClosingRepository = cashClosingRepository;
        this.orderService = orderService;
    }

    /**
     * Get today's sales by payment method
     */
    public Map<String, Double> getTodaysSalesByPaymentMethod() {
        LocalDate today = LocalDate.now();
        Map<String, Double> sales = new HashMap<>();

        // Initialize all payment methods with 0
        for (PaymentMethod method : PaymentMethod.values()) {
            sales.put(method.toString(), 0.0);
        }

        // Get all completed orders for today
        List<OrderDto> todaysOrders = orderService.getAllOrdersAsDto().stream()
                .filter(order -> order.getDate().equals(today) && order.getStatus() == OrderStatus.COMPLETED)
                .toList();

        // Sum sales by payment method
        for (OrderDto order : todaysOrders) {
            if (order.getPayment() != null) {
                String paymentMethod = order.getPayment().getPaymentMethod().toString();
                Double amount = order.getPayment().getPaymentAmount();
                sales.put(paymentMethod, sales.getOrDefault(paymentMethod, 0.0) + amount);
            }
        }

        return sales;
    }

    /**
     * Create a cash opening for today
     */
    @Transactional
    public CashClosingDto openCash(Map<String, Double> openingBalance) {
        LocalDate today = LocalDate.now();

        // Check if there's already an open cash for today
        CashClosing existingClosure = cashClosingRepository.findLatestByDate(today).orElse(null);
        if (existingClosure != null && !existingClosure.getIsClosed()) {
            throw new IllegalStateException("There's already an open cash for today");
        }

        // Initialize all payment methods with 0 if not provided
        Map<String, Double> initialBalance = new HashMap<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            initialBalance.put(method.toString(), openingBalance.getOrDefault(method.toString(), 0.0));
        }

        CashClosing cashClosing = new CashClosing();
        cashClosing.setDate(today);
        cashClosing.setOpeningBalance(initialBalance);
        cashClosing.setExpectedAmount(new HashMap<>());
        cashClosing.setRealAmount(new HashMap<>());
        cashClosing.setDifference(new HashMap<>());
        cashClosing.setIsClosed(false);

        cashClosing = cashClosingRepository.save(cashClosing);
        return CashClosingDto.fromCashClosing(cashClosing);
    }

    /**
     * Close cash for today with real amounts and calculate differences
     */
    @Transactional
    public CashClosingDto closeCash(Map<String, Double> realAmount, String notes) {
        LocalDate today = LocalDate.now();

        CashClosing cashClosing = cashClosingRepository.findLatestByDate(today)
                .orElseThrow(() -> new EntityNotFoundException("No open cash found for today"));

        if (cashClosing.getIsClosed()) {
            throw new IllegalStateException("Cash is already closed for today");
        }

        // Get today's sales by payment method
        Map<String, Double> todaysSales = getTodaysSalesByPaymentMethod();

        // Calculate expected amount: opening balance + sales for each payment method
        Map<String, Double> expectedAmount = new HashMap<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            String methodName = method.toString();
            Double openingBalance = cashClosing.getOpeningBalance().getOrDefault(methodName, 0.0);
            Double sales = todaysSales.getOrDefault(methodName, 0.0);
            expectedAmount.put(methodName, openingBalance + sales);
        }

        // Initialize real amounts with all payment methods
        Map<String, Double> realAmounts = new HashMap<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            realAmounts.put(method.toString(), realAmount.getOrDefault(method.toString(), 0.0));
        }

        // Calculate differences: real - expected
        Map<String, Double> difference = new HashMap<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            String methodName = method.toString();
            Double expected = expectedAmount.getOrDefault(methodName, 0.0);
            Double real = realAmounts.getOrDefault(methodName, 0.0);
            difference.put(methodName, real - expected);
        }

        // Update cash closing
        cashClosing.setExpectedAmount(expectedAmount);
        cashClosing.setRealAmount(realAmounts);
        cashClosing.setDifference(difference);
        cashClosing.setNotes(notes);
        cashClosing.setIsClosed(true);

        cashClosing = cashClosingRepository.save(cashClosing);
        return CashClosingDto.fromCashClosing(cashClosing);
    }

    /**
     * Get today's cash closing
     */
    public CashClosingDto getTodaysCashClosing() {
        LocalDate today = LocalDate.now();
        return cashClosingRepository.findLatestByDate(today)
                .map(CashClosingDto::fromCashClosing)
                .orElse(null);
    }

    /**
     * Get all closed cash closings
     */
    public List<CashClosingDto> getAllClosedCashClosings() {
        return cashClosingRepository.findByIsClosedTrue().stream()
                .map(CashClosingDto::fromCashClosing)
                .collect(Collectors.toList());
    }

    /**
     * Get cash closing by date
     */
    public CashClosingDto getCashClosingByDate(LocalDate date) {
        return cashClosingRepository.findLatestByDate(date)
                .map(CashClosingDto::fromCashClosing)
                .orElse(null);
    }

    /**
     * Get cash closings within a date range
     */
    public List<CashClosingDto> getCashClosingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return cashClosingRepository.findByDateBetween(startDate, endDate).stream()
                .map(CashClosingDto::fromCashClosing)
                .collect(Collectors.toList());
    }

    /**
     * Delete cash closing by id
     */
    @Transactional
    public void deleteCashClosing(Integer id) {
        CashClosing cashClosing = cashClosingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cash closing not found with id: " + id));
        cashClosingRepository.delete(cashClosing);
    }
}

