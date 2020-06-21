package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewFoodDetail extends AppCompatActivity {
    TextView txtFoodID, txtFoodName, txtFoodPrice, txtOrderQuantity;
    Button btnAddFoodToCart, btnViewFoodInCart;
    DatabaseReference mDatabase, db;
    String foodID = "";
    Order order = new Order();
    public ElegantNumberButton btnOrderQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        txtFoodID = (TextView)findViewById(R.id.txtFoodID);
        txtFoodName = (TextView)findViewById(R.id.txtFoodName);
        txtFoodPrice = (TextView)findViewById(R.id.txtFoodPrice);
        txtOrderQuantity = findViewById(R.id.txtOrderQuantity);
        btnAddFoodToCart = (Button)findViewById(R.id.btnAddFoodToCart);
        btnViewFoodInCart = (Button)findViewById(R.id.btnViewFoodInCart);
        btnOrderQuantity = findViewById(R.id.btnOrderQuantity);


        btnOrderQuantity.setRange(1, 50);

        btnOrderQuantity.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                order.setQuantity(btnOrderQuantity.getNumber());
                txtOrderQuantity.setText("Quantity " + order.getQuantity());
            }
        });

        if(getIntent() != null){
            foodID = getIntent().getStringExtra("FoodID");
        }
        if(!foodID.isEmpty()){
            getDetailFood(foodID);

        }

        btnAddFoodToCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                db = FirebaseDatabase.getInstance().getReference("Cart");
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        db.child(Common.currentUser.getUserName()).child("Order").child(order.getProductName()).setValue(order);
                        Toast.makeText(ViewFoodDetail.this, "Add Food To Cart successfully !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        btnViewFoodInCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent cart = new Intent(ViewFoodDetail.this, Cart.class);
                startActivity(cart);
            }
        });
    }

    private void getDetailFood(final String foodID) {
        mDatabase.child("Food").child(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);
                order.setProductID(food.getFoodID());
                order.setProductName(food.getFoodName());
                order.setPrice(food.getFoodPrice());
                txtFoodID.setText("ID " + food.getFoodID());
                txtFoodName.setText("Name " + food.getFoodName());
                txtFoodPrice.setText("Price " + food.getFoodPrice());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}