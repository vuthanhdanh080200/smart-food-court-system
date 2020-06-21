package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smart_food_court_system.model.Category;
import com.example.smart_food_court_system.model.Food;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

//Duy DO--------------------------------------------------------
//Hiển thị lên màn hình thông báo gì đó (LINE 62)

public class ViewFood extends AppCompatActivity {
    ListView listFoodView;
    FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);

        Query query = FirebaseDatabase.getInstance().getReference().child("Danh-Food");
        listFoodView = (ListView)findViewById(R.id.listFoodView);


        FirebaseListOptions<Category> options = new FirebaseListOptions.Builder<Category>()
                .setLayout(R.layout.food_item)
                .setQuery(query, Category.class)
                .build();

        adapter = new FirebaseListAdapter<Category>(options)
        {
            protected void populateView(@NonNull View view, @NonNull Category category, final int position) {
                TextView foodName = view.findViewById(R.id.txtFoodName);
                foodName.setText("Food Name: " + category.getFoodName());
                //TextView foodPrice = view.findViewById(R.id.txtFoodPrice);
                //foodPrice.setText("Food Price: " +food.getFoodPrice());
                ImageView imageFood;
                imageFood = (ImageView)view.findViewById(R.id.imageFood);
                Log.e("Error " ,""+ category.getFoodImage());
                Picasso.with(getBaseContext())
                        .load(""+category.getFoodImage())
                        .into(imageFood);

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
        try{
            listFoodView.setAdapter(adapter);
        }catch(Exception e){
            Log.e("Error " ,""+ e.getMessage());
            //TO DO--------------------------------------------------------
            //Hiển thị lên màn hình thông báo gì đó
        }

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