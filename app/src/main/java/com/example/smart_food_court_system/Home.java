package com.example.smart_food_court_system;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import vn.momo.momo_partner.AppMoMoLib;
import vn.momo.momo_partner.MoMoParameterNameMap;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ProgressDialog mDialog;
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    Button logout,cancel, changePassword, changeProfile, btnRecharge;
    ListView listFoodView;
    FirebaseListAdapter adapter;
    TextView txtName, txtAccountBalance, txtEmail;
    CircleImageView circleImageView;
    EditText edtOldPassword, edtNewPassword, edtConfirmNewPassword, edtRechargeAmount;
    EditText edtName, edtEmailAddress, edtPhoneNumber;
    FloatingActionButton btnViewCart;
    DatabaseReference mDatabase;

    private String merchantName = Common.merchantName;
    private String merchantCode = Common.merchantCode;
    private String merchantNameLabel = Common.merchantNameLabel;
    private String description = "Recharge";
    private String amount = "0";
    private String fee = "0";
    int environment = 0;//developer default
    TextView tvMessage;
    int total_amount = 0;
    int total_fee = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDialog = new ProgressDialog(Home.this);
        mDialog.setMessage("Please wait...");
        mDialog.show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION
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
        UserDB.addValueEventListener(new ValueEventListener() {
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
            mDialog.dismiss();
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
        if (id == R.id.action_go_home) {
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
            /*
            else if (id == R.id.order_details_drawer) {
                Toast.makeText(getApplicationContext(), "No details found because you didn't order something...", Toast.LENGTH_SHORT).show();
            }
             */
            else if (id == R.id.submit_order) {
                Intent intent = new Intent(Home.this, OrderHistory.class);
                startActivity(intent);
            }
            else if(id == R.id.recharge){
                //Intent HomeToRecharge = new Intent(Home.this, Recharge.class);
                //startActivity(HomeToRecharge);
                openRecharge();
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
                Paper.book().destroy();
                builder.dismiss();
                Common.currentUser = new User();
                Home.super.onBackPressed();
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

    public void openRecharge() {

        final Dialog builder = new Dialog(this); // Context, this, etc.
        builder.setContentView(R.layout.dialog_recharge);
        builder.setTitle(R.string.dialog_popup);
        builder.show();
        btnRecharge = (Button) builder.findViewById(R.id.btnRecharge);
        cancel = (Button) builder.findViewById(R.id.dialog_cancel);
        edtRechargeAmount = (EditText)builder.findViewById(R.id.edtRechargeAmount);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Duy");
        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDiaglog = new ProgressDialog(Home.this);
                mDiaglog.setMessage("Please wait");
                mDiaglog.show();
                final String rechargeAmount = edtRechargeAmount.getText().toString();
                Log.e("Error", rechargeAmount);
                if(rechargeAmount.isEmpty()){
                    mDiaglog.dismiss();
                    Toast.makeText(Home.this, "You haven't type recharge amount", Toast.LENGTH_SHORT).show();
                }
                else if(rechargeAmount.equals("0")){
                    mDiaglog.dismiss();
                    Toast.makeText(Home.this, "Not a proper amount", Toast.LENGTH_SHORT).show();
                }
                else{
                    total_amount = Integer.parseInt(edtRechargeAmount.getText().toString());
                    requestPayment();
                }

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
    }

    private void requestPayment() {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
        if (edtRechargeAmount.getText().toString() != null && edtRechargeAmount.getText().toString().trim().length() != 0)
            amount = edtRechargeAmount.getText().toString().trim();

        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", total_amount); //Kiểu integer
        eventValue.put("orderId", "orderId123456789"); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", "Mã đơn hàng"); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", total_fee); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId",  merchantCode+"merchant_billId_"+System.currentTimeMillis());
        eventValue.put("partnerCode", merchantCode);
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "CGV Cresent Mall");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Special");
            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
            objExtraData.put("movie_format", "2D");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventValue.put("extraData", objExtraData.toString());

        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if(data != null) {
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    //tvMessage.setText("message: " + "Get token " + data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order

                        /*
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String currentBalance = dataSnapshot.child("User")
                                        .child(Common.currentUser.getUserName())
                                        .child("accountBalance").getValue().toString();
                                String newBalance = String.valueOf(Integer.parseInt(currentBalance) + total_amount);
                                mDatabase.child("User")
                                        .child(Common.currentUser.getUserName())
                                        .child("accountBalance").setValue(newBalance);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                         */
                    Toast.makeText(Home.this, "Recharge successfully", Toast.LENGTH_SHORT).show();

                    } else {
                        //tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
                    //tvMessage.setText("message: " + message);
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    //tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                } else {
                    //TOKEN FAIL
                    //tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                }
            } else {
                //tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
            }
        } else {
            //tvMessage.setText("message: " + this.getString(R.string.not_receive_info_err));
        }
    }

    private void searchFood(String searchText){
        mDialog.show();
        Query query = FirebaseDatabase.getInstance().getReference().child("Duy").child("Food").orderByKey().startAt(searchText).endAt(searchText+"\uf8ff");
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
            mDialog.dismiss();
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