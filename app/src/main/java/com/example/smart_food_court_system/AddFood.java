package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddFood extends AppCompatActivity {
    EditText edtFoodStallName,
             edtFoodName,
             edtFoodType,
             edtFoodPrice,
             edtFoodDescription,
             edtFoodRemaining;
    Button btnAddFood, btnCancel;
    DatabaseReference mDatabase;
    // new
    Button btnChoose;
    ImageView imgFood;
    Bitmap selectedBitmap;
    String imgeEncoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        mDatabase = FirebaseDatabase.getInstance().getReference("Duy");

        edtFoodStallName = (EditText)findViewById(R.id.edtFoodStallName);
        if(Common.currentUser.getRole().equals("cook")){
            edtFoodStallName.setText(Common.currentUser.getStall());
            edtFoodStallName.setTag(edtFoodStallName.getKeyListener());
            edtFoodStallName.setKeyListener(null);
        }
        edtFoodName = (EditText)findViewById(R.id.edtFoodName);
        edtFoodType = (EditText)findViewById(R.id.edtFoodType);
        edtFoodPrice = (EditText)findViewById(R.id.edtFoodPrice);
        edtFoodDescription = (EditText)findViewById(R.id.edtFoodDescription);
        edtFoodRemaining = (EditText)findViewById(R.id.edtFoodRemaining);
        btnAddFood = (Button)findViewById(R.id.btnAddFood);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        // new
        btnChoose = (Button)findViewById(R.id.btnChoose);
        imgFood = (ImageView)findViewById(R.id.imgFood);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 200);
            }
        });


        btnAddFood.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                final ProgressDialog mDialog = new ProgressDialog(AddFood.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();
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
                                imgeEncoded
                                //edtFoodImage.getText().toString()
                                );
                        //To Do
                        if(food.getFoodStallName().isEmpty()
                        || food.getFoodName().isEmpty()
                        || food.getFoodType().isEmpty()
                        || food.getFoodPrice().isEmpty()
                        || food.getFoodDescription().isEmpty()
                        || food.getFoodRemaining().isEmpty()
                        || food.getFoodImage().isEmpty()){
                            mDialog.dismiss();
                            Toast.makeText(AddFood.this, "Please fill in all information", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(dataSnapshot.child("Food").child(food.getFoodName()).exists()){
                                mDialog.dismiss();
                                Toast.makeText(AddFood.this, "Food name " + food.getFoodName() + " exists, please choose another food name.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                mDialog.dismiss();
                                mDatabase.child("Food").child(food.getFoodName()).setValue(food);
                                Log.e("ERR", food.getFoodName());
                                mDatabase.child("FoodStall").child(food.getFoodStallName()).child("FoodList").child(food.getFoodName()).setValue(food);
                                Toast.makeText(AddFood.this, Common.addFoodSuccessMessage, Toast.LENGTH_SHORT).show();
                                if (Common.currentUser.getRole().equals("cook")) {
                                    Intent intent = new Intent(AddFood.this, FoodManagement.class);
                                    startActivity(intent);
                                }
                                else if (Common.currentUser.getRole().equals("manager")) {
                                    Intent intent = new Intent(AddFood.this, HomeManager.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (Common.currentUser.getRole().equals("cook")) {
                    Intent intent = new Intent(AddFood.this, FoodManagement.class);
                    startActivity(intent);
                }
                else if (Common.currentUser.getRole().equals("manager")) {
                    Intent intent = new Intent(AddFood.this, HomeManager.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            try {
                //xử lý lấy ảnh chọn từ điện thoại:
                Uri imageUri = data.getData();
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imgFood.setImageBitmap(selectedBitmap);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                imgeEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}