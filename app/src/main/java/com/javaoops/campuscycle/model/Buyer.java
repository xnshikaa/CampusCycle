package com.javaoops.campuscycle.model;

public class Buyer extends User {

    public Buyer(String userId, String name, String universityId, String email) {
        super(userId, name, universityId, email, "buyer");
    }

    @Override
    public void login() {
        System.out.println("Buyer logged in: " + getName());
    }

    @Override
    public void logout() {
        System.out.println("Buyer logged out: " + getName());
    }

    @Override
    public String getProfile() {
        return "Buyer Profile: " + getName() + getEmail() + getUniversityId();
    }
}

