package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    TextView txtCartTotal;
    Button btnCheckOut ;
    final int CartTotal[] = {0};
    Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Query query = FirebaseDatabase.getInstance().getReference("Duy").child("Cart")
                .child(Common.currentUser.getUserName()).child("foodOrderList");
        listCartView = (ListView)findViewById(R.id.listCartView);
        txtCartTotal = (TextView)findViewById(R.id.txtCartTotal);
        btnCheckOut = (Button)findViewById(R.id.btnCheckOut);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtCartTotal.setText("Please wait...");
        btnCheckOut.setVisibility(View.GONE);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String total = dataSnapshot.child("Duy/Cart").child(Common.currentUser.getUserName())
                            .child("total").getValue().toString();
                    txtCartTotal.setText("Total: " + total + " VND");
                    btnCheckOut.setVisibility(View.VISIBLE);
                    CartTotal[0] = Integer.parseInt(total);
                }
                catch(Exception e){
                    CartTotal[0] = 0;
                }
                if(CartTotal[0] == 0){
                    txtCartTotal.setText("Your cart is empty.");
                    btnCheckOut.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseListOptions<FoodOrder> options = new FirebaseListOptions.Builder<FoodOrder>()
                .setLayout(R.layout.cart_item)
                .setQuery(query, FoodOrder.class)
                .build();

        adapter = new FirebaseListAdapter<FoodOrder>(options)
        {
            protected void populateView(@NonNull View view, @NonNull final FoodOrder foodOrder, final int position) {
                final int remainingFood[] = {0};
                DatabaseReference FoodDatabase = FirebaseDatabase.getInstance().getReference("Duy/Food");
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
                txtCartName.setText(foodOrder.getFoodName());
                final TextView txtCartQuantity = view.findViewById(R.id.txtCartQuantity);
                txtCartQuantity.setText("x " + foodOrder.getQuantity());
                //txtCartQuantity.setText("Food Quantity: " + cart.getQuantity() + "/" + getRemainingFood(cart.getProductID()));
                final TextView txtCartPrice = view.findViewById(R.id.txtCartPrice);
                txtCartPrice.setText("Unit Price : " + foodOrder.getPrice());
                final ElegantNumberButton btnCartQuantity = view.findViewById(R.id.btnCartQuantity);
                Button btnRemove = (Button)view.findViewById(R.id.btnRemove);

                btnCartQuantity.setRange(1, 1000);
                btnCartQuantity.setNumber(foodOrder.getQuantity());

                btnCartQuantity.setOnClickListener(new ElegantNumberButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int quantity[] = {Integer.parseInt(btnCartQuantity.getNumber())};

                        if(quantity[0] > remainingFood[0]){
                            Toast.makeText(Cart.this, "Insufficient remaining food", Toast.LENGTH_SHORT).show();
                            btnCartQuantity.setNumber(Integer.toString(quantity[0] - 1));
                        }
                        else {
                            final int oldQuantity[] = {0};
                            oldQuantity[0] = Integer.parseInt(foodOrder.getQuantity());

                            foodOrder.setQuantity(btnCartQuantity.getNumber());
                            txtCartQuantity.setText("x " + foodOrder.getQuantity());
                            //txtCartQuantity.setText("Food Quantity: " + cart.getQuantity() + "/" + getRemainingFood(cart.getProductID()));
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    mDatabase.child("Duy/Cart").child(Common.currentUser.getUserName())
                                            .child("foodOrderList").child(foodOrder.getFoodName())
                                            .child("quantity").setValue(foodOrder.getQuantity());
                                    CartTotal[0] += (quantity[0] - oldQuantity[0])*Integer.parseInt(foodOrder.getPrice());
                                    mDatabase.child("Duy/Cart").child(Common.currentUser.getUserName()).child("total").setValue(String.valueOf(CartTotal[0]));
                                    txtCartTotal.setText(String.valueOf(CartTotal[0]));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
                btnRemove.setOnClickListener(new Button.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        int quantity = Integer.parseInt(foodOrder.getQuantity());
                        int price = Integer.parseInt(foodOrder.getPrice());
                        CartTotal[0] -= quantity*price;
                        txtCartPrice.setText(String.valueOf(CartTotal[0]));

                        if(CartTotal[0] == 0){
                            txtCartTotal.setText("Your cart is empty.");
                            btnCheckOut.setVisibility(View.INVISIBLE);
                            mDatabase.child("Duy/Cart").child(Common.currentUser.getUserName()).getRef().removeValue();
                        }
                        else{
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    mDatabase.child("Duy/Cart").child(Common.currentUser.getUserName()).child("total").setValue(String.valueOf(CartTotal[0]));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            getRef(position).removeValue();
                            notifyDataSetChanged();
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
        btnCheckOut.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent cart = new Intent(Cart.this, Payment.class);
                startActivity(cart);
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_3, menu);
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
            Intent intent = new Intent(Cart.this, Home.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}