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
import com.example.smart_food_court_system.model.CustomerCart;
import com.example.smart_food_court_system.model.Order;
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

//Danh DO
//Trong giỏ hàng hiển thị thêm tổng tiền
//Hiển thị nút "gì đó" để thanh toán,

//Hiện thực chức năng thanh toán bằng app
//Hàm kiểm tra các đồ ăn trong giỏ hàng còn không khi bấm nút "gì đó", và số lượng các đồ ăn tương ứng bị giảm xuống.
//Sau khi thanh toán thì giỏ hàng của người đó biến mất, lấy Common.currentuser.getUserName() để lấy tên người đang sử dụng
//Cập nhật database
//Hiện thông báo
//...

//Hiện thực chức năng xóa món ăn ra khỏi giỏ hàng

//Nếu không có món ăn trong giỏ hàng thì hiện thông báo
//Hiển thị lên màn hình thông báo gì đó <LINE 68>

public class Cart extends AppCompatActivity {
    ListView listCartView;
    DatabaseReference mDatabase;
    FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = FirebaseDatabase.getInstance().getReference("Demo").child("Cart").child(Common.currentUser.getUserName());
        listCartView = (ListView)findViewById(R.id.listCartView);

        FirebaseListOptions<CustomerCart> options = new FirebaseListOptions.Builder<CustomerCart>()
                .setLayout(R.layout.cart_item)
                .setQuery(query, CustomerCart.class)
                .build();

        adapter = new FirebaseListAdapter<CustomerCart>(options)
        {
            protected void populateView(@NonNull View view, @NonNull final CustomerCart cart, final int position) {
                final int remainingFood[] = {0};
                DatabaseReference FoodDatabase = FirebaseDatabase.getInstance().getReference("Demo/Food");
                FoodDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        remainingFood[0] = Integer.parseInt(dataSnapshot.child(cart.getProductID()).child("remaining").getValue().toString());
                        //Log.e("LogsFunc", dataSnapshot.child(FoodID).child("remaining").getValue().toString());
                        //Log.e("LogFuncIntInside", Integer.toString(Common.currentRemaining));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


                //Log.e("LogFuncIntOutside", Integer.toString(Common.currentRemaining));

                TextView txtCartName = view.findViewById(R.id.txtCartName);
                txtCartName.setText("Food Name: " + cart.getProductName());
                final TextView txtCartQuantity = view.findViewById(R.id.txtCartQuantity);
                txtCartQuantity.setText("Food Quantity: " + cart.getQuantity());
                //txtCartQuantity.setText("Food Quantity: " + cart.getQuantity() + "/" + getRemainingFood(cart.getProductID()));
                TextView txtCartPrice = view.findViewById(R.id.txtCartPrice);
                txtCartPrice.setText("Food Price : " + cart.getPrice());
                final ElegantNumberButton btnCartQuantity = view.findViewById(R.id.btnCartQuantity);

                btnCartQuantity.setRange(1, 50);
                btnCartQuantity.setNumber(cart.getQuantity());

                btnCartQuantity.setOnClickListener(new ElegantNumberButton.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity = Integer.parseInt(btnCartQuantity.getNumber());

                        if(quantity > remainingFood[0]){
                            Toast.makeText(Cart.this, "Insufficient remaining food", Toast.LENGTH_SHORT).show();
                            btnCartQuantity.setNumber(Integer.toString(quantity - 1));
                        }
                        else {
                            cart.setQuantity(btnCartQuantity.getNumber());
                            txtCartQuantity.setText("Food Quantity: " + cart.getQuantity());
                            //txtCartQuantity.setText("Food Quantity: " + cart.getQuantity() + "/" + getRemainingFood(cart.getProductID()));
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    mDatabase.child("Cart").child(Common.currentUser.getUserName()).child(cart.getProductName())
                                            .child("quantity").setValue(cart.getQuantity());
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