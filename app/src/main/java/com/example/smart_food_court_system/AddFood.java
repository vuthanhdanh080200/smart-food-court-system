package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Category;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

//TO DO
//Tự động tăng chỉ số ID của Food lên 1, tùy chỉnh các thứ liên quan tới food_ID
//FoodName là duy nhất
//Không được add khi chưa điền đầy đủ các trường cần thiết, các trường khác là option, và các thứ liên quan :)
//FoodPrice giờ sẽ yêu cầu nhập dạng 24.000, không nhập số âm, chữ số, và các điều kiện liên quan
//Tùy chỉnh lại code sau khi thêm các trường mới

public class AddFood extends AppCompatActivity {
    EditText edtFoodID, edtFoodName, edtFoodPrice, edtFoodImage, edtFoodRemaining;;
    Button btnAddFood, btnViewFood;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        mDatabase = FirebaseDatabase.getInstance().getReference("Demo");

        edtFoodID = (MaterialEditText)findViewById(R.id.edtFoodID);
        edtFoodName = (MaterialEditText)findViewById(R.id.edtFoodName);
        edtFoodPrice = (MaterialEditText)findViewById(R.id.edtFoodPrice);
        edtFoodImage = (MaterialEditText)findViewById(R.id.edtFoodImage);
        edtFoodRemaining = (MaterialEditText)findViewById(R.id.edtFoodRemaining);
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
                            edtFoodPrice.getText().toString(),
                            edtFoodImage.getText().toString(),
                            edtFoodRemaining.getText().toString());
                    Category category = new Category(
                            edtFoodName.getText().toString(),
                            edtFoodImage.getText().toString());
                    mDatabase.child("Food").child(edtFoodID.getText().toString()).setValue(food);
                    mDatabase.child("Category").child(edtFoodID.getText().toString()).setValue(category);
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
            Intent home = new Intent(AddFood.this, Home.class);
            Common.currentUser = new User("Admin", "Admin", "1234", "Admin@gmail.com", "1234");
            startActivity(home);
            }
        });
    }
}