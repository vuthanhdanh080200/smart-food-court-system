package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.FoodOrder;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Cart extends AppCompatActivity {
    ListView listCartView;
    DatabaseReference mDatabase;
    FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = FirebaseDatabase.getInstance().getReference("Danh").child("Cart").child(Common.currentUser.getUserName());
        listCartView = (ListView)findViewById(R.id.listCartView);

        FirebaseListOptions<FoodOrder> options = new FirebaseListOptions.Builder<FoodOrder>()
                .setLayout(R.layout.cart_item)
                .setQuery(query, FoodOrder.class)
                .build();

        adapter = new FirebaseListAdapter<FoodOrder>(options)
        {
            protected void populateView(@NonNull View view, @NonNull final FoodOrder foodOrder, final int position) {
                final int remainingFood[] = {0};
                DatabaseReference FoodDatabase = FirebaseDatabase.getInstance().getReference("Danh/Food");
                FoodDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        remainingFood[0] = Integer.parseInt(dataSnapshot.child(foodOrder.getFoodName()).child("foodRemaining").getValue().toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


                //Log.e("LogFuncIntOutside", Integer.toString(Common.currentRemaining));

                TextView txtCartName = view.findViewById(R.id.txtCartName);
                txtCartName.setText("Food Name: " + foodOrder.getFoodName());
                final TextView txtCartQuantity = view.findViewById(R.id.txtCartQuantity);
                txtCartQuantity.setText("Food Quantity: " + foodOrder.getQuantity());
                //txtCartQuantity.setText("Food Quantity: " + cart.getQuantity() + "/" + getRemainingFood(cart.getProductID()));
                TextView txtCartPrice = view.findViewById(R.id.txtCartPrice);
                txtCartPrice.setText("Food Price : " + foodOrder.getPrice());
                final ElegantNumberButton btnCartQuantity = view.findViewById(R.id.btnCartQuantity);

                btnCartQuantity.setRange(1, 50);
                btnCartQuantity.setNumber(foodOrder.getQuantity());

                btnCartQuantity.setOnClickListener(new ElegantNumberButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity = Integer.parseInt(btnCartQuantity.getNumber());

                        if(quantity > remainingFood[0]){
                            Toast.makeText(Cart.this, "Insufficient remaining food", Toast.LENGTH_SHORT).show();
                            btnCartQuantity.setNumber(Integer.toString(quantity - 1));
                        }
                        else {
                            foodOrder.setQuantity(btnCartQuantity.getNumber());
                            txtCartQuantity.setText("Food Quantity: " + foodOrder.getQuantity());
                            //txtCartQuantity.setText("Food Quantity: " + cart.getQuantity() + "/" + getRemainingFood(cart.getProductID()));
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    mDatabase.child("Danh/Cart").child(Common.currentUser.getUserName()).child(foodOrder.getFoodName())
                                            .child("foodQuantity").setValue(foodOrder.getQuantity());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
            }
        };

        try{
            listCartView.setAdapter(adapter);
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