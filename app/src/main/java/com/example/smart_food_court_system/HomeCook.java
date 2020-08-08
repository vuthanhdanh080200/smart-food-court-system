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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.smart_food_court_system.common.Common;
import com.example.smart_food_court_system.model.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeCook extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    Button changeProfile, changePassword, logout, cancel;
    ListView listOrderView;
    FirebaseListAdapter adapter;
    TextView txtName, txtEmail, txtRole, txtStall;
    CircleImageView circleImageView;
    EditText edtOldPassword, edtNewPassword, edtConfirmNewPassword;
    EditText edtName, edtEmailAddress, edtPhoneNumber;
    DatabaseReference mDatabase, db;
    String orderId="";
    //
    Button btnAll, btnReady, btnCook, btnComplete;
    //

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_cook);

        Paper.init(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);

        View headerView = navigationView.getHeaderView(0);
        txtName = (TextView)headerView.findViewById(R.id.txtName);
        txtEmail = (TextView)headerView.findViewById(R.id.txtEmailAddress);
        txtRole = (TextView)headerView.findViewById(R.id.txtRole);
        txtStall = (TextView)headerView.findViewById(R.id.txtStall);

        txtName.setText(Common.currentUser.getName());
        txtEmail.setText(Common.currentUser.getEmailAddress());
        txtRole.setText(Common.currentUser.getRole());
        txtStall.setText(Common.currentUser.getStall());


        Query query = FirebaseDatabase.getInstance().getReference().child("Duy").child("Order");
        listOrderView = (ListView) findViewById(R.id.lVOrder);

        FirebaseListOptions<com.example.smart_food_court_system.model.Order> options = new FirebaseListOptions.Builder<com.example.smart_food_court_system.model.Order>()
                .setLayout(R.layout.order_management_item)
                .setQuery(query, com.example.smart_food_court_system.model.Order.class)
                .build();

        adapter = new FirebaseListAdapter<com.example.smart_food_court_system.model.Order>(options) {
            protected void populateView(@NonNull View view, @NonNull com.example.smart_food_court_system.model.Order order, final int position) {
                TextView userName = view.findViewById(R.id.txtUserName);
                userName.setText("User name: " + order.getUserName());
                TextView totalPrice = view.findViewById(R.id.txtTotalPrice);
                totalPrice.setText("Total: " + order.getTotal());
                TextView status = view.findViewById(R.id.txtStatus);
                if(order.getStatus().equals("ready " + order.getUserName())){
                    status.setText("Order status: Preparing for cooking");
                }
                else if(order.getStatus().equals("cook " + order.getUserName())){
                    status.setText("Order status: Food is being cooked");
                }
                else if(order.getStatus().equals("cook done " + order.getUserName())){
                    status.setText("Order status: Cook done, waiting for customer to get the food");
                }
                else if(order.getStatus().equals("complete " + order.getUserName())){
                    Log.e("ERRR", order.getUserName());
                    status.setText("Order status: Complete");
                }
                Button backStage = view.findViewById(R.id.btnBackStage);
                Button nextStage = view.findViewById(R.id.btnNextStage);

                backStage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderId = adapter.getRef(position).getKey();
                        db=FirebaseDatabase.getInstance().getReference("Duy/Order");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                com.example.smart_food_court_system.model.Order order=dataSnapshot.child(orderId).getValue(com.example.smart_food_court_system.model.Order.class);
                                if(order.getStatus().equals("ready "+order.getUserName())){
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("ready "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook done "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("complete "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook done "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                nextStage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderId = adapter.getRef(position).getKey();
                        db=FirebaseDatabase.getInstance().getReference("Duy/Order");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                com.example.smart_food_court_system.model.Order order=dataSnapshot.child(orderId).getValue(com.example.smart_food_court_system.model.Order.class);
                                if(order.getStatus().equals("ready "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook done "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook done "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("complete "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeCook.this, ViewOrderDetails.class);
                        intent.putExtra("userName", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };

        try{
            listOrderView.setAdapter(adapter);
        }catch(Exception e){
            Log.e("Error " ,""+ e.getMessage());
            //TO DO--------------------------------------------------------
            //Hiển thị lên màn hình giao diện đồ ăn hiện tại không sẵn sàng
        }

        //
        /*
        btnAll = (Button)findViewById(R.id.btnAll);
        btnReady = (Button)findViewById(R.id.btnReady);
        btnCook = (Button)findViewById(R.id.btnCook);
        btnComplete = (Button)findViewById(R.id.btnComplete);

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewAll();
            }
        });

        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewState("ready");
            }
        });

        btnCook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewState("cook ");
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewState("complete");
            }
        });
         */

        //


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
        getMenuInflater().inflate(R.menu.main_4, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeCook/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_go_home) {
            return true;
        }
        else if (id == R.id.action_ready) {
            viewAll();
        }
        else if (id == R.id.action_cook) {
            viewState("ready");
        }
        else if (id == R.id.action_cook_done) {
            viewState("cook ");
        }
        else if (id == R.id.action_complete) {
            viewState("complete");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmailAddress);
        DatabaseReference UserDB = FirebaseDatabase.getInstance().getReference("Duy/User")
                .child(Common.currentUser.getUserName());
        UserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                txtName.setText(user.getName());
                txtEmail.setText(user.getEmailAddress());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        int id = item.getItemId();
        if (Common.isConnectedToInternet(getBaseContext())) {
            if (id == R.id.change_profile) {
                openChangeProfile();
            }
            else if (id == R.id.change_password) {
                openChangePassword();
            }
            else if (id == R.id.order_management) {
                Toast.makeText(HomeCook.this, "You are in order management!", Toast.LENGTH_SHORT).show();
            }
            else if(id == R.id.food_management){
                Intent intent = new Intent(HomeCook.this, FoodManagement.class);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(HomeCook.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
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
                Paper.book().destroy();
                Common.currentUser = new User();
                HomeCook.super.onBackPressed();
                Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(loginIntent);
                builder.dismiss();
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
                                Toast.makeText(HomeCook.this, Common.changePasswordSuccessMessage, Toast.LENGTH_SHORT).show();
                                user.setPassword(edtNewPassword.getText().toString());
                                Common.currentUser.setPassword(edtNewPassword.getText().toString());
                                EditText password = (EditText)findViewById(R.id.edtPassword);
                                mDatabase.child(Common.currentUser.getUserName()).child("password").setValue(edtNewPassword.getText().toString());
                                builder.dismiss();
                            }
                            else{
                                Toast.makeText(HomeCook.this, Common.confirmPasswordErrorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(HomeCook.this, Common.currentPasswordIsNotCorrectErrorMessage, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(HomeCook.this, Common.fillAllErrorMessage, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(HomeCook.this, Common.emailAddressExistsErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                                else if(!isEmailAddressExists){
                                    Toast.makeText(HomeCook.this, Common.phoneNumberExistsErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(HomeCook.this, Common.emailAddressExistsErrorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                            //Add to Database
                            else {
                                mDatabase.child(Common.currentUser.getUserName()).setValue(user);
                                Common.currentUser = user;
                                Toast.makeText(HomeCook.this, Common.updateInforSuccessMessage, Toast.LENGTH_SHORT).show();
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


    private void viewAll() {
        adapter.stopListening();

        Query query = FirebaseDatabase.getInstance().getReference().child("Duy").child("Order");
        listOrderView = (ListView) findViewById(R.id.lVOrder);

        FirebaseListOptions<com.example.smart_food_court_system.model.Order> options = new FirebaseListOptions.Builder<com.example.smart_food_court_system.model.Order>()
                .setLayout(R.layout.order_management_item)
                .setQuery(query, com.example.smart_food_court_system.model.Order.class)
                .build();

        adapter = new FirebaseListAdapter<com.example.smart_food_court_system.model.Order>(options) {
            protected void populateView(@NonNull View view, @NonNull com.example.smart_food_court_system.model.Order order, final int position) {
                TextView userName = view.findViewById(R.id.txtUserName);
                userName.setText("User name: " + order.getUserName());
                TextView totalPrice = view.findViewById(R.id.txtTotalPrice);
                totalPrice.setText("Total: " + order.getTotal());
                TextView status = view.findViewById(R.id.txtStatus);
                if(order.getStatus().equals("ready " + order.getUserName())){
                    status.setText("Order status: Preparing for cooking");
                }
                else if(order.getStatus().equals("cook " + order.getUserName())){
                    status.setText("Order status: Food is being cooked");
                }
                else if(order.getStatus().equals("cook done " + order.getUserName())){
                    status.setText("Order status: Cook done, waiting for customer to get the food");
                }
                else if(order.getStatus().equals("complete " + order.getUserName())){
                    Log.e("ERRR", order.getUserName());
                    status.setText("Order status: Complete");
                }
                Button backStage = view.findViewById(R.id.btnBackStage);
                Button nextStage = view.findViewById(R.id.btnNextStage);

                backStage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderId = adapter.getRef(position).getKey();
                        db=FirebaseDatabase.getInstance().getReference("Duy/Order");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                com.example.smart_food_court_system.model.Order order=dataSnapshot.child(orderId).getValue(com.example.smart_food_court_system.model.Order.class);
                                if(order.getStatus().equals("ready "+order.getUserName())){
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("ready "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook done "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("complete "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook done "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                nextStage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderId = adapter.getRef(position).getKey();
                        db=FirebaseDatabase.getInstance().getReference("Duy/Order");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                com.example.smart_food_court_system.model.Order order=dataSnapshot.child(orderId).getValue(com.example.smart_food_court_system.model.Order.class);
                                if(order.getStatus().equals("ready "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook done "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook done "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("complete "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeCook.this, ViewOrderDetails.class);
                        intent.putExtra("userName", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
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
    //
    private void viewState(final String status) {
        adapter.stopListening();

        Query query = FirebaseDatabase.getInstance().getReference().child("Duy").child("Order").orderByChild("status").startAt(status).endAt(status+"\uf8ff");

        listOrderView = (ListView) findViewById(R.id.lVOrder);

        FirebaseListOptions<com.example.smart_food_court_system.model.Order> options = new FirebaseListOptions.Builder<com.example.smart_food_court_system.model.Order>()
                .setLayout(R.layout.order_management_item)
                .setQuery(query, com.example.smart_food_court_system.model.Order.class)
                .build();

        adapter = new FirebaseListAdapter<com.example.smart_food_court_system.model.Order>(options) {
            protected void populateView(@NonNull View view, @NonNull com.example.smart_food_court_system.model.Order order, final int position) {

                //
                TextView userName = view.findViewById(R.id.txtUserName);
                userName.setText("User name: " + order.getUserName());
                TextView totalPrice = view.findViewById(R.id.txtTotalPrice);
                totalPrice.setText("Total: " + order.getTotal());
                TextView status = view.findViewById(R.id.txtStatus);
                if(order.getStatus().equals("ready " + order.getUserName())){
                    status.setText("Order status: Preparing for cooking");
                }
                else if(order.getStatus().equals("cook " + order.getUserName())){
                    status.setText("Order status: Food is being cooked");
                }
                else if(order.getStatus().equals("cook done " + order.getUserName())){
                    status.setText("Order status: Cook done, waiting for customer to get the food");
                }
                else if(order.getStatus().equals("complete " + order.getUserName())){
                    Log.e("ERRR", order.getUserName());
                    status.setText("Order status: Complete");
                }
                Button backStage = view.findViewById(R.id.btnBackStage);
                Button nextStage = view.findViewById(R.id.btnNextStage);

                backStage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderId = adapter.getRef(position).getKey();
                        db=FirebaseDatabase.getInstance().getReference("Duy/Order");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                com.example.smart_food_court_system.model.Order order=dataSnapshot.child(orderId).getValue(com.example.smart_food_court_system.model.Order.class);
                                if(order.getStatus().equals("ready "+order.getUserName())){
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("ready "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook done "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("complete "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook done "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                nextStage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderId = adapter.getRef(position).getKey();
                        db=FirebaseDatabase.getInstance().getReference("Duy/Order");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                com.example.smart_food_court_system.model.Order order=dataSnapshot.child(orderId).getValue(com.example.smart_food_court_system.model.Order.class);
                                if(order.getStatus().equals("ready "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("cook done "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else if(order.getStatus().equals("cook done "+order.getUserName())){
                                    db.child(orderId).child("status").setValue("complete "+order.getUserName());
                                    Toast.makeText(HomeCook.this, "Successful", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(HomeCook.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeCook.this, ViewOrderDetails.class);
                        intent.putExtra("userName", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
                //
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