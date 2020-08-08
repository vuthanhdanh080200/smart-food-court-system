package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.common.Text;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OTPVerification extends AppCompatActivity {
    String mail, startActivity;
    String currentOTP;
    EditText edtTextCode;
    Button btnTextCode;
    TextView txtOTPExpiresTime;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_verification);
        edtTextCode = (EditText)findViewById(R.id.edtTextCode);
        btnTextCode = (Button)findViewById(R.id.btnTextCode);
        txtOTPExpiresTime = (TextView)findViewById(R.id.txtOTPExpiresTime);
        if(getIntent() != null){
            mail = getIntent().getStringExtra("Email Address");
            Log.e("Mail", mail);
            startActivity = getIntent().getStringExtra("Start Activity");
            Log.e("Activity", startActivity);
        }
        if(!mail.isEmpty()){
            sendMail(mail);

        }
        btnTextCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentOTP.equals(edtTextCode.getText().toString())){
                    if(startActivity.equals("Forgot Password")) {
                        Intent intent = new Intent(OTPVerification.this, ForgotPassword.class);
                        startActivity(intent);
                    }
                    else{
                        mDatabase = FirebaseDatabase.getInstance().getReference("Duy/User");
                        mDatabase.child(Common.currentUser.getUserName()).setValue(Common.currentUser);
                        Toast.makeText(OTPVerification.this, "Sign Up successfully!", Toast.LENGTH_SHORT).show();
                        Intent signIn = new Intent(OTPVerification.this, MainActivity.class);
                        startActivity(signIn);
                    }
                }
                else{
                    Toast.makeText(OTPVerification.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
        new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtOTPExpiresTime.setText("OTP expires in " + millisUntilFinished/1000 + " s ");
            }

            public void onFinish() {
                Intent intent = new Intent(OTPVerification.this, MainActivity.class);
                startActivity(intent);
            }
        }.start();

    }

    private void sendMail(String mail) {
        String subject = Text.textSubEmailConfirmOTPPassword;
        currentOTP = generateOTP();
        String message = "HỆ THỐNG QUẢN LÝ SMART FOOD COUR SYSTEM \n\n"
        + "Đây là OTP của bạn: " + currentOTP + "\n\nTrân trọng! \nCảm ơn";
        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);
        javaMailAPI.execute();

    }

    public static String generateOTP()
    {  //int randomPin declared to store the otp
        //since we using Math.random() hence we have to type cast it int
        //because Math.random() returns decimal value
        int randomPin   =(int) (Math.random()*9000)+1000;
        String otp  = String.valueOf(randomPin);
        return otp; //returning value of otp
    }


}