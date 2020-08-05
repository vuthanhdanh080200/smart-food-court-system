package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassword extends AppCompatActivity {
    EditText edtNewPassword, edtConfirmNewPassword;
    Button btnChangePassword, btnCancel;
    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtNewPassword = (EditText)findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = (EditText)findViewById(R.id.edtConfirmNewPassword);
        btnChangePassword = (Button)findViewById(R.id.dialog_change);
        btnCancel = (Button)findViewById(R.id.dialog_cancel);

        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("Duy/User");
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtNewPassword.getText().toString().isEmpty() || edtConfirmNewPassword.getText().toString().isEmpty()){
                    Toast.makeText(ForgotPassword.this, Common.fillAllErrorMessage, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (edtNewPassword.getText().toString().equals(edtConfirmNewPassword.getText().toString())) {
                        Toast.makeText(ForgotPassword.this, Common.changePasswordSuccessMessage, Toast.LENGTH_SHORT).show();
                        Intent signIn = new Intent(ForgotPassword.this, MainActivity.class);
                        startActivity(signIn);
                        mDatabase.child(Common.userName).child("password").setValue(edtNewPassword.getText().toString());
                        Common.userName = "";
                        Common.phoneNumber = "";
                    } else {
                        Toast.makeText(ForgotPassword.this, Common.confirmPasswordErrorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn = new Intent(ForgotPassword.this, MainActivity.class);
                startActivity(signIn);
            }
        });
    }
}