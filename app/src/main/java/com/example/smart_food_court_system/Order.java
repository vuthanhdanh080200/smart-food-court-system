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

import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
//Giao dien cua Waitor va cook
public class Order extends AppCompatActivity {
    ListView listOrderView;
    FirebaseListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Hieu");

        Query query = FirebaseDatabase.getInstance().getReference().child("Hieu").child("Order");
        listOrderView = (ListView) findViewById(R.id.lVOrder);

        FirebaseListOptions<com.example.smart_food_court_system.model.Order> options = new FirebaseListOptions.Builder<com.example.smart_food_court_system.model.Order>()
                .setLayout(R.layout.order_item)
                .setQuery(query, com.example.smart_food_court_system.model.Order.class)
                .build();

        adapter = new FirebaseListAdapter<com.example.smart_food_court_system.model.Order>(options) {
            protected void populateView(@NonNull View view, @NonNull com.example.smart_food_court_system.model.Order order, final int position) {
                TextView userName = view.findViewById(R.id.txtUserName);
                userName.setText("User name: " + order.getUserName());
                TextView totalPrice = view.findViewById(R.id.txtTotalPrice);
                totalPrice.setText("Total: " + order.getTotal());
                TextView status = view.findViewById(R.id.txtStatus);
                status.setText("Status order: " + order.getStatus());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent orderDetailIntent = new Intent(Order.this, ViewOrderDetail.class);
                        orderDetailIntent.putExtra("ID", adapter.getRef(position).getKey());
                        startActivity(orderDetailIntent);
                    }
                });


            }
        };

        try{
            listOrderView.setAdapter(adapter);
        }catch(Exception e){
            Log.e("Error " ,""+ e.getMessage());
            //TO DO--------------------------------------------------------
            //Hiển thị lên màn hình giao diện đồ ăn hiện tại không sẵn sàng
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