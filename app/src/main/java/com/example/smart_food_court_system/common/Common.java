package com.example.smart_food_court_system.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smart_food_court_system.model.User;

public class Common {
    public static User currentUser;
    public static User admin = new User("Admin", "Admin", "1234", "Admin@gmail.com", "1234", "Customer", "0", "0");
    public static String userName; // Forgot password: store username
    public static String emailAddress; // Forgot password: store email address
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
    public static final String publicKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAh+fBkzLrYk7dMySeASH3U6Z8+sANdmYbESOzvoaMIJ4RsvE94HJY8mw6fcoq5NddSIcdJa2RabDCBaYvkljiW5064wFW9xRhUhT9lM/JI8w9WG01499qfn1+m1hAOx8CNGJuz91r/kQL7S+xCTc+s+mO0EwLWRqUiFVQZtPAXdR/wg1UTHuY1zOmoD0dWq5yO562tO2fOLKIlYAe5zC0+J4yypGODLN0FJ5OdGH99WNCHpYzJE1PgIOOKnadS04ql0wywfZtQTSp8mex/qAazwVZYNPLO5NtwV5ReSXDCNei+w0zdi3cui7l7KWbWuy9luwru32cKG5N6R9KKU+j5/X3g86HviCmasPx/azQXyyUl2MmAumjRRDkPv+iBIUjvc6+L7iUJNmOeclpOySSKP43K6oFyx+8KbCJNu2Y0xR2xCYrfWIqGsahO5HIm0VzER2k1kxxxlMvcjt9Yu6eqm1YykA1/zUc9zcaYG/EL3sypHdc1yJp6jBNC2wTWkcE+MpmGAH8B+uKOw8hNvoV1i5p18AFBPKxBNdus01cxKdg5lkRddqAtj2vMuR/UYUJhmB76wjc0hVSp/AvZP9nh2bwT7IMzzaOmzH98QvLyppJw4hTT0F2rJpdNqY4KG4y7EKBbc4W1CQGSDksgORRhsQ/26GMaITAXu26gvCkkN8CAwEAAQ==";

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
