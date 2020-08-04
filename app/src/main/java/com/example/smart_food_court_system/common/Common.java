package com.example.smart_food_court_system.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smart_food_court_system.model.User;

public class Common {
    public static String power="bat";
    public static User currentUser;
    public static User admin = new User("Admin", "Admin", "1234", "Admin@gmail.com", "1234", "Customer", "0", "0");
    public static String userName; // Forgot password: store username
    public static String phoneNumber; // Forgot password: store phone number
    public static final String USER_KEY = "User"; // Remember me user
    public static final String PWD_KEY = "Password"; // Remember me password
    public static final String EMAIL = "smartfoodcourthcmut@gmail.com";  //Host Email
    public static final String PASSWORD = "smartfoodcourthcmut123456789"; //Host Password
    public static final String textSubEmailConfirmOTPPasswordMessage = "Xác Nhận Mã OTP";
    public static final String addFoodSuccessMessage = "Add Food Success";
    public static final String changeFoodSuccessMessage = "Change Food Success";
    public static final String remainingFoodErrorMessage = "Insufficient remaining food";
    public static final String fillAllErrorMessage = "Please Fill In All Information!";
    public static final String changePasswordSuccessMessage = "Change Password Success";
    public static final String confirmPasswordErrorMessage = "Please make sure your passwords match";
    public static final String currentPasswordIsNotCorrectErrorMessage = "Current Password Is Not Correct";
    public static final String emailAddressExistsErrorMessage = "This email address has been used!";
    public static final String phoneNumberExistsErrorMessage = "This phone number has been used!";
    public static final String updateInforSuccessMessage = "Update information success";
    public static final String signInSuccessMessage = "Sign in successfully!";
    public static final String signUpSuccessMessage = "Sign up successfully!";

    public static final String merchantName = "smartfoodcourthcmut";
    public static final String merchantCode = "MOMOO1KC20200802";
    public static final String merchantNameLabel = "Provider";

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null){
                for(int i=0; i<info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
