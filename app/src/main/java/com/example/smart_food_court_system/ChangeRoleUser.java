package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smart_food_court_system.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.snapshot.ChildrenNode;

public class ChangeRoleUser extends AppCompatActivity {

    ListView listUserView;
    FirebaseListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_role_user);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Duy");

        Query query = FirebaseDatabase.getInstance().getReference().child("Duy").child("User");
        listUserView = (ListView) findViewById(R.id.lvUser);

        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.user_item)
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseListAdapter<User>(options) {
            protected void populateView(@NonNull View view, @NonNull User user, final int position) {
                TextView txtname = view.findViewById(R.id.txtName);
                txtname.setText("Name: " + user.getName());
                TextView txtRole = view.findViewById(R.id.txtRole);
                txtRole.setText("Role: " + user.getRole());
                TextView txtStall = view.findViewById(R.id.txtStall);
                txtStall.setText("Stall: " + user.getStall());

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent userDetailIntent = new Intent(ChangeRoleUser.this, ViewUserDetail.class);
                        userDetailIntent.putExtra("UserName", adapter.getRef(position).getKey());
                        startActivity(userDetailIntent);
                    }
                });


            }
        };

        try{
            listUserView.setAdapter(adapter);
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

