package com.javaoops.campuscycle.util;

public interface Payable {
    boolean processPayment(double amount);
    boolean verifyAccount();
    boolean checkVerification();
}
