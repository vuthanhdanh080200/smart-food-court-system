package com.example.smart_food_court_system.model;

// TO DO
// Khoi tao class, bien,  va cac phuong thuc get, set cho cac truong sau
// Name: string
// EmailAddress: string
// UserName: string
// Password: string
// PhoneNumber: string

public class User {
    private String name;
    private String password;


    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
