package com.javaoops.campuscycle.model;

public class User {
    private String userId;
    private String name;
    private String universityId;
    private String email;
    private boolean isVerified;

    public User(String userId, String name, String universityId, String email) {
        this.userId = userId;
        this.name = name;
        this.universityId = universityId;
        this.email = email;
        this.isVerified = false;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUniversityId() { return universityId; }
    public void setUniversityId(String universityId) { this.universityId = universityId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public void login() {
        // Implementation for login logic if needed
    }

    public void logout() {
        // Implementation for logout logic if needed
    }

    public String getProfile() {
        return "Name: " + name + "\nID: " + universityId + "\nEmail: " + email;
    }
}
