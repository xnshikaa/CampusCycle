package com.javaoops.campuscycle.model;

public class Seller extends User{
    super(userId, name, universityId, email, "seller");

    private boolean isPaymentVerified;
    private String paymentAccountId;


    @Override
    public void login() {
        System.out.println("Seller logged in: " + getName());
    }

    @Override
    public void logout() {
        System.out.println("Seller logged out: " + getName());
    }

    @Override
    public String getProfile() {
        return "Seller Profile: " + getName() + getEmail() + getUniversityId();
    }
}
