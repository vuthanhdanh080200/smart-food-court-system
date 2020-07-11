package com.example.smart_food_court_system;

import android.content.Context;
import android.view.View;

import com.example.smart_food_court_system.model.Mail;

public class Utility {

    public Utility() {
    }

    public void sendEmail(Context mContext, Mail mail){
        JavaMailAPI javaMailAPI = new JavaMailAPI(mContext, mail.getMail(), mail.getSubject(), mail.getMessage());
        javaMailAPI.execute();
    }
}
