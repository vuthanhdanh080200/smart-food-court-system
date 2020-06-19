package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button btnSignIn, btnSignUp, btnAddFood, btnAddFoodToCart;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button)findViewById(R.id.SignIn);
        btnSignUp = (Button)findViewById(R.id.SignUp);
        btnAddFood = (Button)findViewById(R.id.AddFood);
        btnAddFoodToCart = (Button)findViewById(R.id.AddFoodToCart);

        txtSlogan = (TextView)findViewById(R.id.txtSlogan);

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });

        btnAddFood.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent addFoodIntent = new Intent(MainActivity.this, AddFood.class);
                startActivity(addFoodIntent);
            }
        });

        btnAddFoodToCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent addFoodToCartIntent = new Intent(MainActivity.this, AddFoodToCart.class);
                startActivity(addFoodToCartIntent);
            }
        });
    }
}