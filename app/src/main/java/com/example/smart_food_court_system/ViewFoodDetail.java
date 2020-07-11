package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.FoodOrder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewFoodDetail extends AppCompatActivity {
    TextView txtFoodRemaining, txtFoodName, txtFoodPrice, txtOrderQuantity;
    ImageView imageFood;
    Button btnAddFoodToCart, btnViewFoodInCart;
    DatabaseReference mDatabase, db;
    String foodName = "";
    int FoodRemaining = 0;
    FoodOrder foodOrder = new FoodOrder();
    public ElegantNumberButton btnOrderQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food_detail);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        txtFoodRemaining = (TextView)findViewById(R.id.txtFoodRemaining);
        txtFoodName = (TextView)findViewById(R.id.txtFoodName);
        txtFoodPrice = (TextView)findViewById(R.id.txtFoodPrice);
        txtOrderQuantity = findViewById(R.id.txtOrderQuantity);
        btnAddFoodToCart = (Button)findViewById(R.id.btnAddFoodToCart);
        btnViewFoodInCart = (Button)findViewById(R.id.btnViewFoodInCart);
        btnOrderQuantity = findViewById(R.id.btnOrderQuantity);
        imageFood = findViewById(R.id.imageFood);


        btnOrderQuantity.setRange(1, 1000);

        btnOrderQuantity.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(btnOrderQuantity.getNumber());

                if(quantity > FoodRemaining){
                    Toast.makeText(ViewFoodDetail.this, "Insufficient remaining food", Toast.LENGTH_SHORT).show();
                    btnOrderQuantity.setNumber(Integer.toString(quantity - 1));
                }
                else {
                    foodOrder.setQuantity(btnOrderQuantity.getNumber());
                    txtOrderQuantity.setText("Quantity " + foodOrder.getQuantity());
                }
            }
        });

        if(getIntent() != null){
            foodName = getIntent().getStringExtra("FoodName");
        }
        if(!foodName.isEmpty()){
            getDetailFood(foodName);

        }
        final int total[] = {0};
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    total[0] = Integer.parseInt(dataSnapshot.child("Duy/Cart")
                            .child(Common.currentUser.getUserName())
                            .child("total").getValue().toString());
                }
                catch(Exception e){
                    total[0] = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnAddFoodToCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                db = FirebaseDatabase.getInstance().getReference("Duy/Cart")
                        .child(Common.currentUser.getUserName()).child("foodOrderList");


                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final int oldQuantity[] = {0};
                        try {
                            oldQuantity[0] = Integer.parseInt(dataSnapshot.child(foodName).child("quantity").getValue().toString());
                        }
                        catch(Exception e){
                            oldQuantity[0] = 0;
                        }
                        if(btnOrderQuantity.getNumber().equals("0")){
                            Toast.makeText(ViewFoodDetail.this, "Not a proper number", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            int quantity = Integer.parseInt(btnOrderQuantity.getNumber());
                            int price = Integer.parseInt(foodOrder.getPrice());
                            total[0] += (quantity - oldQuantity[0])*price;

                            db.child(foodOrder.getFoodName()).setValue(foodOrder);
                            db.getParent().child("total").setValue(String.valueOf(total[0]));
                            db.getParent().child("userName").setValue(Common.currentUser.getUserName());
                            Toast.makeText(ViewFoodDetail.this, "Add Food To Cart successfully !", Toast.LENGTH_SHORT).show();
                        }
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
        mDatabase.child("Duy").child("Food").child(foodName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);
                foodOrder.setFoodName(food.getFoodName());
                foodOrder.setPrice(food.getFoodPrice());
                foodOrder.setFoodStallName(food.getFoodStallName());
                FoodRemaining = Integer.parseInt(food.getFoodRemaining());
                txtFoodName.setText("Name " + food.getFoodName());
                txtFoodPrice.setText("Price " + food.getFoodPrice());
                Picasso.with(getBaseContext())
                        .load(food.getFoodImage())
                        .into(imageFood);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}