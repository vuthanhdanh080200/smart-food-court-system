package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

//TO DO

public class AddFood extends AppCompatActivity {
    EditText edtFoodID, edtFoodName, edtFoodPrice;
    Button btnAddFood, btnViewFood;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        mDatabase = FirebaseDatabase.getInstance().getReference("Food");

        edtFoodID = (MaterialEditText)findViewById(R.id.edtFoodID);
        edtFoodName = (MaterialEditText)findViewById(R.id.edtFoodName);
        edtFoodPrice = (MaterialEditText)findViewById(R.id.edtFoodPrice);
        btnAddFood = (Button)findViewById(R.id.btnAddFood);
        btnViewFood = (Button)findViewById(R.id.btnViewFood);

        btnAddFood.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Food food = new Food(
                            edtFoodID.getText().toString(),
                            edtFoodName.getText().toString(),
                            edtFoodPrice.getText().toString());

                    mDatabase.child(edtFoodID.getText().toString()).setValue(food);
                    Toast.makeText(AddFood.this, "Add food successfully !", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            }
        });

        btnViewFood.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            Intent foodIntent = new Intent(AddFood.this, ViewFood.class);
            startActivity(foodIntent);
            }
        });
    }
}