package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        mDatabase = FirebaseDatabase.getInstance().getReference("Danh/User");

       btnSignUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

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

                           if (user.getName().isEmpty() || user.getUserName().isEmpty() || user.getPassword().isEmpty() ||
                                   user.getEmailAddress().isEmpty() || user.getPhoneNumber().isEmpty()) {
                               Toast.makeText(SignUp.this, "Please fill in all information!", Toast.LENGTH_SHORT).show();
                           } else {
                               if (dataSnapshot.child(user.getUserName()).exists()) {
                                   Toast.makeText(SignUp.this, "Username " + user.getUserName() + " exists, please choose another username.", Toast.LENGTH_SHORT).show();
                               } else {
                                   boolean isEmailAddressExists = false, isPhoneNumberExists = false;
                                   for (DataSnapshot item : dataSnapshot.getChildren()) {
                                       if (user.getEmailAddress().equals(item.child("emailAddress").getValue())) {
                                           isEmailAddressExists = true;
                                           break;
                                       }
                                   }
                                   for (DataSnapshot item : dataSnapshot.getChildren()) {
                                       if (user.getPhoneNumber().equals(item.child("phoneNumber").getValue())) {
                                           isPhoneNumberExists = true;
                                           break;
                                       }
                                   }
                                   if (isEmailAddressExists || isPhoneNumberExists) {
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
                                       mDatabase.child(edtUserName.getText().toString()).setValue(user);
                                       Intent signIn = new Intent(SignUp.this, SignIn.class);
                                       startActivity(signIn);
                                       Toast.makeText(SignUp.this, "Sign Up successfully!", Toast.LENGTH_SHORT).show();
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