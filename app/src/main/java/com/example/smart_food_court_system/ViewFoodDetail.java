package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.FoodOrder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewFoodDetail extends AppCompatActivity {
    TextView txtFoodRemaining, txtFoodName, txtFoodPrice, txtOrderQuantity, txtFoodType, txtFoodStall, txtFoodDescr;
    ImageView imageFood;
    Button btnAddFoodToCart;
    FloatingActionButton fabViewFoodInCart;
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
        txtFoodType = (TextView)findViewById(R.id.txtFoodType);
        txtFoodStall = (TextView)findViewById(R.id.txtFoodStall);
        txtFoodDescr = (TextView)findViewById(R.id.txtFoodDescr);

        btnAddFoodToCart = (Button)findViewById(R.id.btnAddFoodToCart);
        btnOrderQuantity = findViewById(R.id.btnOrderQuantity);
        fabViewFoodInCart = (FloatingActionButton)findViewById(R.id.fab_view_cart);
        imageFood = findViewById(R.id.imageFood);


        btnOrderQuantity.setRange(1, 50);

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

        btnAddFoodToCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                db = FirebaseDatabase.getInstance().getReference("Vuong/Cart");
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(btnOrderQuantity.getNumber().equals("0")){
                            Toast.makeText(ViewFoodDetail.this, "Not a proper number", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            db.child(Common.currentUser.getUserName()).child(foodOrder.getFoodName()).setValue(foodOrder);
                            Toast.makeText(ViewFoodDetail.this, "Add Food To Cart successfully !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        fabViewFoodInCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent cart = new Intent(ViewFoodDetail.this, Cart.class);
                startActivity(cart);
            }
        });
    }

    private void getDetailFood(final String foodID) {
        mDatabase.child("Vuong").child("Food").child(foodName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);
                foodOrder.setFoodName(food.getFoodName());
                foodOrder.setPrice(food.getFoodPrice());
                FoodRemaining = Integer.parseInt(food.getFoodRemaining());
                txtFoodName.setText(food.getFoodName());
                txtFoodPrice.setText("Price : " + food.getFoodPrice()+" VND");
                txtFoodType.setText("Type : "+food.getFoodType());
                txtFoodStall.setText("Stall : "+food.getFoodStallName());
                txtFoodRemaining.setText("Food Remaining :"+food.getFoodRemaining());
                txtFoodDescr.setText(food.getFoodDescription());
                byte[] decodedString = Base64.decode(food.getFoodImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageFood.setImageBitmap(decodedByte);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}