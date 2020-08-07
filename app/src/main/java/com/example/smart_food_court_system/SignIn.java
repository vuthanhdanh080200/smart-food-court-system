package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;


public class SignIn extends AppCompatActivity {
    EditText edtUserName, edtPassword, edtEmailAddress;
    Button btnSignIn, btnChangePassword, btnCancel;
    CheckBox ckbRememberMe;
    TextView txtForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtUserName = (EditText)findViewById(R.id.edtUserName);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        ckbRememberMe = (CheckBox)findViewById(R.id.ckbRememberMe);
        txtForgotPassword = (TextView)findViewById(R.id.txtForgotPassword);
        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Duy");

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();

                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //NEW: Check if all of the fields have been filled yet.
                            if (edtUserName.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "Please fill in all information.", Toast.LENGTH_SHORT).show();
                            } else {
                                //Check if user not exist in database
                                if (dataSnapshot.child("User").child(edtUserName.getText().toString()).exists()) {
                                    //Save user and password - Remember me
                                    if(ckbRememberMe.isChecked()){
                                        Paper.book().write(Common.USER_KEY, edtUserName.getText().toString());
                                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                                    }
                                    //Get User information
                                    mDialog.dismiss();
                                    User user = dataSnapshot.child("User").child(edtUserName.getText().toString()).getValue(User.class);
                                    if (user.getPassword().equals(edtPassword.getText().toString())) {
                                        String powerMode = dataSnapshot.child("PowerMode").getValue(String.class);
                                        if (user.getRole().equals("it staff")) {
                                            Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                            Intent itStaffHome = new Intent(SignIn.this, ItStaffHome.class);
                                            Common.currentUser = user;
                                            startActivity(itStaffHome);
                                            finish();
                                        }
                                        else if(powerMode.equals("maintenance")) {
                                            Intent turnoff = new Intent(SignIn.this, TurnOffSystem.class);
                                            startActivity(turnoff);
                                            finish();
                                        }
                                        else if (user.getRole().equals("customer")) {
                                            if (Common.currentUser == null) {
                                                Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                            Intent home = new Intent(SignIn.this, Home.class);
                                            startActivity(home);
                                            finish();
                                        }
                                        else if (user.getRole().equals("cook")){
                                            Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignIn.this, HomeCook.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else if (user.getRole().equals("waitor")){
                                            Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignIn.this, HomeCook.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else if(user.getRole().equals("manager")){
                                            Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                            Intent homeManager = new Intent(SignIn.this, HomeManager.class);
                                            startActivity(homeManager);
                                            finish();
                                        }
                                        Common.currentUser = user;
                                    }
                                    else {
                                        if (Common.currentUser == null) {
                                            Toast.makeText(SignIn.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(SignIn.this, "User not found!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(SignIn.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForgotPassword();
            }
        });
    }

    public void openForgotPassword(){

        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.dialog_user_otp_confirm);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        btnChangePassword = (Button) builder.findViewById(R.id.dialog_go);
        btnCancel = (Button) builder.findViewById(R.id.dialog_cancel);
        edtUserName = (EditText) builder.findViewById(R.id.edtUserName);
        edtEmailAddress = (EditText) builder.findViewById(R.id.edtEmailAddress);
        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("Duy/User");

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.userName = edtUserName.getText().toString();
                        Common.emailAddress = edtEmailAddress.getText().toString();
                        if(Common.userName.isEmpty() || Common.emailAddress.isEmpty()){
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "Please fill in all information!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (!dataSnapshot.child(Common.userName).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "Username " + Common.userName + " doesn't exist, please choose another username.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                User user = dataSnapshot.child(Common.userName).getValue(User.class);
                                if(!user.getEmailAddress().equals(Common.emailAddress)){
                                    mDialog.dismiss();
                                    Toast.makeText(SignIn.this, "Email address of " + Common.userName + " incorrect, please choose another email.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    mDialog.dismiss();
                                    Intent intent = new Intent(SignIn.this, OTPVerification.class);
                                    intent.putExtra("Email Address", Common.emailAddress);
                                    intent.putExtra("Start Activity", "Forgot Password");
                                    startActivity(intent);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }
    @Override
    public void onBackPressed() {

    }

}