package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smart_food_court_system.model.Order;
import com.example.smart_food_court_system.databases.Database;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class AddFoodToCart extends AppCompatActivity {
    EditText edtFoodID, edtFoodName, edtFoodQuantity, edtFoodPrice;
    Button btnAddFoodToCart;

    FirebaseDatabase database;
    DatabaseReference foods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_to_cart);

        edtFoodID = (MaterialEditText)findViewById(R.id.edtFoodID);
        edtFoodName = (MaterialEditText)findViewById(R.id.edtFoodName);
        edtFoodQuantity = (MaterialEditText)findViewById(R.id.edtFoodQuantity);
        edtFoodPrice = (MaterialEditText)findViewById(R.id.edtFoodPrice);
        btnAddFoodToCart = (Button)findViewById(R.id.btnAddFoodToCart);

        final DatabaseReference mDatabase;
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");

        btnAddFoodToCart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Order order = new Order(
                        edtFoodID.getText().toString(),
                        edtFoodName.getText().toString(),
                        edtFoodQuantity.getText().toString(),
                        edtFoodPrice.getText().toString());
                new Database(getBaseContext()).addToCart(order);
                Toast.makeText(AddFoodToCart.this, "Add Food To Cart successfully !", Toast.LENGTH_SHORT).show();

                /*
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Toast.makeText(AddFoodToCart.this, "Add Food To Cart successfully !", Toast.LENGTH_SHORT).show();
                        Food food = new Food(
                                edtFoodID.getText().toString(),
                                edtFoodName.getText().toString(),
                                edtFoodPrice.getText().toString());
                        mDatabase.child(edtFoodID.getText().toString()).setValue(food);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                 */
            }
        });
    }


}