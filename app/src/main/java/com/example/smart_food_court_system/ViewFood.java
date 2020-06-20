package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smart_food_court_system.model.Food;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ViewFood extends AppCompatActivity {
    ListView listFoodView;
    FirebaseListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);

        Query query = FirebaseDatabase.getInstance().getReference().child("Food");
        listFoodView = (ListView)findViewById(R.id.listFoodView);

        FirebaseListOptions<Food> options = new FirebaseListOptions.Builder<Food>()
                .setLayout(R.layout.food_item)
                .setQuery(query, Food.class)
                .build();

        adapter = new FirebaseListAdapter<Food>(options)
        {
            protected void populateView(@NonNull View view, @NonNull Food food, final int position) {
                TextView foodName = view.findViewById(R.id.txtFoodName);
                foodName.setText("Food Name: " + food.getFoodName());
                TextView foodPrice = view.findViewById(R.id.txtFoodPrice);
                foodPrice.setText("Food Price: " +food.getFoodPrice());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent foodDetailIntent = new Intent(ViewFood.this, ViewFoodDetail.class);
                        foodDetailIntent.putExtra("FoodID", adapter.getRef(position).getKey());
                        startActivity(foodDetailIntent);
                    }
                });
            }
        };

        // Now set the adapter with a given layout
        listFoodView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}