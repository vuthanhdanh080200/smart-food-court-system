package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Recharge extends AppCompatActivity {
    EditText edtRechargeAmount;
    Button btnRecharge;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        edtRechargeAmount = (EditText)findViewById(R.id.edtRechargeAmount);
        btnRecharge = (Button)findViewById(R.id.btnRecharge);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Duy");

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDiaglog = new ProgressDialog(Recharge.this);
                mDiaglog.setMessage("Please wait");
                mDiaglog.show();
                final String rechargeAmount = edtRechargeAmount.getText().toString();
                if(rechargeAmount.isEmpty()){
                    mDiaglog.dismiss();
                    Toast.makeText(Recharge.this, "You haven't type recharge amount", Toast.LENGTH_SHORT).show();
                }
                else if(rechargeAmount.equals("0")){
                    mDiaglog.dismiss();
                    Toast.makeText(Recharge.this, "Not a proper amount", Toast.LENGTH_SHORT).show();
                }
                else{
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String currentBalance = dataSnapshot.child("User")
                                    .child(Common.currentUser.getUserName())
                                    .child("accountBalance").getValue().toString();
                            String newBalance = String.valueOf(Integer.parseInt(currentBalance) + Integer.parseInt(rechargeAmount));
                            mDatabase.child("User")
                                    .child(Common.currentUser.getUserName())
                                    .child("accountBalance").setValue(newBalance);
                            Common.currentUser.setAccountBalance(newBalance);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    mDiaglog.dismiss();
                    Toast.makeText(Recharge.this, "Recharge successfully", Toast.LENGTH_SHORT).show();
                    Intent RechargeToHome = new Intent(Recharge.this, Home.class);
                    startActivity(RechargeToHome);
                }

            }
        });

    }
}