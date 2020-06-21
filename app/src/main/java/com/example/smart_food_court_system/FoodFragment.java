package com.example.smart_food_court_system;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.smart_food_court_system.model.Food;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FoodFragment extends Fragment {
    public FoodFragment() {
        // Required empty public constructor
    }

    ListView listFoodView;
    FirebaseListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        Query query = FirebaseDatabase.getInstance().getReference().child("Food");
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
                /*
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent foodDetailIntent = new Intent(FoodFragment.this, ViewFoodDetail.class);
                        foodDetailIntent.putExtra("FoodID", adapter.getRef(position).getKey());
                        startActivity(foodDetailIntent);
                    }
                });

                 */
            }
        };
        listFoodView = (ListView)view.findViewById(R.id.listFoodView);
        // Now set the adapter with a given layout
        try{
            listFoodView.setAdapter(adapter);
        }catch(Exception e){
            Log.e("Error " ,""+ e.getMessage());
            //TO DO--------------------------------------------------------
            //Hiển thị lên màn hình giao diện đồ ăn hiện tại không sẵn sàng
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
