package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewFood extends AppCompatActivity {
    ListView listFoodView;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);

        mDatabase = FirebaseDatabase.getInstance().getReference("Food");
        listFoodView = (ListView)findViewById(R.id.listFoodView);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Count " ,""+ dataSnapshot.getChildrenCount());
                List<String> foodNameList = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Food food = postSnapshot.getValue(Food.class);
                    foodNameList.add(food.getFoodName());
                }
                ArrayAdapter<String> arrayAdapter
                        = new ArrayAdapter<String>(ViewFood.this, android.R.layout.simple_list_item_1 , foodNameList);
                listFoodView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}