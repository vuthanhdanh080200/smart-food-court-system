package com.example.smart_food_court_system.model;

// TO DO
// Khoi tao class, bien,  va cac phuong thuc get, set cho cac truong sau
// Name: string
// EmailAddress: string
// UserName: string
// Password: string
// PhoneNumber: string

public class User {
    private String Name;
    private String UserName;
    private String Password;
    private String EmailAddress;
    private String PhoneNumber;
    private String Role;
    private String AccountBalance;
    private String Stall;

    public User() {
    }

    public User(String name, String userName, String password, String emailAddress, String phoneNumber, String role, String accountBalance, String stall) {
        Name = name;
        UserName = userName;
        Password = password;
        EmailAddress = emailAddress;
        PhoneNumber = phoneNumber;
        Role = role;
        AccountBalance = accountBalance;
        Stall = stall;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getAccountBalance() {
        return AccountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        AccountBalance = accountBalance;
    }

    public String getStall() {
        return Stall;
    }

    public void setStall(String stall) {
        Stall = stall;
    }


}
