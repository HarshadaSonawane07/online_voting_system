package com.onlie.voting.onlinevotingsystem;

public class UserDetails {
    private String fullName;
    private String aadharNumber;
    private String voterId;

    public UserDetails() {
        // Default constructor required for Firebase
    }

    public UserDetails(String fullName, String aadharNumber, String voterId) {
        this.fullName = fullName;
        this.aadharNumber = aadharNumber;
        this.voterId = voterId;
    }

    // Getters and setters for fields (fullName, aadharNumber, voterId)

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }
}
