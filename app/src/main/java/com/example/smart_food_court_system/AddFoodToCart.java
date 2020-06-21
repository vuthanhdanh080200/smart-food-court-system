package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
//Tùy chỉnh điều kiện:
//Không được add Food khi có quantity là 1
//Không được add Food quá số lượng có hạn của food
//và các điều kiện khác :> Hehe

public class AddFoodToCart extends AppCompatActivity {
    EditText edtFoodID, edtFoodName, edtFoodQuantity, edtFoodPrice;
    Button btnAddFoodToCart, btnViewFoodInCart;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_to_cart);

        mDatabase = FirebaseDatabase.getInstance().getReference("Order");

        edtFoodID = (MaterialEditText)findViewById(R.id.edtFoodID);
        edtFoodName = (MaterialEditText)findViewById(R.id.edtFoodName);
        edtFoodQuantity = (MaterialEditText)findViewById(R.id.edtFoodQuantity);
        edtFoodPrice = (MaterialEditText)findViewById(R.id.edtFoodPrice);
        btnAddFoodToCart = (Button)findViewById(R.id.btnAddFoodToCart);
        btnViewFoodInCart = (Button)findViewById(R.id.btnViewFoodInCart);

        btnAddFoodToCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> foodID = new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Order order = postSnapshot.getValue(Order.class);
                            if (order.getProductID().equals(edtFoodID.getText().toString())) {
                                Toast.makeText(AddFoodToCart.this, "Add Food To Cart successfully !", Toast.LENGTH_SHORT).show();
                            }

                        }

                        Order order = new Order(
                                edtFoodID.getText().toString(),
                                edtFoodName.getText().toString(),
                                edtFoodQuantity.getText().toString(),
                                edtFoodPrice.getText().toString());

                        mDatabase.child(edtFoodID.getText().toString()).setValue(order);
                        Toast.makeText(AddFoodToCart.this, "Add Food To Cart successfully !", Toast.LENGTH_SHORT).show();
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
                Intent cart = new Intent(AddFoodToCart.this, Cart.class);
                startActivity(cart);
            }
        });

    }


}