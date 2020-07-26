package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
                    //Kiểm tra đã điền đầy đủ form chưa (phần mô tả và số lượng món ăn có thể để trống) ?
                    //Kiểm tra tên món ăn đã bị trùng chưa ?
                    //Kiểm tra tên quầy có chưa ? (quầy chưa có thì không được điền)
                    mDatabase.child("Food").child(edtFoodName.getText().toString()).setValue(food);
                    mDatabase.child("FoodStall").child(edtFoodStallName.getText().toString()).child("FoodList").child(edtFoodName.getText().toString()).setValue(food);
                    Toast.makeText(AddFood.this, Common.addFoodSuccessMessage, Toast.LENGTH_SHORT).show();
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