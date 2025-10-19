package com.foodie.application.service;

import com.foodie.application.domain.Order;
import com.foodie.application.domain.Payment;
import com.foodie.application.domain.PaymentMethod;
import com.foodie.application.domain.PaymentStatus;
import com.foodie.application.helper.OrderHelper;
import com.foodie.application.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment createPayment(Order order, String paymentMethod){
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentAmount(OrderHelper.calculateTotal(order));
        payment.setPaymentStatus(PaymentStatus.PENDING);
        try {
            PaymentMethod pMethod = PaymentMethod.valueOf(paymentMethod.toUpperCase());
            payment.setPaymentMethod(pMethod);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method: " + paymentMethod);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public void deletePayment(Integer paymentID) {
        Payment payment = paymentRepository
                .findById(paymentID)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        paymentRepository.delete(payment);
    }

    @Transactional
    public void processPayment(Integer paymentID, String methodString){
        Payment payment = paymentRepository
                .findById(paymentID)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        if (PaymentStatus.COMPLETED.equals(payment.getPaymentStatus())) {
            throw new IllegalStateException("Payment already completed");
        }

        try {
            PaymentMethod pMethod = PaymentMethod.valueOf(methodString.toUpperCase());
            payment.setPaymentMethod(pMethod);
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method: " + methodString);
        }
    }

    @Transactional
    public void refundPayment(Integer paymentID){
        Payment payment = paymentRepository
                .findById(paymentID)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        if (!PaymentStatus.COMPLETED.equals(payment.getPaymentStatus())) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }
        payment.setPaymentStatus(PaymentStatus.REFUNDED);
    }

    @Transactional
    public void cancelPayment(Integer paymentID){
        Payment payment = paymentRepository
                .findById(paymentID)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        if (PaymentStatus.COMPLETED.equals(payment.getPaymentStatus())) {
            throw new IllegalStateException("Completed payments cannot be cancelled");
        }
        payment.setPaymentStatus(PaymentStatus.CANCELLED);
    }

    @Transactional
    public void updatePaymentMethod(Integer paymentID, String newMethod){
        Payment payment = paymentRepository
                .findById(paymentID)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        try {
            PaymentMethod pMethod = PaymentMethod.valueOf(newMethod.toUpperCase());
            payment.setPaymentMethod(pMethod);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method: " + newMethod);
        }
    }

    @Transactional
    public void markPaymentAsFailed(Integer paymentID) {
        Payment payment = paymentRepository
                .findById(paymentID)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING)) {
            throw new IllegalStateException("Only pending payments can be marked as failed");
        }
        payment.setPaymentStatus(PaymentStatus.FAILED);
    }

}
