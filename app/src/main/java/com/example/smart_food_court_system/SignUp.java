package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//Duy DO
//Kiem tra User name co trong database chua, neu co roi thi thong bao su dung user name khac
//Them cac truong khac co trong User class
//Password hien ****

public class SignUp extends AppCompatActivity {
    EditText edtName, edtUserName, edtPassword, edtEmailAddress, edtPhoneNumber;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (EditText)findViewById(R.id.edtName);
        edtUserName = (EditText)findViewById(R.id.edtUserName);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtEmailAddress = (EditText)findViewById(R.id.edtEmailAddress);
        edtPhoneNumber = (EditText)findViewById(R.id.edtPhoneNumber);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        //Init Firebase

        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("Duy");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();

                if (Common.isConnectedToInternet(getBaseContext())) {

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get User information
                            User user = new User(
                                    edtName.getText().toString(),
                                    edtUserName.getText().toString(),
                                    edtPassword.getText().toString(),
                                    edtEmailAddress.getText().toString(),
                                    edtPhoneNumber.getText().toString(),
                                    "customer",
                                    "0",
                                    "0");
                            Common.currentUser = user;
                            if (user.getName().isEmpty() || user.getUserName().isEmpty() || user.getPassword().isEmpty() ||
                                    user.getEmailAddress().isEmpty() || user.getPhoneNumber().isEmpty()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Please fill in all information!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (dataSnapshot.child("User").child(user.getUserName()).exists()) {
                                    mDialog.dismiss();
                                    Toast.makeText(SignUp.this, "Username " + user.getUserName() + " exists, please choose another username.", Toast.LENGTH_SHORT).show();
                                } else {
                                    boolean isEmailAddressExists = false, isPhoneNumberExists = false;
                                    for (DataSnapshot item : dataSnapshot.child("User").getChildren()) {
                                        if (user.getEmailAddress().equals(item.child("emailAddress").getValue())) {
                                            isEmailAddressExists = true;
                                            break;
                                        }
                                    }
                                    for (DataSnapshot item : dataSnapshot.child("User").getChildren()) {
                                        if (user.getPhoneNumber().equals(item.child("phoneNumber").getValue())) {
                                            isPhoneNumberExists = true;
                                            break;
                                        }
                                    }
                                    if (isEmailAddressExists || isPhoneNumberExists) {
                                        mDialog.dismiss();
                                        if (!isPhoneNumberExists) {
                                            Toast.makeText(SignUp.this, "This email address has been used!", Toast.LENGTH_SHORT).show();
                                        } else if (!isEmailAddressExists) {
                                            Toast.makeText(SignUp.this, "This phone number has been used!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SignUp.this, "This email address and phone number has been used!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //Add to Database
                                    else {
                                        Intent intent = new Intent(SignUp.this, OTPVerification.class);
                                        intent.putExtra("Email Address", edtEmailAddress.getText().toString());
                                        intent.putExtra("Start Activity", "Main Activity");
                                        startActivity(intent);
                                    }
                                }
                            }

                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                else{
                    Toast.makeText(SignUp.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

}