package com.example.smart_food_court_system;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    DatabaseReference mDatabase;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mDatabase = FirebaseDatabase.getInstance().getReference("Food");
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        listView = (ListView)findViewById(R.id.listView);

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
                        = new ArrayAdapter<String>(Home.this, android.R.layout.simple_list_item_1 , foodNameList);
                listView.setAdapter(arrayAdapter);

                //String[] myDataset = foodNameList.toArray(new String[0]);
                //Log.e("Check " ,""+ myDataset[0]);
                //mAdapter = new MyAdapter(myDataset);
                //recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}