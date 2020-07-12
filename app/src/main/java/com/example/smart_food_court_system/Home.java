package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.Food;
import com.example.smart_food_court_system.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    Button logout,cancel, changePassword, changeProfile;
    ListView listFoodView;
    FirebaseListAdapter adapter;
    TextView txtName, txtAccountBalance, txtEmail;
    CircleImageView circleImageView;
    EditText edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    EditText edtName, edtEmailAddress, edtPhoneNumber;
    FloatingActionButton btnViewCart;

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

        btnViewCart = (FloatingActionButton)findViewById(R.id.fab_view_cart_home);
        btnViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cart = new Intent(Home.this, Cart.class);
                startActivity(cart);
            }
        });
        DatabaseReference UserDB = FirebaseDatabase.getInstance().getReference("Duy/User")
                .child(Common.currentUser.getUserName());
        Log.e("Error", Common.currentUser.getUserName());
        UserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String balance = dataSnapshot.child("accountBalance").getValue().toString();
                txtAccountBalance.setText("Account balance: " + balance + " VND");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


        Query query = FirebaseDatabase.getInstance().getReference().child("Danh").child("Food");
        listFoodView = (ListView) findViewById(R.id.lVFood);

        FirebaseListOptions<Food> options = new FirebaseListOptions.Builder<Food>()
                .setLayout(R.layout.food_item)
                .setQuery(query, Food.class)
                .build();

        adapter = new FirebaseListAdapter<Food>(options) {
            protected void populateView(@NonNull View view, @NonNull Food food, final int position) {
                TextView foodName = view.findViewById(R.id.txtFoodName);
                foodName.setText(food.getFoodName());

                TextView foodPrice = view.findViewById(R.id.txtFoodPrice);
                foodPrice.setText(food.getFoodPrice()+" VND");

                ImageView imageFood = view.findViewById(R.id.imageFood);
                byte[] decodedString = Base64.decode(food.getFoodImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageFood.setImageBitmap(decodedByte);

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
    /*
    circleImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(Home.this, "Profile_image", Toast.LENGTH_SHORT).show();
        }
    });

     */
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
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFood(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                searchFood(newQuery);
                return false;
            }
        });

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
        else if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtName.setText(Common.currentUser.getName());
        txtEmail.setText(Common.currentUser.getEmailAddress());

        int id = item.getItemId();
        if (Common.isConnectedToInternet(getBaseContext())) {
            if (id == R.id.change_profile) {
                openChangeProfile();
            }
            else if (id == R.id.order_details_drawer) {
                Toast.makeText(getApplicationContext(), "No details found because you didn't order something...", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.submit_order) {
                Toast.makeText(getApplicationContext(), "Sorry, You don't order anything...", Toast.LENGTH_SHORT).show();
            }
            else if(id == R.id.recharge){
                Intent HomeToRecharge = new Intent(Home.this, Recharge.class);
                startActivity(HomeToRecharge);
            }
            else if(id == R.id.view_cart){
                Intent HomeToCart = new Intent(Home.this, Cart.class);
                startActivity(HomeToCart);
            }
            else if(id == R.id.change_password){
                openChangePassword();
            }
        }
        else{
            Toast.makeText(Home.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id == R.id.log_out ){
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
                Common.currentUser = new User();
                Home.super.onBackPressed();
                /*
                Intent loginIntent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(loginIntent);*/
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

    private void searchFood(String searchText){
        Query query = FirebaseDatabase.getInstance().getReference().child("Vuong").child("Food").orderByKey().startAt(searchText).endAt(searchText+"\uf8ff");
        listFoodView = (ListView) findViewById(R.id.lVFood);

        FirebaseListOptions<Food> options = new FirebaseListOptions.Builder<Food>()
                .setLayout(R.layout.food_item)
                .setQuery(query, Food.class)
                .build();

        adapter.stopListening();

        adapter = new FirebaseListAdapter<Food>(options) {
            protected void populateView(@NonNull View view, @NonNull Food food, final int position) {
                TextView foodName = view.findViewById(R.id.txtFoodName);
                foodName.setText(food.getFoodName());

                TextView foodPrice = view.findViewById(R.id.txtFoodPrice);
                foodPrice.setText(food.getFoodPrice()+" VND");

                ImageView imageFood = view.findViewById(R.id.imageFood);
                byte[] decodedString = Base64.decode(food.getFoodImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageFood.setImageBitmap(decodedByte);

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
        adapter.startListening();


        try{
            listFoodView.setAdapter(adapter);
        }catch(Exception e){
            Log.e("Error " ,""+ e.getMessage());
            //TO DO--------------------------------------------------------
            //Hiển thị lên màn hình giao diện đồ ăn hiện tại không sẵn sàng
        }
    }

    public void openChangePassword() {

        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.change_password);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        changePassword = (Button) builder.findViewById(R.id.dialog_change);
        cancel = (Button) builder.findViewById(R.id.dialog_cancel);
        edtOldPassword = (EditText) builder.findViewById(R.id.edtOldPassword);
        edtNewPassword = (EditText) builder.findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = (EditText) builder.findViewById(R.id.edtConfirmNewPassword);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference("Duy/User");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(Common.currentUser.getUserName()).getValue(User.class);
                        if (user.getPassword().equals(edtOldPassword.getText().toString())){
                            if(edtNewPassword.getText().toString().equals(edtConfirmNewPassword.getText().toString())){
                                Toast.makeText(Home.this, Common.changePasswordSuccessMessage, Toast.LENGTH_SHORT).show();
                                user.setPassword(edtNewPassword.getText().toString());
                                Common.currentUser.setPassword(edtNewPassword.getText().toString());
                                EditText password = (EditText)findViewById(R.id.edtPassword);
                                mDatabase.child(Common.currentUser.getUserName()).child("password").setValue(edtNewPassword.getText().toString());
                                builder.dismiss();
                            }
                            else{
                                Toast.makeText(Home.this, Common.confirmPasswordErrorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(Home.this, Common.currentPasswordIsNotCorrectErrorMessage, Toast.LENGTH_SHORT).show();
                        }
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

    public void openChangeProfile(){
        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.change_profile);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        changeProfile = (Button) builder.findViewById(R.id.dialog_change);
        cancel = (Button) builder.findViewById(R.id.dialog_cancel);
        edtName = (EditText) builder.findViewById(R.id.edtName);
        edtEmailAddress = (EditText) builder.findViewById(R.id.edtEmailAddress);
        edtPhoneNumber = (EditText) builder.findViewById(R.id.edtPhoneNumber);

        edtName.setText("" + Common.currentUser.getName());
        edtEmailAddress.setText("" + Common.currentUser.getEmailAddress());
        edtPhoneNumber.setText("" + Common.currentUser.getPhoneNumber());


        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference("Duy/User");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user = new User(
                                edtName.getText().toString(),
                                Common.currentUser.getUserName(),
                                Common.currentUser.getPassword(),
                                edtEmailAddress.getText().toString(),
                                edtPhoneNumber.getText().toString(),
                                Common.currentUser.getRole(),
                                Common.currentUser.getAccountBalance(),
                                Common.currentUser.getStall());

                        if(user.getName().isEmpty() || user.getUserName().isEmpty() || user.getPassword().isEmpty() ||
                                user.getEmailAddress().isEmpty() || user.getPhoneNumber().isEmpty()){
                                Toast.makeText(Home.this, Common.fillAllErrorMessage, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            boolean isEmailAddressExists = false, isPhoneNumberExists = false;
                            for(DataSnapshot item : dataSnapshot.getChildren()){
                                if(user.getEmailAddress().equals(item.child("emailAddress").getValue())){
                                    if(!user.getEmailAddress().equals(Common.currentUser.getEmailAddress())) {
                                        isEmailAddressExists = true;
                                        break;
                                    }
                                }
                            }
                            for(DataSnapshot item : dataSnapshot.getChildren()){
                                if(user.getPhoneNumber().equals(item.child("phoneNumber").getValue())){
                                    if(!user.getPhoneNumber().equals(Common.currentUser.getPhoneNumber())) {
                                        isPhoneNumberExists = true;
                                        break;
                                    }
                                }
                            }
                            if(isEmailAddressExists || isPhoneNumberExists){
                                if(!isPhoneNumberExists){
                                    Toast.makeText(Home.this, Common.emailAddressExistsErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                                else if(!isEmailAddressExists){
                                    Toast.makeText(Home.this, Common.phoneNumberExistsErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(Home.this, Common.emailAddressExistsErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                            //Add to Database
                            else {
                                mDatabase.child(Common.currentUser.getUserName()).setValue(user);
                                Common.currentUser = user;
                                Toast.makeText(Home.this, Common.updateInforSuccessMessage, Toast.LENGTH_SHORT).show();
                                builder.dismiss();
                            }
                        }

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
}