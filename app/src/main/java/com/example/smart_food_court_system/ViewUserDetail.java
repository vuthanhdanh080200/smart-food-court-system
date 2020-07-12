package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.UserHandle;
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

public class ViewUserDetail extends AppCompatActivity {

    TextView txtName, txtUserName, txtPassword,txtEmailAdress,txtPhoneNumber,txtRole,txtAccountBalance,txtStall;
    Button btnChangeRole;
    User user=new User();
    DatabaseReference mDatabase, db;
    String username="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtName = (TextView) findViewById(R.id.txtName);
        txtPassword = (TextView) findViewById(R.id.txtPassWord);
        txtEmailAdress = (TextView) findViewById(R.id.txtEmailAdress);
        txtPhoneNumber = (TextView) findViewById(R.id.txtPhoneNumber);
        txtRole = (TextView) findViewById(R.id.txtRole);
        txtAccountBalance = (TextView) findViewById(R.id.txtAcountBalance);
        txtStall = (TextView) findViewById(R.id.txtStall);
        btnChangeRole=(Button)findViewById(R.id.btnChangeRole);

        if (getIntent() != null) {
            username = getIntent().getStringExtra("UserName");
        }
        if (!username.isEmpty()) {
            getDetailUser(username);
        }
        btnChangeRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db=FirebaseDatabase.getInstance().getReference("Hieu/User");
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user=dataSnapshot.child(username).getValue(User.class);
                        if(user.getRole().equals("customer")){
                            db.child(username).child("role").setValue("waitor");
                            Toast.makeText(ViewUserDetail.this, "The role has been changed to waitor ", Toast.LENGTH_SHORT).show();
                        }
                        else if(user.getRole().equals("waitor")){
                            db.child(username).child("role").setValue("cook");
                            Toast.makeText(ViewUserDetail.this, "The role has been changed to cook ", Toast.LENGTH_SHORT).show();
                        }
                        else if(user.getRole().equals("cook")){
                            db.child(username).child("role").setValue("vendor owner");
                            Toast.makeText(ViewUserDetail.this, "The role has been changed to vendor owner ", Toast.LENGTH_SHORT).show();
                        }
                        else if(user.getRole().equals("vendor owner")){
                            db.child(username).child("role").setValue("manager");
                            Toast.makeText(ViewUserDetail.this, "The role has been changed to manager ", Toast.LENGTH_SHORT).show();
                        }
                        else if(user.getRole().equals("manager")){
                            db.child(username).child("role").setValue("itstaff");
                            Toast.makeText(ViewUserDetail.this, "The role has been changed to itstaff ", Toast.LENGTH_SHORT).show();
                        }
                        else if(user.getRole().equals("itstaff")){
                            db.child(username).child("role").setValue("customer");
                            Toast.makeText(ViewUserDetail.this, "The role has been changed to customer ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



    }



    private void getDetailUser(final String username) {
        mDatabase.child("Hieu").child("User").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user_detail = dataSnapshot.getValue(User.class);
                txtUserName.setText("User name: "+ user_detail.getUserName());
                txtName.setText("Name: "+ user_detail.getName());
                txtPassword.setText("Password: "+ user_detail.getPassword());
                txtEmailAdress.setText("EmailAddress: "+ user_detail.getEmailAddress());
                txtPhoneNumber.setText("Phone Number: "+ user_detail.getPhoneNumber());
                txtRole.setText("Role: "+ user_detail.getRole());
                txtAccountBalance.setText("Account Balance: "+ user_detail.getAccountBlance());
                txtStall.setText("Stall: "+ user_detail.getStall());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}