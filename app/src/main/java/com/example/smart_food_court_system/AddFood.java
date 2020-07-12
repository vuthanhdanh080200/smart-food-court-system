package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddFood extends AppCompatActivity {
    EditText edtFoodStallName,
             edtFoodName,
             edtFoodType,
             edtFoodPrice,
             edtFoodDescription,
             edtFoodRemaining,
             edtFoodImage;
    Button btnAddFood, btnViewFood;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        mDatabase = FirebaseDatabase.getInstance().getReference("Danh");
        edtFoodStallName = (EditText)findViewById(R.id.edtFoodStallName);
        edtFoodName = (EditText)findViewById(R.id.edtFoodName);
        edtFoodType = (EditText)findViewById(R.id.edtFoodType);
        edtFoodPrice = (EditText)findViewById(R.id.edtFoodPrice);
        edtFoodDescription = (EditText)findViewById(R.id.edtFoodDescription);
        edtFoodRemaining = (EditText)findViewById(R.id.edtFoodRemaining);
        edtFoodImage = (EditText)findViewById(R.id.edtFoodImage);
        btnAddFood = (Button)findViewById(R.id.btnAddFood);
        btnViewFood = (Button)findViewById(R.id.btnViewFood);

        btnAddFood.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Food food = new Food(
                            edtFoodStallName.getText().toString(),
                            edtFoodName.getText().toString(),
                            edtFoodType.getText().toString(),
                            edtFoodPrice.getText().toString(),
                            edtFoodDescription.getText().toString(),
                            edtFoodRemaining.getText().toString(),
                            edtFoodImage.getText().toString()
                            );
                    //To Do
                    //Kiểm tra đã điền đầy đủ form chưa (phần mô tả và số lượng món ăn có thể để trống) ?
                    //Kiểm tra tên món ăn đã bị trùng chưa ?
                    //Kiểm tra tên quầy có chưa ? (quầy chưa có thì không được điền)
                    mDatabase.child("Food").child(edtFoodName.getText().toString()).setValue(food);
                    mDatabase.child("FoodStall").child(edtFoodStallName.getText().toString()).child(edtFoodName.getText().toString()).setValue(food);
                    Toast.makeText(AddFood.this, Common.addFoodSuccessMessage, Toast.LENGTH_SHORT).show();
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
            Intent home = new Intent(AddFood.this, Home.class);
            Common.currentUser = Common.admin;
            startActivity(home);
            }
        });
    }
}