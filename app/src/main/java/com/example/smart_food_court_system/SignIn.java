package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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


public class SignIn extends AppCompatActivity {
    EditText edtUserName, edtPassword;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtUserName = (EditText)findViewById(R.id.edtUserName);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        btnSignIn = (Button)findViewById(R.id.btnSignIn);

        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Danh/User");

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //NEW: Check if all of the fields have been filled yet.
                        if(edtUserName.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()){
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "Please fill in all information.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Check if user not exist in database
                            if (dataSnapshot.child(edtUserName.getText().toString()).exists()) {
                                //Get User information
                                mDialog.dismiss();
                                User user = dataSnapshot.child(edtUserName.getText().toString()).getValue(User.class);
                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                    if (user.getRole().equals("it staff")) {
                                        Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                        Intent itstaffhome = new Intent(SignIn.this, ItStaffHome.class);
                                        startActivity(itstaffhome);
                                    }
                                    else if(Common.power.equals("tat")){
                                        Intent turnoff = new Intent(SignIn.this, TurnOffSystem.class);
                                        startActivity(turnoff);
                                    }
                                    else if(user.getRole().equals("customer")) {
                                        Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                        Intent home = new Intent(SignIn.this, Home.class);
                                        startActivity(home);
                                    }
                                    else if (user.getRole().equals("cook")||user.getRole().equals("waitor")){
                                        Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                        Intent order = new Intent(SignIn.this, Order.class);
                                        startActivity(order);
                                    }
                                    else if(user.getRole().equals("manager")){
                                        Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                        Intent homemanager = new Intent(SignIn.this, HomeManager.class);
                                        startActivity(homemanager);
                                    }
                                    else{
                                        Toast.makeText(SignIn.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();

                                    }
                                    Common.currentUser = user;
                                } else {
                                    Toast.makeText(SignIn.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
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
        });

    }


}