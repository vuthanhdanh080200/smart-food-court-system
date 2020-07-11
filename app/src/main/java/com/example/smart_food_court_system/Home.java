package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    Button logout,cancel;

    ListView listFoodView;
    FirebaseListAdapter adapter;
    TextView txtName, txtAccountBalance, txtEmail;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        txtName = (TextView)headerView.findViewById(R.id.txtName);
        txtAccountBalance = (TextView)headerView.findViewById(R.id.txtAccountBalance);
        txtEmail = (TextView)headerView.findViewById(R.id.txtEmail);

        txtName.setText(Common.currentUser.getName());
        txtEmail.setText(Common.currentUser.getEmailAddress());
        DatabaseReference UserDB = FirebaseDatabase.getInstance().getReference("Duy/User")
                .child(Common.currentUser.getUserName());
        UserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String balance = dataSnapshot.child("accountBalance").getValue().toString();
                txtAccountBalance.setText("Account balance: " + balance + " VND");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Query query = FirebaseDatabase.getInstance().getReference().child("Duy").child("Food");
        listFoodView = (ListView) findViewById(R.id.lVFood);

        FirebaseListOptions<Food> options = new FirebaseListOptions.Builder<Food>()
                .setLayout(R.layout.food_item)
                .setQuery(query, Food.class)
                .build();

        adapter = new FirebaseListAdapter<Food>(options) {
            protected void populateView(@NonNull View view, @NonNull Food food, final int position) {
                TextView foodName = view.findViewById(R.id.txtFoodName);
                foodName.setText("" + food.getFoodName());
                ImageView imageFood = view.findViewById(R.id.imageFood);
                Picasso.with(getBaseContext())
                        .load("" + food.getFoodImage())
                        .into(imageFood);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent foodDetailIntent = new Intent(Home.this, ViewFoodDetail.class);
                        foodDetailIntent.putExtra("FoodName", adapter.getRef(position).getKey());
                        startActivity(foodDetailIntent);
                    }
                });
            }
        };

        // Now set the adapter with a given layout
        try{
            listFoodView.setAdapter(adapter);
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        /*
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtName.setText(Common.currentUser.getUserName());
        txtEmail.setText(Common.currentUser.getEmailAddress());
        */
        int id = item.getItemId();

        if(id == R.id.order_details_drawer ){

            Toast.makeText(getApplicationContext(),"No details found because you didn't order something...",Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.submit_order ){

            Toast.makeText(getApplicationContext(),"Sorry, You don't order anything...",Toast.LENGTH_SHORT).show();
        }

        else if(id == R.id.recharge){
            Intent HomeToRecharge = new Intent(Home.this, Recharge.class);
            startActivity(HomeToRecharge);
        }
        else if(id == R.id.view_cart){
            Intent HomeToCart = new Intent(Home.this, Cart.class);
            startActivity(HomeToCart);
        }
        else if(id == R.id.log_out ){
            openDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    public void openDialog() {

        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.dialogdesign);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        logout = (Button) builder.findViewById(R.id.dialog_ok);
        cancel = (Button) builder.findViewById(R.id.dialog_cancel);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent loginIntent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(loginIntent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"So you don't want to, Logout !!!",Toast.LENGTH_SHORT).show();
                builder.dismiss();
            }
        });
    }

}