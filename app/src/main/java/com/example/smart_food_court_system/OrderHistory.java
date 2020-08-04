package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.Order;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OrderHistory extends AppCompatActivity {

    Toolbar toolbar = null;
    FirebaseListAdapter adapter;
    ListView listOrderView;
    DatabaseReference mDatabase, db;
    String orderId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Query query = FirebaseDatabase.getInstance().getReference().child("Duy").child("Order").orderByChild("userName").equalTo(Common.currentUser.getUserName());
        listOrderView = (ListView) findViewById(R.id.lVOrder);

        FirebaseListOptions<Order> options = new FirebaseListOptions.Builder<com.example.smart_food_court_system.model.Order>()
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
                if(order.getStatus().equals("ready " + Common.currentUser.getUserName())){
                    status.setText("Status order: ready");
                }
                else if(order.getStatus().equals("cook " + Common.currentUser.getUserName())){
                    status.setText("Status order: cook");
                }
                else if(order.getStatus().equals("cook done " + Common.currentUser.getUserName())){
                    status.setText("Status order: cook done");
                }
                else if(order.getStatus().equals("complete " + Common.currentUser.getUserName())){
                    status.setText("Status order: complete");
                }

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

    /*
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    */
    @Override
    protected  void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_go_home) {
            Intent intent = new Intent(OrderHistory.this, Home.class);
            startActivity(intent);
        }
        else if (id == R.id.action_ready) {
            listOrderByStatus("ready");
        }
        else if (id == R.id.action_cook) {
            listOrderByStatus("cook");
        }
        else if (id == R.id.action_cook_done) {
            listOrderByStatus("cook done");
        }
        else if (id == R.id.action_complete) {
            listOrderByStatus("complete");
        }


        return super.onOptionsItemSelected(item);
    }

    private void listOrderByStatus(String status){

        Query query = FirebaseDatabase.getInstance().getReference().child("Duy").child("Order")
                .orderByChild("status").equalTo(status + " " + Common.currentUser.getUserName());



        listOrderView = (ListView) findViewById(R.id.lVOrder);

        FirebaseListOptions<Order> options = new FirebaseListOptions.Builder<Order>()
                .setLayout(R.layout.order_item)
                .setQuery(query, Order.class)
                .build();

        adapter.stopListening();

        adapter = new FirebaseListAdapter<Order>(options) {
            protected void populateView(@NonNull View view, @NonNull Order order, final int position) {
                TextView userName = view.findViewById(R.id.txtUserName);
                userName.setText("User name: " + order.getUserName());
                TextView totalPrice = view.findViewById(R.id.txtTotalPrice);
                totalPrice.setText("Total: " + order.getTotal());
                TextView status = view.findViewById(R.id.txtStatus);
                if(order.getStatus().equals("ready " + Common.currentUser.getUserName())){
                    status.setText("Status order: ready");
                }
                else if(order.getStatus().equals("cook " + Common.currentUser.getUserName())){
                    status.setText("Status order: cook");
                }
                else if(order.getStatus().equals("cook done " + Common.currentUser.getUserName())){
                    status.setText("Status order: cook done");
                }
                else if(order.getStatus().equals("complete " + Common.currentUser.getUserName())){
                    status.setText("Status order: complete");
                }


            }
        };
        adapter.startListening();


        try{
            listOrderView.setAdapter(adapter);
        }catch(Exception e){
            Log.e("Error " ,""+ e.getMessage());
            //TO DO--------------------------------------------------------
            //Hiển thị lên màn hình giao diện đồ ăn hiện tại không sẵn sàng
        }
    }
}