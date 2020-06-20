package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.Order;
import com.example.smart_food_court_system.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {
    ListView listOrderView;
    DatabaseReference mDatabase;
    FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Query query = FirebaseDatabase.getInstance().getReference().child("Cart").child("nguyenvana").child("Order");
        listOrderView = (ListView)findViewById(R.id.listOrderView);

        FirebaseListOptions<Order> options = new FirebaseListOptions.Builder<Order>()
                .setLayout(R.layout.order_item)
                .setQuery(query, Order.class)
                .build();

        adapter = new FirebaseListAdapter<Order>(options)
        {
            protected void populateView(@NonNull View view, @NonNull Order order, final int position) {
                TextView orderName = view.findViewById(R.id.txtOrderName);
                orderName.setText("Food Name: " + order.getProductName());
                TextView orderQuantity = view.findViewById(R.id.txtOrderQuantity);
                orderQuantity.setText("Food Quantity: " + order.getQuantity());
                TextView orderPrice = view.findViewById(R.id.txtOrderPrice);
                orderPrice.setText("Food Price : " + order.getPrice());

            }
        };
        listOrderView.setAdapter(adapter);
        /*
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Count " ,""+ dataSnapshot.getChildrenCount());
                List<String> foodNameList = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Order order = postSnapshot.getValue(Order.class);
                    foodNameList.add(order.getProductName());
                }
                ArrayAdapter<String> arrayAdapter
                        = new ArrayAdapter<String>(Cart.this, android.R.layout.simple_list_item_1 , foodNameList);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

         */

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