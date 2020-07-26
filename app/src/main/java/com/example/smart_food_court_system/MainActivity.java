package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {
    Button btnSignIn, btnSignUp, btnAddFood;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button)findViewById(R.id.SignIn);
        btnSignUp = (Button)findViewById(R.id.SignUp);

        txtSlogan = (TextView)findViewById(R.id.txtSlogan);

        //Init paper
        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });

        //Check remember me
        String userRe = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if(userRe != null && pwd != null){
            if(!userRe.isEmpty() && !pwd.isEmpty()){
                login(userRe, pwd);
            }
        }

    }

    private void login(final String userRe, final String pwd) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Duy/User");
        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please wait...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //NEW: Check if all of the fields have been filled yet.

                        //Check if user not exist in database
                        if (dataSnapshot.child(userRe).exists()) {
                            //Get User information
                            mDialog.dismiss();
                            User user = dataSnapshot.child(userRe).getValue(User.class);
                            if (user.getPassword().equals(pwd)) {
                                if (user.getRole().equals("customer")) {
                                    if (Common.currentUser == null) {
                                        Toast.makeText(MainActivity.this, Common.signInSuccessMessage, Toast.LENGTH_SHORT).show();
                                    }
                                    Intent home = new Intent(MainActivity.this, Home.class);
                                    startActivity(home);
                                } else if (user.getRole().equals("it staff")) {
                                    if (Common.currentUser == null) {
                                        Toast.makeText(MainActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                    Intent test = new Intent(MainActivity.this, Test.class);
                                    startActivity(test);
                                }
                                else if (user.getRole().equals("cook")) {
                                    if (Common.currentUser == null) {
                                        Toast.makeText(MainActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                    Intent intent = new Intent(MainActivity.this, HomeCook.class);
                                    startActivity(intent);
                                }
                                else if (user.getRole().equals("manager")) {
                                    if (Common.currentUser == null) {
                                        Toast.makeText(MainActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                    Intent intent = new Intent(MainActivity.this, HomeManager.class);
                                    startActivity(intent);
                                }
                                else {
                                    if (Common.currentUser == null) {
                                        Toast.makeText(MainActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                Common.currentUser = user;
                            } else {
                                if (Common.currentUser == null) {
                                    Toast.makeText(MainActivity.this, "Wrong password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(MainActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "Automatically login fail, please check your connection!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}