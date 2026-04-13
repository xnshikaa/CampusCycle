package com.javaoops.campuscycle.model;

public abstract class User {
    private String userId;
    private String name;
    private String universityId;
    private String email;

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUniversityId() {
        return universityId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUniversityId(String universityId) {
        this.universityId = universityId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String role;
    private boolean isVerified;

    public User(String userId, String name, String universityId, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.universityId = universityId;
        this.email = email;
        this.role = role;
        this.isVerified = false;
    }

    public abstract void login();
    public abstract void logout();
    public abstract String getProfile();
}
