package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.FoodOrder;
import com.example.smart_food_court_system.model.Order;
import com.example.smart_food_court_system.model.User;
import com.example.smart_food_court_system.model.UserCart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Payment extends AppCompatActivity {
    RadioGroup grpPaymentOption;

    RadioButton rdCashSettlement;
    RadioButton rdUseAccountBalance;
    RadioButton rdEwallet;

    TextView txtCashSettlement;
    TextView txtUseAccountBalanceCurrent;
    TextView txtUseAccountBalanceUse;
    TextView txtUseAccountBalanceRemaining;

    Button btnPay;
    DatabaseReference mDatabase;
    String methodPayment = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        grpPaymentOption = (RadioGroup)findViewById(R.id.grpPaymentOption);

        rdCashSettlement = (RadioButton)findViewById(R.id.rdCashSettlement);
        rdUseAccountBalance = (RadioButton)findViewById(R.id.rdUseAccountBalance);
        rdEwallet = (RadioButton)findViewById(R.id.rdEwallet);

        txtCashSettlement = (TextView)findViewById(R.id.txtCashSettlement);
        txtUseAccountBalanceCurrent = (TextView)findViewById(R.id.txtUseAccountBalanceCurrent);
        txtUseAccountBalanceUse = (TextView)findViewById(R.id.txtUseAccountBalanceUse);
        txtUseAccountBalanceRemaining = (TextView)findViewById(R.id.txtUseAccountBalanceRemaining);

        btnPay = (Button)findViewById(R.id.btnPay);

        btnPay.setText("Please choose 1 method");
        btnPay.setClickable(false);
        btnPay.setVisibility(View.GONE);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Duy");

        grpPaymentOption.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton)grpPaymentOption.findViewById(i);
                int index = grpPaymentOption.indexOfChild(radioButton);

                switch(index){
                    case 1:
                        txtCashSettlement.setVisibility(View.VISIBLE);
                        txtUseAccountBalanceCurrent.setVisibility(View.GONE);
                        txtUseAccountBalanceUse.setVisibility(View.GONE);
                        txtUseAccountBalanceRemaining.setVisibility(View.GONE);
                        btnPay.setText("Pay");
                        btnPay.setClickable(true);
                        btnPay.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        final int current[] = {0};
                        final int use[] = {0};
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                current[0] = Integer.parseInt(dataSnapshot.child("User")
                                        .child(Common.currentUser.getUserName())
                                        .child("accountBalance").getValue().toString());
                                try {
                                    use[0] = Integer.parseInt(dataSnapshot.child("Cart")
                                            .child(Common.currentUser.getUserName())
                                            .child("total").getValue().toString());
                                }
                                catch(Exception e){
                                    use[0] = 0;
                                }
                                txtUseAccountBalanceCurrent.setText("Current: " + String.valueOf(current[0]));
                                txtUseAccountBalanceUse.setText("Use: " + String.valueOf(use[0]));
                                txtUseAccountBalanceRemaining.setText("Remaining: " + String.valueOf(current[0] - use[0]));
                                if(current[0] >= use[0]) {
                                    btnPay.setText("Pay");
                                    btnPay.setVisibility(View.VISIBLE);
                                }
                                else{
                                    btnPay.setText("Not enough!");
                                    btnPay.setClickable(false);
                                    btnPay.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        txtCashSettlement.setVisibility(View.GONE);
                        txtUseAccountBalanceCurrent.setVisibility(View.VISIBLE);
                        txtUseAccountBalanceUse.setVisibility(View.VISIBLE);
                        txtUseAccountBalanceRemaining.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        txtCashSettlement.setVisibility(View.GONE);
                        txtUseAccountBalanceCurrent.setVisibility(View.GONE);
                        txtUseAccountBalanceUse.setVisibility(View.GONE);
                        txtUseAccountBalanceRemaining.setVisibility(View.GONE);
                        btnPay.setText("Redirect to Momo wallet");
                        btnPay.setClickable(true);
                        btnPay.setVisibility(View.VISIBLE);
                        break;
                    default:
                        btnPay.setText("Please choose 1 method");
                        btnPay.setClickable(false);
                        break;
                }
            }
        });

        btnPay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Remove the cart, add cart info to Order.
                //Bugs: Synchronization problem when changing remaining food.
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserCart cart = dataSnapshot.child("Cart")
                                .child(Common.currentUser.getUserName())
                                .getValue(UserCart.class);

                        String newID = String.valueOf(dataSnapshot.child("Order").getChildrenCount());
                        updateOrder(mDatabase, newID, cart);
                        //Delete cart
                        dataSnapshot.child("Cart").child(Common.currentUser.getUserName()).getRef().removeValue();

                        //Decrease amount of remaining food in FOOD, FOODSTALL
                        for(String food : cart.getFoodOrderList().keySet()){
                            changeFoodAmount(mDatabase, dataSnapshot, food, cart);
                        }

                        //If customer use account balance, decrease their balance.
                        if(rdUseAccountBalance.isChecked()){
                            int use = Integer.parseInt(cart.getTotal());
                            int current = Integer.parseInt(Common.currentUser.getAccountBalance());
                            int newBalance = current - use;
                            mDatabase.child("User").child(Common.currentUser.getUserName())
                                    .child("accountBalance")
                                    .setValue(String.valueOf(newBalance));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Toast.makeText(Payment.this, "Successful", Toast.LENGTH_SHORT).show();
                Intent PaymentToHome = new Intent(Payment.this, Home.class);
                startActivity(PaymentToHome);
            }
        });

    }
    protected void updateOrder(DatabaseReference mDatabase, String OrderID, UserCart cart){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String timestamp = df.format(c.getTime());


        if(rdUseAccountBalance.isChecked()) {
            methodPayment = "account balance";
        }
        else if(rdCashSettlement.isChecked()) {
            methodPayment = "cash";
        }
        else if(rdEwallet.isChecked()) {
            methodPayment = "ewall";
        }
        Order newOrder = new Order(OrderID,
                Common.currentUser.getUserName(),
                cart.getFoodOrderList(),
                cart.getTotal(),
                timestamp,
                "ready " + Common.currentUser.getUserName(),
                methodPayment);
        //Update order
        mDatabase.child("Order")
                .child(String.valueOf(OrderID) + " : " + Common.currentUser.getUserName())
                .setValue(newOrder);
    }
    protected void changeFoodAmount(DatabaseReference mDatabase, DataSnapshot dataSnapshot, String food, UserCart cart){
        String foodStall = dataSnapshot.child("Food")
                .child(food).child("foodStallName")
                .getValue().toString();
        int remaining = Integer.parseInt(dataSnapshot
                .child("Food").child(food)
                .child("foodRemaining").getValue().toString());
        int newRemaining = remaining -  Integer.parseInt(cart.getFoodOrderList().get(food).getQuantity());
        mDatabase.child("Food").child(food)
                .child("foodRemaining")
                .setValue(String.valueOf(newRemaining));
        mDatabase.child("FoodStall").child(foodStall)
                .child("FoodList").child(food)
                .child("foodRemaining")
                .setValue(String.valueOf(newRemaining));

    }
}