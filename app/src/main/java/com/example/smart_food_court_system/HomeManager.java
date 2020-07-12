package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//Giao dien chinh cua Manager
//THay doi vai tro nguoi dung
//Add Food
//TODO: Xem bao cao?
public class HomeManager extends AppCompatActivity {
    Button btnChangeRoleUser,btnAddFood,btnViewReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_manager);
        btnChangeRoleUser=(Button)findViewById(R.id.btnChangeRole);
        btnChangeRoleUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ChangeRoleUser = new Intent(HomeManager.this, ChangeRoleUser.class);
                startActivity(ChangeRoleUser);
            }
        });
        btnAddFood=(Button)findViewById(R.id.btnAddFood);
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AddFood=new Intent(HomeManager.this, com.example.smart_food_court_system.AddFood.class);
                startActivity(AddFood);
            }
        });
        btnAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeManager.this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}