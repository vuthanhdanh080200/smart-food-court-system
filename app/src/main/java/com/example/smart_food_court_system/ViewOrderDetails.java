package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.FoodOrder;
import com.example.smart_food_court_system.model.Order;
import com.example.smart_food_court_system.model.User;
import com.example.smart_food_court_system.model.UserCart;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class ViewOrderDetails extends AppCompatActivity {
    TextView txtUserName, txtTotalPrice, txtStatus;
    ListView listFoodOrderView;
    String userName;
    DatabaseReference mDatabase;
    FirebaseListAdapter adapter;
    Button btnCancelOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_details);
        txtUserName = (TextView)findViewById(R.id.txtUserName);
        txtTotalPrice = (TextView) findViewById(R.id.txtTotalPrice);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        btnCancelOrder = (Button) findViewById(R.id.btnCancelOrder);
        btnCancelOrder.setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Duy");

        if(getIntent() != null){
            userName = getIntent().getStringExtra("userName");
        }
        if(!userName.isEmpty()){
            mDatabase.child("Order").child(userName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if(order!= null) {
                        txtUserName.setText("User name: " + order.getUserName());
                        txtTotalPrice.setText("Total: " + order.getTotal() + " VND");
                        if (order.getStatus().equals("ready " + order.getUserName())) {
                            txtStatus.setText("Order status: Preparing for cooking");
                            btnCancelOrder.setVisibility(View.VISIBLE);
                        } else if (order.getStatus().equals("cook " + order.getUserName())) {
                            txtStatus.setText("Order status: Food is being cooked");
                            btnCancelOrder.setVisibility(View.GONE);
                        } else if (order.getStatus().equals("cook done " + order.getUserName())) {
                            txtStatus.setText("Order status: Cook done, waiting for customer to get the food");
                            btnCancelOrder.setVisibility(View.GONE);
                        } else if (order.getStatus().equals("completed " + order.getUserName())) {
                            txtStatus.setText("Order status: Completed");
                            btnCancelOrder.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            btnCancelOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog();
                }
            });

        }

        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Duy")
                .child("Order")
                .child(userName)
                .child("foodOrderList");
        listFoodOrderView = (ListView) findViewById(R.id.lVFoodOrder);

        FirebaseListOptions<FoodOrder> options = new FirebaseListOptions.Builder<FoodOrder>()
                .setLayout(R.layout.order_food_item)
                .setQuery(query, FoodOrder.class)
                .build();

        adapter = new FirebaseListAdapter<FoodOrder>(options) {
            protected void populateView(@NonNull View view, @NonNull FoodOrder foodOrder, final int position) {
                TextView foodName = view.findViewById(R.id.txtFoodName);
                foodName.setText(foodOrder.getFoodName());

                TextView foodPrice = view.findViewById(R.id.txtFoodPrice);
                foodPrice.setText("Unit price: " + foodOrder.getPrice()+" VND");

                TextView foodQuantity = view.findViewById(R.id.txtFoodQuantity);
                foodQuantity.setText("Quantity: " + foodOrder.getQuantity());

                TextView foodStall = view.findViewById(R.id.txtFoodStallName);
                foodStall.setText("Food stall: " + foodOrder.getFoodStallName());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewOrderDetails.this, ViewFoodDetail.class);
                        intent.putExtra("FoodName", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };

        try{
            listFoodOrderView.setAdapter(adapter);
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

    public void openDialog(){
        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.dialogdesign);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        TextView tv = (TextView) builder.findViewById(R.id.dialog_info);
        tv.setText("Are you sure to cancel this order?");
        Button confirm = (Button) builder.findViewById(R.id.dialog_ok);
        Button cancel = (Button) builder.findViewById(R.id.dialog_cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Order order = dataSnapshot.child("Order").child(userName).getValue(Order.class);

                        for(String food : order.getFoodOrderList().keySet()){
                            Log.e("Error order", food);
                            changeFoodAmount(mDatabase, dataSnapshot, food, order);
                        }
                        dataSnapshot.child("Order").child(userName).getRef().removeValue();
                        if(order.getMethodPayment().equals("account balance") || order.getMethodPayment().equals("ewall")) {
                            int use = Integer.parseInt(order.getTotal());
                            int current = Integer.parseInt(Common.currentUser.getAccountBalance());
                            int newBalance = current + use;
                            mDatabase.child("User").child(Common.currentUser.getUserName())
                                    .child("accountBalance")
                                    .setValue(String.valueOf(newBalance));
                            Common.currentUser.setAccountBalance(Integer.toString(newBalance));
                        }
                        Toast.makeText(ViewOrderDetails.this, "Cancel order successfully!", Toast.LENGTH_SHORT).show();
                        builder.dismiss();

                        Intent intent = new Intent(ViewOrderDetails.this, OrderHistory.class);
                        startActivity(intent);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    protected void changeFoodAmount(DatabaseReference mDatabase, DataSnapshot dataSnapshot, String food, Order order){
        String foodStall = dataSnapshot.child("Food")
                .child(food).child("foodStallName")
                .getValue().toString();
        int remaining = Integer.parseInt(dataSnapshot
                .child("Food").child(food)
                .child("foodRemaining").getValue().toString());
        int newRemaining = remaining +  Integer.parseInt(order.getFoodOrderList().get(food).getQuantity());
        mDatabase.child("Food").child(food)
                .child("foodRemaining")
                .setValue(String.valueOf(newRemaining));
        mDatabase.child("FoodStall").child(foodStall)
                .child("FoodList").child(food)
                .child("foodRemaining")
                .setValue(String.valueOf(newRemaining));
    }
}