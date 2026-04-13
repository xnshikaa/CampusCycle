package com.javaoops.campuscycle.service;

public class PaymentService {

    public boolean processPayment(double amount) {
        // Simulated payment
        if (amount > 0) {
            return true;
        }
        return false;
    }
}