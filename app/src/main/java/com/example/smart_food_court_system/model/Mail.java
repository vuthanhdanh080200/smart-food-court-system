package com.example.smart_food_court_system.model;

public class Mail {
    private String mail;
    private String message;
    private String subject;

    public Mail() {
    }

    public Mail(String mail, String message, String subject) {
        this.mail = mail;
        this.message = message;
        this.subject = subject;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
