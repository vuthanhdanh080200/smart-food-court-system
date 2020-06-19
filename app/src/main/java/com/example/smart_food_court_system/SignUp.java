package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smart_food_court_system.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

//TO DO
//Kiem tra User name co trong database chua, neu co roi thi thong bao su dung user name khac
//Them cac truong khac co trong User class
//Password hien ****

public class SignUp extends AppCompatActivity {
    EditText edtUserName, edtName, edtPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtUserName = (MaterialEditText)findViewById(R.id.edtUserName);
        edtName = (MaterialEditText)findViewById(R.id.edtName);
        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);


        //Init Firebase

        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference("User");

       btnSignUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mDatabase.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       //Get User information

                       User user = new User(edtName.getText().toString(), edtPassword.getText().toString());

                       mDatabase.child(edtUserName.getText().toString()).setValue(user);
                       Toast.makeText(SignUp.this, "Sign Up successfully !", Toast.LENGTH_SHORT).show();
                       Intent signIn = new Intent(SignUp.this, SignIn.class);
                       startActivity(signIn);

                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
           }
       });

    }



}