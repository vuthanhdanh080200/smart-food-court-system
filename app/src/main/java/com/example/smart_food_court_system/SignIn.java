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
    EditText edtUserName, edtPassword, edtPhoneNumber;
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
        final DatabaseReference table_user = database.getReference("Danh/User");

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (Common.isConnectedToInternet(getBaseContext())) {

                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //NEW: Check if all of the fields have been filled yet.
                            if (edtUserName.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "Please fill in all information.", Toast.LENGTH_SHORT).show();
                            } else {
                                //Check if user not exist in database
                                if (dataSnapshot.child(edtUserName.getText().toString()).exists()) {
                                    //Save user and password - Remember me
                                    if(ckbRememberMe.isChecked()){
                                        Paper.book().write(Common.USER_KEY, edtUserName.getText().toString());
                                        Paper.book().write(Common.PWD_KEY, edtPassword.getText().toString());
                                    }
                                    //Get User information
                                    mDialog.dismiss();
                                    User user = dataSnapshot.child(edtUserName.getText().toString()).getValue(User.class);
                                    if (user.getPassword().equals(edtPassword.getText().toString())) {
                                        if (user.getRole().equals("customer")) {
                                            if (Common.currentUser == null) {
                                                Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                            Intent home = new Intent(SignIn.this, Home.class);
                                            startActivity(home);
                                        } else if (user.getRole().equals("it staff")) {
                                            if (Common.currentUser == null) {
                                                Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                            Intent test = new Intent(SignIn.this, Test.class);
                                            startActivity(test);
                                        }
                                        // Xoa dong duoi nay di khi commit
                                        else if (user.getRole().equals("cook")) {
                                            if (Common.currentUser == null) {
                                                Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                            Intent order = new Intent(SignIn.this, Order.class);
                                            startActivity(order);
                                        }
                                        // Xoa dong tren nay di khi commit
                                        else {
                                            if (Common.currentUser == null) {
                                                Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        Common.currentUser = user;
                                    } else {
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
        edtPhoneNumber = (EditText) builder.findViewById(R.id.edtPhoneNumber);
        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("Danh/User");

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.userName = edtUserName.getText().toString();
                        Common.phoneNumber = edtPhoneNumber.getText().toString();
                        if(Common.userName.isEmpty() || Common.phoneNumber.isEmpty()){
                            Toast.makeText(SignIn.this, "Please fill in all information!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (!dataSnapshot.child(Common.userName).exists()) {
                                Toast.makeText(SignIn.this, "Username " + Common.userName + " doesn't exist, please choose another username.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                User user = dataSnapshot.child(Common.userName).getValue(User.class);
                                if(!user.getPhoneNumber().equals(Common.phoneNumber)){
                                    Toast.makeText(SignIn.this, "Phone number of " + Common.userName + " incorrect, please choose another phone number.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Intent test = new Intent(SignIn.this, Test.class);
                                    test.putExtra("phoneNumber", Common.phoneNumber);
                                    startActivity(test);
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

}