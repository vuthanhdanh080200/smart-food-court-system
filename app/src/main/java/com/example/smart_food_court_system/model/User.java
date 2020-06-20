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

    public User() {
    }

    public User(String name, String userName, String password, String emailAddress, String phoneNumber) {
        Name = name;
        UserName = userName;
        Password = password;
        EmailAddress = emailAddress;
        PhoneNumber = phoneNumber;
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
}
