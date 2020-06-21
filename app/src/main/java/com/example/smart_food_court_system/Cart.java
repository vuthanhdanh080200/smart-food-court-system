package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Order;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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
    ListView listOrderView;
    DatabaseReference mDatabase;
    FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Query query = FirebaseDatabase.getInstance().getReference().child("Cart").child(Common.currentUser.getUserName()).child("Order");
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

        try{
            listOrderView.setAdapter(adapter);
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