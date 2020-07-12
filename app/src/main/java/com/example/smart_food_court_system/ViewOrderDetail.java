package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewOrderDetail extends AppCompatActivity {

    TextView txtUserName, txtTotal, txtStatus;
    Button btnNextStage,btnBackStage;
    com.example.smart_food_court_system.model.Order order=new com.example.smart_food_court_system.model.Order();
    DatabaseReference mDatabase, db;
    String orderId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtUserName=(TextView)findViewById(R.id.txtUserName);
        txtTotal=(TextView)findViewById(R.id.txtTotalPrice);
        txtStatus=(TextView)findViewById(R.id.txtStatus);
        btnNextStage=(Button)findViewById(R.id.btnNextStage);
        btnBackStage=(Button)findViewById(R.id.btnBackStage);
        if(getIntent() != null){
            orderId = getIntent().getStringExtra("ID");
        }
        if(!orderId.isEmpty()){
            getDetailOrder(orderId);
        }
        btnBackStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db=FirebaseDatabase.getInstance().getReference("Hieu/Order");
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        order=dataSnapshot.child(orderId).getValue(com.example.smart_food_court_system.model.Order.class);
                        if(order.getStatus().equals("ready")){
                            Toast.makeText(ViewOrderDetail.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                        else if(order.getStatus().equals("cook")&& Common.currentUser.getRole().equals("cook")){
                            db.child(orderId).child("status").setValue("ready");
                            Toast.makeText(ViewOrderDetail.this, "Successful", Toast.LENGTH_SHORT).show();
                        }
                        else if(order.getStatus().equals("cook done") && Common.currentUser.getRole().equals("cook")){
                            db.child(orderId).child("status").setValue("cook");
                            Toast.makeText(ViewOrderDetail.this, "Successful", Toast.LENGTH_SHORT).show();
                        }
                        else if(order.getStatus().equals("complete")&&Common.currentUser.getRole().equals("waitor")){
                            db.child(orderId).child("status").setValue("cook done");
                            Toast.makeText(ViewOrderDetail.this, "Successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ViewOrderDetail.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btnNextStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db=FirebaseDatabase.getInstance().getReference("Hieu/Order");
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        order=dataSnapshot.child(orderId).getValue(com.example.smart_food_court_system.model.Order.class);
                        if(order.getStatus().equals("ready")&&Common.currentUser.getRole().equals("cook")){
                            db.child(orderId).child("status").setValue("cook");
                            Toast.makeText(ViewOrderDetail.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                        else if(order.getStatus().equals("cook")&& Common.currentUser.getRole().equals("cook")){
                            db.child(orderId).child("status").setValue("cook done");
                            Toast.makeText(ViewOrderDetail.this, "Successful", Toast.LENGTH_SHORT).show();
                        }
                        else if(order.getStatus().equals("cook done") && Common.currentUser.getRole().equals("waitor")){
                            db.child(orderId).child("status").setValue("complete");
                            Toast.makeText(ViewOrderDetail.this, "Successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(ViewOrderDetail.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }


    private void getDetailOrder(final String OrderId) {
        mDatabase.child("Hieu").child("Order").child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                com.example.smart_food_court_system.model.Order order_detail = dataSnapshot.getValue(com.example.smart_food_court_system.model.Order.class);
                txtStatus.setText("Status "+ order_detail.getStatus());
                txtUserName.setText("Name " + order_detail.getUserName());
                txtTotal.setText("Price " + order_detail.getTotal());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}